package com.example.recyclerviewkt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class ProfileAdapter(val profileList: ArrayList<Profiles>) : RecyclerView.Adapter<ProfileAdapter.CustomViewHolder>() {

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gender = itemView.findViewById<ImageView>(R.id.iv_profile)  // 성별
        val name   = itemView.findViewById<TextView>(R.id.tv_name)      // 이름
        val age    = itemView.findViewById<TextView>(R.id.tv_age)       // 나이
        val job    = itemView.findViewById<TextView>(R.id.tv_job)       // 직업
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        // view 연동은 끝남. but 클릭처리를 위해 apply 추가
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                // click을 했을때 이름 age job 텍스트 값 출력하고 싶음
                val curPos  : Int = adapterPosition  // 현재 click된 adapter의 포지션
                val profile : Profiles = profileList.get(curPos)
                Toast.makeText(parent.context, "이름 : ${profile.name}\n" +
                        "나이 : ${profile.age}" +
                        "\n직업 : ${profile.job}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onBindViewHolder(holder: ProfileAdapter.CustomViewHolder, position: Int) {
        // view에 대해서 안정적으로 모든 데이터를 매치해주는 곳
        holder.gender.setImageResource(profileList.get(position).gender)
        holder.name.text = profileList.get(position).name
        holder.age.text  = profileList.get(position).age.toString()     // Int형이라 toString()
        holder.job.text  = profileList.get(position).job
    }

    override fun getItemCount(): Int {
        // list들에 대한 총 개수를 적어줘야 함.
        return  profileList.size
    }


}