package com.example.cloneinstagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.cloneinstagram.R
import com.example.cloneinstagram.databinding.FragmentAlarmBinding
import com.example.cloneinstagram.databinding.FragmentDetailBinding
import com.example.cloneinstagram.navigation.model.AlarmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AlarmFragment:Fragment() {

   // private var _binding: FragmentAlarmBinding? = null
   // private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_alarm,container,false)
        view.alarmfragment_recyclerview.adapter = AlarmRecyclerviewAdapter()
        view.alarmfragment_recyclerview.layoutManger = LinearLayoutManager(activity)
        //_binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return view
    }


    inner class AlarmRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var alarmDTOList : ArrayList<AlarmDTO> = arrayListOf()

        init{
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid",uid).addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                alarmDTOList.clear()
                if(querySnapshot == null) return@addSnapshotListener
                for(snapshot in querySnapshot.documents){
                    alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate((R.layout.item_comment,parent,false))
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder.itemView
            FirebaseFirestore.getInstance().collection("profileImage").document(alarmDTOList[position].uid!!).get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val url = task.result!!["image"]
                    Glide.with(view.context).load(url).apply(RequestOptions.circleCrop()).into(view.commetviewitem_imageview_profile)
                }
            }
            when(alarmDTOList[position].kind){
                0->{
                    val str_0 = alarmDTOList[position].userId+getString(R.string.alarm_favorite)
                    view.commentviewitem_texview_profile.text = str_0
                }
                1->{
                    val str_0 = alarmDTOList[position].userId+" "+getString(R.string.alarm_comment)+"of"+alarmDTOList[position].message
                    view.commentviewitem_texview_profile.text = str_0
                }
                0->{
                    val str_0 = alarmDTOList[position].userId+""+getString(R.string.alarm_follow)
                    view.commentviewitem_texview_profile.text = str_0
                }

            }
            view.commentviewitem_textview_comment.visibility = View.INVISIBLE
        }

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

    }
}