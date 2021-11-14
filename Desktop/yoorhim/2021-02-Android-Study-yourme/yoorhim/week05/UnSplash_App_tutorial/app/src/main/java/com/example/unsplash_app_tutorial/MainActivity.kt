package com.example.unsplash_app_tutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.unsplash_app_tutorial.databinding.ActivityMainBinding
import com.example.unsplash_app_tutorial.retrofit.RetrofitManager
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import com.example.unsplash_app_tutorial.utils.RESPONSE_STATE
import com.example.unsplash_app_tutorial.utils.SEARCH_TYPE
import com.example.unsplash_app_tutorial.utils.onMyTextChanged


class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    private var currentSearchType: SEARCH_TYPE = SEARCH_TYPE.PHOTO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "MainActivity - onCreate() called")

        // 라디오 그룹 가져오기
        binding.searchTermRadioGroup.setOnCheckedChangeListener { _, checkedId ->

            // switch문
            when(checkedId) {
                R.id.photo_search_radio_btn -> {
                    Log.d(TAG, "사진검색 버튼 클릭!")
                    binding.searchTermTextLayout.hint = "사진검색"
                    binding.searchTermTextLayout.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_photo_library_24, resources.newTheme())
                    this.currentSearchType = SEARCH_TYPE.PHOTO
                }
                R.id.user_search_radio_btn -> {
                    Log.d(TAG, "사용자검색 버튼 클릭!")
                    binding.searchTermTextLayout.hint = "사용자검색"
                    binding.searchTermTextLayout.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_person_24, resources.newTheme())
                    this.currentSearchType = SEARCH_TYPE.USER
                }
            }
            Log.d(TAG, "MainActivity - OnCheckedChanged() called / currentSearchType : $currentSearchType")
        }

        // 텍스트가 변경이 되었을때
        binding.searchTermEditText.onMyTextChanged {
            // 입력된 글자가 하나라도 있다면
            if (it.toString().count() > 0) {
                // 검색 버튼을 보여준다.
                binding.frameLayout.frameSearchBtn.visibility = View.VISIBLE
                binding.searchTermTextLayout.helperText = " "

                // 스크롤뷰를 올린다.
                binding.mainScrollview.scrollTo(0, 200)
            } else {
                binding.frameLayout.frameSearchBtn.visibility = View.INVISIBLE
            }

            if (it.toString().count() == 12) {
                Log.d(TAG, "MainActivity - 에러 띄우기")
                Toast.makeText(this, "검색어는 12자까지 입력 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 버튼 클릭시
        binding.frameLayout.btnSearch.setOnClickListener {
            Log.d(TAG, "MainActivity - 검색 버튼이 클릭되었다. / currentSearchType : $currentSearchType")

            // 검색 api 호출
            RetrofitManager.instance.searchPhotos(searchTerm = binding.searchTermEditText.toString(), completion = {
                    responseState, responseBody ->

                when(responseState) {
                    RESPONSE_STATE.OKAY -> {
                        Log.d(TAG, "api 호출 성공 : $responseBody")
                    }
                    RESPONSE_STATE.FAIL -> {
                        Toast.makeText(this, "api 호출 에러입니다.", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "api 호출 실패 : $responseBody")
                    }
                }
            })

            this.handleSearchButtonUi()
        }

    } // onCreate

    private fun handleSearchButtonUi() {
        binding.frameLayout.btnProgress.visibility = View.VISIBLE
        binding.frameLayout.btnSearch.text = ""
        Handler().postDelayed({
            binding!!.frameLayout.btnProgress.visibility = View.INVISIBLE
            binding!!.frameLayout.btnSearch.text = "검색"
        }, 1500)
    }

}