package com.example.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viewpager.databinding.LayoutPagerItemBinding

class PagerRecyclerAdapter(private var pageList: ArrayList<PageItem>) :RecyclerView.Adapter<PagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        // 뷰 바인딩 적용해주세요
        val binding = LayoutPagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pageList.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {

        // bind view with data
        holder.bindWithView(pageList[position])
    }
}