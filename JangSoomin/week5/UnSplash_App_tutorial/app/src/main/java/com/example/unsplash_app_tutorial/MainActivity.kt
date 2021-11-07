package com.example.unsplash_app_tutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.unsplash_app_tutorial.databinding.ActivityMainBinding
import com.example.unsplash_app_tutorial.retrofit.RetrofitManager
import com.example.unsplash_app_tutorial.utils.Constants
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import com.example.unsplash_app_tutorial.utils.RESPONSE_STATE
import com.example.unsplash_app_tutorial.utils.SEARCH_TYPE
import com.example.unsplash_app_tutorial.utils.onMyTextChange


class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    private var currentSearchType: SEARCH_TYPE = SEARCH_TYPE.PHOTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(Constants.TAG,"MainActivity-onCreate() called")

        //라디오 그룹 가져오기
        binding.searchTermRadioGroup.setOnCheckedChangeListener{_,checkedId->
            when(checkedId){
                R.id.photo_search_radio_btn->{
                    Log.d(TAG,"사진 검색 버튼 클릭")
                    binding.searchTermTextLayout.hint="사진검색"
                    binding.searchTermTextLayout.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_photo_library_24,resources.newTheme())
                    this.currentSearchType = SEARCH_TYPE.PHOTO
                }
                R.id.user_search_radio_btn->{
                    Log.d(TAG,"사용자 검색 버튼 클릭")
                    binding.searchTermTextLayout.hint="사용자검색"
                    binding.searchTermTextLayout.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_person_24,resources.newTheme())
                    this.currentSearchType = SEARCH_TYPE.USER
                }
            }
            Log.d(TAG,"MainActivity-onCreate() called / currentSearchType : $currentSearchType")
        }

        binding.searchTermEditText.onMyTextChange {
            if(it.toString().count() > 0){
                binding.searchBtn.frameSearchBtn.visibility = View.VISIBLE
                binding.mainScrollview.scrollTo(0,200)
                binding.searchTermTextLayout.helperText = " "
            }else{
                binding.searchBtn.frameSearchBtn.visibility = View.INVISIBLE
            }
            if(it.toString().count() ==12){
                Log.d(TAG,"MainActivity - 에러 띄우기")
                Toast.makeText(this,"검색어는 12자 까지만 입력 가능합니다.",Toast.LENGTH_SHORT).show()
            }
        }

        binding.searchBtn.btnSearch.setOnClickListener{
            Log.d(TAG,"MainActivity - 검색버튼이 클릭되었다./currentSearchType : $currentSearchType")

            RetrofitManager.instance.searchPhotos(searchTerm = binding.searchTermEditText.toString(),completion = {
                responseState, resposeBody->
                when(responseState){
                    RESPONSE_STATE.OKAY->{
                        Log.d(TAG,"api 호출 성공:$resposeBody")
                    }
                    RESPONSE_STATE.FAIL->{
                        Toast.makeText(this,"api 호출 에러",Toast.LENGTH_SHORT).show()
                        Log.d(TAG,"api 호출 에러:$resposeBody")
                    }
                }
            })

            this.handleSearchButtonUi()
        }

    }

    private fun handleSearchButtonUi(){
        binding.searchBtn.btnProgress.visibility = View.VISIBLE
        binding.searchBtn.btnSearch.text =" "
       Handler().postDelayed({
           binding.searchBtn.btnProgress.visibility=View.INVISIBLE
           binding.searchBtn.btnSearch.text="검색"
       },1500)
    }

    override fun onDestroy() {
        // 바인딩 클래스의 인스턴스 참조를 정리합니다.
        mBinding = null
        super.onDestroy()
    }


}