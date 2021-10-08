package com.example.lionprcatice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lionprcatice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAccnt.setOnClickListener{
            val intent = Intent(this,SubActivity::class.java)
            intent.putExtra("msg",binding.btnAccnt.text.toString())
            startActivity(intent)
        }

        binding.btncont.setOnClickListener{
            val intent = Intent(this,Sub2Activity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

}