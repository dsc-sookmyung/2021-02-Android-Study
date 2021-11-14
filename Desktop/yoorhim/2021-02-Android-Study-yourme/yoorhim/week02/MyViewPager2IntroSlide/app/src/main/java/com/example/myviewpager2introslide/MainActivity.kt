package com.example.myviewpager2introslide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.example.myviewpager2introslide.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // MainActivity 뷰바인딩
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    companion object {
        const val TAG: String = "log"
    }

    // 데이터 배열 선언
    private var pageItemList = ArrayList<PageItem>()
    private lateinit var myIntroPagerRecyclerAdapter: MyIntroPagerRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "MainActivity - onCreate() called")

        // Btn setOnClickListener
        binding.previousBtn.setOnClickListener {
            Log.d(TAG, "MainActivity - 이전 버튼 클릭")
            binding.myIntroViewPager.currentItem = binding.myIntroViewPager.currentItem - 1
        }
        binding.nextBtn.setOnClickListener {
            Log.d(TAG, "MainActivity - 다음 버튼 클릭")
            binding.myIntroViewPager.currentItem = binding.myIntroViewPager.currentItem + 1
        }


        // 데이터 배열을 준비
        pageItemList.add(
            PageItem(R.color.colorOrange, R.drawable.ic_pager_item_1, "안녕하세요!\n 개발하는 정대리입니다!")
        )
        pageItemList.add(
            PageItem(R.color.colorBlue, R.drawable.ic_pager_item_2, "구독, 좋아요 눌러주세요!")
        )
        pageItemList.add(
            PageItem(R.color.colorWhite, R.drawable.ic_pager_item_3, "알림설정 부탁드립니다!")
        )

        // Adapter instance 설정(메모리에 올리는 것)
        myIntroPagerRecyclerAdapter = MyIntroPagerRecyclerAdapter(pageItemList)

        binding.myIntroViewPager.apply {
            adapter = myIntroPagerRecyclerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.dotsIndicator.setViewPager2(this)
        }

        /*
        apply를 쓰지 않으면 아래의 내용을 반복해서 써야함

        binding.myIntroViewPager.adapter = myIntroPagerRecyclerAdapter
        binding.myIntroViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
         */

        //

    }
    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}