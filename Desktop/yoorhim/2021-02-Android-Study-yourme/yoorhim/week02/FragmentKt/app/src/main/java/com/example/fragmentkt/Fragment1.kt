package com.example.fragmentkt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class Fragment1 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // frag1.xml과 연결하는 코드 (SetContentView와 유사함)
        val view = inflater.inflate(R.layout.frag1, container, false)
        return view // 만들어준 view를 리턴
    }
}