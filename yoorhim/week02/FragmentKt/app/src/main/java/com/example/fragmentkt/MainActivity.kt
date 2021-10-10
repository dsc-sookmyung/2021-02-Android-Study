package com.example.fragmentkt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fragmentkt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // MainActivity 뷰바인딩
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Fragment와 연결
        setFrag(0)

        binding.btnFragment1.setOnClickListener {
            setFrag(0)
        }
        binding.btnFragment2.setOnClickListener {
            setFrag(1)
        }
        binding.btnFragment3.setOnClickListener {
            setFrag(2)
        }
    }

    private fun setFrag(fragNum : Int) {
        // ft : fragment transaction
        val ft = supportFragmentManager.beginTransaction()
        when(fragNum) { // switch 문과 유사
            0 -> {
                ft.replace(R.id.main_frame, Fragment1()).commit()   // commit : 저장 개념
            }
            1 -> {
                ft.replace(R.id.main_frame, Fragment2()).commit()
            }
            2 -> {
                ft.replace(R.id.main_frame, Fragment3()).commit()
            }
        }
    }

    // 액티비티가 파괴될 때
    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}