package com.example.fragmentkt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class Fragment2 : Fragment() {
    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?,  savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.frag2,container,false) //setContentView와 동일한 기능

        return view
    }
}