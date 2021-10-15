package com.example.myviewpager2introslide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myviewpager2introslide.databinding.LayoutIntroPagerItemBinding

class MyIntroPagerRecyclerAdapter(private var pageList: ArrayList<PageItem>) : RecyclerView.Adapter<MyPagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPagerViewHolder {
        val binding = LayoutIntroPagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyPagerViewHolder, position: Int) {
        holder.bindWithView(pageList[position])
    }

    override fun getItemCount(): Int {
        // 데이터와 뷰를 묶는다
        return pageList.size
    }


}