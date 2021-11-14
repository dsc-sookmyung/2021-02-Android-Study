package com.example.layoutpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// 메인화면
class MainActivity : AppCompatActivity() {

    // 뷰가 실행되었을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 그릴 xml 뷰 파일을 연결 시켜준다. 즉, 설정한다.
        setContentView(R.layout.activity_main)
    }
}