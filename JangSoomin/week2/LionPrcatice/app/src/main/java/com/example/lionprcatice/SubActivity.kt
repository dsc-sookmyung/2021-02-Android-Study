package com.example.lionprcatice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lionprcatice.databinding.ActivityMainBinding
import com.example.lionprcatice.databinding.ActivitySubBinding

class SubActivity : AppCompatActivity() {
    private var mBinding: ActivitySubBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("msg")){
            binding.textView.text= intent.getStringExtra("msg")
        }

        val profileList = arrayListOf(
            Profiles(R.drawable.cloud,"구름1",22,"안드로이드 앱 개발자"),
            Profiles(R.drawable.sun,"해2",21,"아이폰 앱 개발자"),
            Profiles(R.drawable.star,"별3",20,"알고리즘 개발자"),
            Profiles(R.drawable.moon,"달4",24,"딥러닝 개발자"),
            Profiles(R.drawable.moon,"달5",23,"웹 개발자"),
            Profiles(R.drawable.star,"별6",23,"유니티 개발자"),
            Profiles(R.drawable.sun,"해7",25,"시스템 개발자"),
            Profiles(R.drawable.sun,"해8",26,"클라우드 개발자"),
            Profiles(R.drawable.cloud,"구름9",27,"서버 개발자"),
            Profiles(R.drawable.star,"별10",22,"OS 개발자"),
        )
        binding.rvProfile.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvProfile.setHasFixedSize(true)
        binding.rvProfile.adapter = ProfileAdapter(profileList)

    }
    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}