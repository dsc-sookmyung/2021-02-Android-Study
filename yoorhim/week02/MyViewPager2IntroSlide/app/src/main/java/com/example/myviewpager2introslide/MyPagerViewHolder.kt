package com.example.myviewpager2introslide

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.myviewpager2introslide.databinding.LayoutIntroPagerItemBinding

class MyPagerViewHolder(private val binding: LayoutIntroPagerItemBinding) : RecyclerView.ViewHolder(binding.root) {

    private val itemImage   : ImageView    = binding.pagerItemImage
    private val itemContent : TextView     = binding.pagerItemText
    private val itemBg      : LinearLayout = binding.pagerItemBg

    fun bindWithView(pageItem: PageItem) {
        itemImage.setImageResource(pageItem.imageSrc)
        itemContent.text = pageItem.content

        if(pageItem.bgColor != R.color.colorWhite) {
            itemContent.setTextColor(Color.WHITE)
        }

        itemBg.setBackgroundResource(pageItem.bgColor)
    }

}