package com.example.cloneinstagram.navigation

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cloneinstagram.LoginActivity
import com.example.cloneinstagram.MainActivity
import com.example.cloneinstagram.R
import com.example.cloneinstagram.databinding.ActivityMainBinding
import com.example.cloneinstagram.databinding.FragmentUserBinding
import com.example.cloneinstagram.navigation.model.AlarmDTO
import com.example.cloneinstagram.navigation.model.ContentDTO
import com.example.cloneinstagram.navigation.model.FollowDTO
import com.example.cloneinstagram.navigation.util.FcmPush
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
//import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment:Fragment() {
    var fragmentView : View?=null
    var firestore : FirebaseFirestore?=null
    var uid:String?=null
    var auth:FirebaseAuth?=null
    var currentUserUid : String?=null

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private var _binding2: ActivityMainBinding? = null
    private val binding2 get() = _binding2!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       // val binding = FragmentUserBinding.inflate(inflater, container, false)
       // val binding2 = ActivityMainBinding.inflate(inflater,container,false)
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        //fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid

        if(uid==currentUserUid){
            binding.accountBtnFollowSignout.text= getString(R.string.signout)
            binding.accountBtnFollowSignout.setOnClickListener{
                activity?.finish()
                startActivity(Intent(activity,LoginActivity::class.java))
                auth?.signOut()
            }
        }else{
            binding.accountBtnFollowSignout.text = getString(R.string.follow)
            binding2.toolbarUsername.text=arguments?.getString("userId")
            binding2.toolbarBtnBack.setOnClickListener{
                binding2.bottomNavigation.selectedItemId = R.id.action_home
                binding2.toolbarTitleImage.visibility=View.VISIBLE
                binding2.toolbarUsername.visibility = View.GONE
                binding2.toolbarBtnBack.visibility = View.GONE
            }
            binding2.toolbarTitleImage.visibility=View.GONE
            binding2.toolbarUsername.visibility = View.VISIBLE
            binding2.toolbarBtnBack.visibility = View.VISIBLE
            binding.accountBtnFollowSignout.setOnClickListener{
                requestFollow()
            }

        }
        binding.accountRecyclerview.adapter = UserFragmentRecyclerViewAdapter()
        binding.accountRecyclerview.layoutManager = GridLayoutManager(activity,3)

        binding.accountIvProfile.setOnClickListener{
            var photoPickIntent =Intent(Intent.ACTION_PICK)
            photoPickIntent.type="image/*"
            launcher.launch(photoPickIntent)
        }
        getProfileImage()
        getFollowerAndFollowing()
        return binding.root
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            var imageUri = result.data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)    // uid와 맞는 유저의 프로필 이미지 참조 정보
            storageRef.putFile(imageUri!!)
                .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->                                               // 프로필 이미지 업로드
                    return@continueWithTask storageRef.downloadUrl
                }.addOnSuccessListener { uri ->
                    var map = HashMap<String, Any>()
                    map["image"] = uri.toString()
                    FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)           // 바꾼 이미지의 주소 저장
                }
        }
    }

    fun requestFollow(){
        var tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO!!.followingCount = 1
                followDTO!!.followings[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction
            }
            if (followDTO.followings.containsKey(uid)){
                //It remove following third person when a third person follow me
                followDTO?.followingCount = followDTO?.followingCount - 1
                followDTO?.followers?.remove(uid)
            } else{
                followDTO?.followingCount = followDTO?.followingCount + 1
                followDTO?.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }
        //Save data to third person
        var tsDocFollower = firestore?.collection("users")?.document(uid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid!!)
                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            if (followDTO!!.followers.containsKey(currentUserUid)) {
                //It cancel my follower when I follow a third person
                followDTO!!.followerCount -= 1
                followDTO!!.followers.remove(currentUserUid)
            } else {
                //It add my follower when I don't follow a third person
                followDTO!!.followerCount += 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid!!)
            }
            transaction.set(tsDocFollower,followDTO!!)
            return@runTransaction
        }
    }

    fun followerAlarm(destinationUid:String){
        var alarmDTO = AlarmDTO()
        alarmDTO.destinaionUid=destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.kind=2
        alarmDTO.timestamp=System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarm").document().set(alarmDTO)

        var message = auth?.currentUser?.email+getString(R.string.alarm_follow)
        FcmPush.instance.sendMessage(destinationUid,"cloneinstagram",message)
    }





    fun getProfileImage(){
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot == null) return@addSnapshotListener
            if (documentSnapshot.data != null) {
                var url = documentSnapshot?.data!!["image"]
                Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop()).into(binding.accountIvProfile!!)
            }
        }

    }
    fun getFollowerAndFollowing(){

        firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { value, error ->
            if(value == null) return@addSnapshotListener

            var followDTO = value.toObject(FollowDTO::class.java)
            if (followDTO?.followingCount != null) {
                binding.accountTvFollowingCount?.text = followDTO?.followingCount?.toString()
            }
            if (followDTO?.followerCount != null) {
                binding.accountTvFollowerCount?.text = followDTO?.followerCount?.toString()
                if (followDTO?.followers?.containsKey(currentUserUid)) {
                    binding.accountBtnFollowSignout?.text = getString(R.string.follow_cancel)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        binding.accountBtnFollowSignout?.background?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(R.color.colorLightGray, BlendModeCompat.MULTIPLY)
                    } else {
                        @Suppress("DEPRECATION")
                        binding.accountBtnFollowSignout?.background?.setColorFilter(R.color.colorLightGray.toInt(), PorterDuff.Mode.MULTIPLY)
                    }
                } else {
                    if (uid != currentUserUid) {
                        binding.accountBtnFollowSignout?.text = getString(R.string.follow)
                        binding.accountBtnFollowSignout?.background?.colorFilter = null
                    }

                }
            }
        }

    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        init {
            firestore?.collection("images")?.whereEqualTo("uid", uid)?.addSnapshotListener { value, error ->
                if(value == null) return@addSnapshotListener
                for (snapshot in value.documents) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                binding.accountTvPostCount?.text = contentDTOs.size.toString()
                notifyDataSetChanged()

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels/3
            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)
        }
        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _binding2=null
    }


}