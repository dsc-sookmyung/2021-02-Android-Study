package com.example.intentkt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.intentkt.databinding.ActivityMainBinding
import com.example.intentkt.databinding.ActivitySubBinding

class SubActivity : AppCompatActivity() {
    // SubActivity 뷰바인딩
    private var mBinding: ActivitySubBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("msg")) {
            binding.tvGetMsg.text = intent.getStringExtra("msg")    // 서브 액티비티에 존재하는 텍스트뷰에다가 Helloworld가 넘겨져 옴.
        }
    }

    // 액티비티가 파괴될 때
    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}