package com.example.hawlinstargram.navigation

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.jinstargram.LoginActivity
import com.example.jinstargram.MainActivity
import com.example.jinstargram.R
import com.example.jinstargram.navigation.model.AlarmDTO
import com.example.jinstargram.navigation.model.ContentDTO
import com.example.jinstargram.navigation.model.FollowDTO
import com.example.jinstargram.util.FcmPush
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment:Fragment() {
    var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var auth : FirebaseAuth? =null
    var currentUserUid : String? = null
    companion object{
        var PICK_PROFILE_FROM_ALBUM=10
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)
        uid=arguments?.getString("destinationUid")
        firestore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        currentUserUid=auth?.currentUser?.uid

        if(uid == currentUserUid){
            //mypage
            fragmentView?.account_btn_follow_signout?.text=getString(R.string.signout)
            fragmentView?.account_btn_follow_signout?.setOnClickListener{
                activity?.finish()
                startActivity(Intent(activity, LoginActivity::class.java))
                auth?.signOut()
            }
        }else{
            //other userpage
            fragmentView?.account_btn_follow_signout?.text=getString(R.string.follow)
            var mainactivity=(activity as MainActivity)
            mainactivity?.toolbar_username?.text=arguments?.getString("userId")
            mainactivity?.toolbar_btn_back?.setOnClickListener{
                mainactivity.bottom_navigation.selectedItemId=R.id.action_home
            }
            mainactivity?.toolbar_title_image?.visibility=View.GONE
            mainactivity?.toolbar_username?.visibility=View.VISIBLE
            mainactivity?.toolbar_btn_back?.visibility=View.VISIBLE
            fragmentView?.account_btn_follow_signout?.setOnClickListener{
                requestFollow()
            }
        }

        fragmentView?.account_recyclerview?.adapter=UserFragmentRecyclerViewAdapter()
        fragmentView?.account_recyclerview?.layoutManager=GridLayoutManager(activity,3)

        fragmentView?.account_iv_profile?.setOnClickListener{
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type="image/*"
            activity?.startActivityForResult(photoPickerIntent,PICK_PROFILE_FROM_ALBUM)

        }
        getProfileImage()
        getFollowerAndFollowing()
        return fragmentView
    }

    fun getFollowerAndFollowing(){
        firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { value, error ->
            if(value == null) return@addSnapshotListener

            var followDTO = value.toObject(FollowDTO::class.java)
            if (followDTO?.followingCount != null) {
                fragmentView?.account_tv_following_count?.text = followDTO?.followingCount?.toString()
            }
            if (followDTO?.followerCount != null) {
                fragmentView?.account_tv_follower_count?.text = followDTO?.followerCount?.toString()
                if (followDTO?.followers?.containsKey(currentUserUid)) {
                    fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow_cancel)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        fragmentView?.account_btn_follow_signout?.background?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(R.color.colorLightGray, BlendModeCompat.MULTIPLY)
                    } else {
                        @Suppress("DEPRECATION")
                        fragmentView?.account_btn_follow_signout?.background?.setColorFilter(R.color.colorLightGray.toInt(), PorterDuff.Mode.MULTIPLY)
                    }
                } else {
                    if (uid != currentUserUid) {
                        fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
                        fragmentView?.account_btn_follow_signout?.background?.colorFilter = null
                    }

                }
            }
        }
    }
    fun requestFollow(){
        //save data to my account
        var tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if (followDTO == null){
                followDTO = FollowDTO()
                followDTO!!.followingCount = 1
                followDTO!!.followers[uid!!]=true

                transaction.set(tsDocFollowing,followDTO)
                return@runTransaction
            }
            if (followDTO.followings.containsKey(uid)){
                //it remove following third person when a third person follow me
                followDTO?.followingCount=followDTO?.followingCount -1
                followDTO?.followers?.remove(uid)
            }else{
                //it add following third person when a third person do not follow me
                followDTO?.followingCount=followDTO?.followingCount +1
                followDTO?.followers[uid!!]=true
            }
            transaction.set(tsDocFollowing,followDTO)
            return@runTransaction
        }
        //save data to third person
        var tsDocFollower = firestore?.collection("users")?.document(uid!!)
        firestore?.runTransaction { transaction ->
            var followDTO=transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO= FollowDTO()
                followDTO!!.followerCount=1
                followDTO!!.followers[currentUserUid!!]=true
                followerAlarm(uid!!)
                transaction.set(tsDocFollower,followDTO!!)
                return@runTransaction
            }
            if(followDTO!!.followers.containsKey(currentUserUid)){
                //it cancel my follower when i follow a third person
                followDTO!!.followerCount=followDTO!!.followerCount-1
                followDTO!!.followers.remove(currentUserUid)
            }else{
                //it add my follower when i don't follow a third person
                followDTO!!.followerCount=followDTO!!.followerCount+1
                followDTO!!.followers[currentUserUid!!]=true
                followerAlarm(uid!!)
            }
            transaction.set(tsDocFollower,followDTO!!)
            return@runTransaction
        }
    }
    fun followerAlarm(destinationUid : String){
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid=destinationUid
        alarmDTO.userId=auth?.currentUser?.email
        alarmDTO.uid=auth?.currentUser?.uid
        alarmDTO.kind=2
        alarmDTO.timestamp=System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message=auth?.currentUser?.email + getString(R.string.alarm_follow)
        FcmPush.instance.sendMessage(destinationUid,"Jinstargram",message)
    }
    fun getProfileImage(){
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener{documentSnapshot,firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
            if (documentSnapshot.data != null){
                var url = documentSnapshot?.data!!["image"]
                Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop()).into(fragmentView?.account_iv_profile!!)
            }
        }
    }
    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        init{
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener{querySnapshot,firebaseFirestoreException ->
                if(querySnapshot == null) return@addSnapshotListener

                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                fragmentView?.account_tv_post_count?.text=contentDTOs.size.toString()
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels /3
            var imageview = ImageView(p0.context)
            imageview.layoutParams=LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var imageview=(p0 as CustomViewHolder).imageview
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
        }
    }
}