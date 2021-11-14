package com.example.cloneinstagram.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cloneinstagram.R
import com.example.cloneinstagram.databinding.ActivityLoginBinding
import com.example.cloneinstagram.databinding.FragmentDetailBinding
import com.example.cloneinstagram.databinding.FragmentUserBinding
import com.example.cloneinstagram.databinding.ItemDetailBinding
import com.example.cloneinstagram.navigation.model.AlarmDTO
import com.example.cloneinstagram.navigation.model.ContentDTO
import com.example.cloneinstagram.navigation.util.FcmPush
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailViewFragment:Fragment() {

    var uid : String ?= null
    var firestore : FirebaseFirestore? = null

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var _binding2: ItemDetailBinding? = null
    private val binding2 get() = _binding2!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       // var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail,container,false)
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        _binding2 = ItemDetailBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        binding.detailviewfragmentRecyclerview.adapter = DetailViewRecyclerViewAdapter()
        binding.detailviewfragmentRecyclerview.layoutManager = LinearLayoutManager(activity)

        return binding.root
    }
    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {

            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                contentDTOs.clear()
                contentUidList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentDTO::class.java)

                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }

                notifyDataSetChanged()
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
          //  var viewHolder = (holder as CustomViewHolder).itemView

            binding2.detailviewitemProfileTextview.text = contentDTOs!![position].userId
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(binding2.detailviewitemImageviewContent)
            binding2.detailviewitemExplainTextview.text = contentDTOs!![position].explain
            binding2.detailviewitemFavoriteconuterTextview.text = "Likes " + contentDTOs!![position].favoriteCount
            binding2.detailviewitemFavoriteImageview.setOnClickListener{
                favoriteEvent(position)
            }

            if (contentDTOs!![position].favorites.containsKey(uid)) {
                binding2.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_favorite)

            } else {
                binding2.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_favorite_border)
            }

            binding2.detailviewitemProfileImage.setOnClickListener{
                var fragment = UserFragment()
                var bundle = Bundle()

                bundle.putString("destinationUid", contentDTOs[position].uid)
                bundle.putString("userId", contentDTOs[position].userId)
                fragment.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content, fragment)?.commit()
            }

            binding2.detailviewitemCommentImageview.setOnClickListener { v ->
                var intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid",contentDTOs[position].uid)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }


        fun favoriteEvent(position : Int) {

            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])

            firestore?.runTransaction { transaction ->
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)){
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)
                } else {
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                    favoriteAlarm(contentDTOs[position].uid!!)
                }
                transaction.set(tsDoc, contentDTO)
            }
        }

        fun favoriteAlarm(destinationUid : String){
            var alarmDTO = AlarmDTO()
            alarmDTO.destinaionUid=destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.kind=0
            alarmDTO.timestamp=System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("alarm").document().set(alarmDTO)

            var message = FirebaseAuth.getInstance()?.currentUser?.email+ getString(R.string.alarm_favorite)
            FcmPush.instance.sendMessage(destinationUid,"cloneinstagram",message)

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _binding2 =null
    }


}