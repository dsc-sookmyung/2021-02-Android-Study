package com.example.viewpager

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.viewpager.databinding.LayoutPagerItemBinding

class PagerViewHolder( private val binding: LayoutPagerItemBinding) :RecyclerView.ViewHolder(binding.root) {
    private val itemImage : ImageView = binding.pagerItemImage
    private val itemContent : TextView = binding.pagerItemText
    private val itemBg : LinearLayout = binding.pagerItemBg

    fun bindWithView(pageItem: PageItem) {
        itemImage.setImageResource(pageItem.imageSrc)
        itemContent.text = pageItem.content

        if (pageItem.bgColor != Color.WHITE) {
            itemContent.setTextColor(Color.WHITE)
        }

        itemBg.setBackgroundColor(pageItem.bgColor)
    }




}