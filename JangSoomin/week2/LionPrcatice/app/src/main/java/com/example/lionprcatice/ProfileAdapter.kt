package com.example.lionprcatice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ProfileAdapter (val profileList: ArrayList<Profiles>) : RecyclerView.Adapter<ProfileAdapter.CustomViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ProfileAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                var curPos : Int = adapterPosition
                var profile: Profiles = profileList.get(curPos)
                Toast.makeText(parent.context, "name : ${profile.name},\n age : ${profile.age},\n job : ${profile.job}", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onBindViewHolder(holder: ProfileAdapter.CustomViewHolder, position: Int) {
        holder.type.setImageResource(profileList.get(position).type)
        holder.name.text= profileList.get(position).name
        holder.age.text= profileList.get(position).age.toString()
        holder.job.text= profileList.get(position).job
    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    class CustomViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        val type = itemView.findViewById<ImageView>(R.id.ivProfile)
        val name = itemView.findViewById<TextView>(R.id.tvName)
        val age = itemView.findViewById<TextView>(R.id.tvAge)
        val job = itemView.findViewById<TextView>(R.id.tvJob)
    }

}