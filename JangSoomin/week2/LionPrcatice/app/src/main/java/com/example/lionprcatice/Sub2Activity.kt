package com.example.lionprcatice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lionprcatice.databinding.ActivitySub2Binding
import com.example.lionprcatice.databinding.ActivitySubBinding

class Sub2Activity : AppCompatActivity() {

    private var mBinding: ActivitySub2Binding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySub2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFrg1.setOnClickListener{
            setFrag(0)
        }
        binding.btnFrg2.setOnClickListener{
            setFrag(1)
        }
        binding.btnFrg3.setOnClickListener{
            setFrag(2)
        }
    }

    private fun setFrag(fragNum : Int){
        val ft = supportFragmentManager.beginTransaction()
        when(fragNum)
        {
            0 -> {
                ft.replace(R.id.mainFrame, Fragment1()).commit()
            }
            1 -> {
                ft.replace(R.id.mainFrame, Fragment2()).commit()
            }
            2 -> {
                ft.replace(R.id.mainFrame, Fragment3()).commit()
            }
        }
    }



    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }


}