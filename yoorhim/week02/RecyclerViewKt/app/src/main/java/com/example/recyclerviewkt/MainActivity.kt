package com.example.recyclerviewkt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerviewkt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // MainActivity 뷰바인딩
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profilelist = arrayListOf(
            Profiles(R.drawable.man, "이다니", 2, "안드로이드 앱 개발자"),
            Profiles(R.drawable.woman, "메아리", 12, "아이폰 앱 개발자"),
            Profiles(R.drawable.man, "김개땅", 3, "리액트 앱 개발자"),
            Profiles(R.drawable.woman, "박구름", 2, "플러터 앱 개발자"),
            Profiles(R.drawable.man, "박덩쿨", 4, "유니티 앱 개발자"),
            Profiles(R.drawable.woman, "순돌이", 5, "알고리즘 앱 개발자"),
            Profiles(R.drawable.woman, "이소금", 10, "웹 앱 개발자"),
            Profiles(R.drawable.man, "양쵸파", 12, "하이브리드 앱 개발자"),
            Profiles(R.drawable.woman, "김초코", 9, "그냥 앱 개발자"),
            Profiles(R.drawable.man, "고양이", 8, "졸린 앱 개발자")
        )

        binding.rvProfile.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvProfile.setHasFixedSize(true)

        binding.rvProfile.adapter = ProfileAdapter(profilelist)

    }

    // 액티비티가 파괴될 때
    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}