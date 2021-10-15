package com.example.viewpager2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        const val TAG:String="로그"
    }

    //데이터 배열 선언
    private var pageItemList=ArrayList<PageItem>()
    private lateinit var myIntropPageRecyclerAdapter: MyIntropPageRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG,"MainActivity=onCreate() called")

        previous_btn.setOnClickListener {
            Log.d(TAG, "MainActivity - 이전 버튼 클릭")

            my_intro_view_pager.currentItem = my_intro_view_pager.currentItem - 1
        }

        next_btn.setOnClickListener {
            Log.d(TAG, "MainActivity - 다음 버튼 클릭")
            my_intro_view_pager.currentItem = my_intro_view_pager.currentItem + 1
        }

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        //데이터 배열 준비
        pageItemList.add(PageItem(R.color.colorOrange,R.drawable.ic_pager_item_1,"안녕하세요.\n 안드로이드를 공부중입니다."))
        pageItemList.add(PageItem(R.color.colorBlue,R.drawable.ic_pager_item_2,"안드로이드는 재미있다."))
        pageItemList.add(PageItem(R.color.colorWhite,R.drawable.ic_pager_item_3,"하지만, 조금 어려운 부분이 있다."))

        //어댑터 인스턴스 생성
        myIntropPageRecyclerAdapter= MyIntropPageRecyclerAdapter(pageItemList)

        my_intro_view_pager.apply{
            adapter=myIntropPageRecyclerAdapter
            orientation=ViewPager2.ORIENTATION_HORIZONTAL
            dots_indicator.setViewPager2(this)

        }

        //my_intro_view_pager.adapter=myIntropPageRecyclerAdapter
        //my_intro_view_pager.orientation= ViewPager2.ORIENTATION_HORIZONTAL
    }
}