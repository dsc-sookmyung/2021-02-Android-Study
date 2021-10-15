package com.example.lionprcatice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lionprcatice.databinding.Frag3Binding

class Fragment3 : Fragment() {
    private var mBinding: Frag3Binding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = Frag3Binding.inflate(inflater,container,false)
        return binding.root
    }
    
    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

}