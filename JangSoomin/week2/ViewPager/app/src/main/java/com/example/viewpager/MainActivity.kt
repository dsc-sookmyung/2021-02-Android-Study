package com.example.viewpager

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.example.viewpager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!


    companion object {
        const val TAG: String = "log"
    }

    private var pageItemList = ArrayList<PageItem>()
    private lateinit var pagerrecycleradapter: PagerRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.previousBtn.setOnClickListener {
            Log.d(TAG, "MainActivity - click previous button")

            binding.myIntroViewPager.currentItem--
        }

        binding.nextBtn.setOnClickListener {
            Log.d(TAG, "MainActivity - click next button")

            binding.myIntroViewPager.currentItem++
        }
        Log.d(TAG, "MainActivity - onCreate() called")

        pageItemList.add(PageItem(Color.BLACK, R.drawable.ic_pager_item_1, "안녕하세요!"))
        pageItemList.add(PageItem(Color.CYAN, R.drawable.ic_pager_item_2, "Hello"))
        pageItemList.add(PageItem(Color.WHITE, R.drawable.ic_pager_item_3, "World!"))

        pagerrecycleradapter = PagerRecyclerAdapter(pageItemList)

        binding.myIntroViewPager.apply {
            adapter = pagerrecycleradapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.dotsIndicator.setViewPager2(this)
        }


    }
    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }


}