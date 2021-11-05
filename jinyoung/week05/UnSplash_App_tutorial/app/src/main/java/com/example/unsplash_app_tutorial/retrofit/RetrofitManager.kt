package com.example.unsplash_app_tutorial.retrofit

import android.util.Log
import com.example.unsplash_app_tutorial.utils.API
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import com.example.unsplash_app_tutorial.utils.RESPONSE_STATE
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {
    companion object{
        val instance=RetrofitManager()
    }

    //http 콜 만들기
    //레트로핏 인터페이스 가져오기
    private val iRetrofit : IRetrofit? = RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)

    //사진 검색 api 호출
    fun searchPhotos(searchTerm:String?, completion:(RESPONSE_STATE,String)->Unit){

        val term=searchTerm.let{
            it
        }?: ""

        val call =iRetrofit?.searchPhotos(searchTerm=term).let {
            it
        }?:return

        call.enqueue(object:retrofit2.Callback<JsonElement>{

            //응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG,"RetrofitManager - onFailure() called/t : $t")

                completion(RESPONSE_STATE.FAIL,t.toString())
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG,"RetrofitManager - onResponse() called/response : ${response.raw()}")
                completion(RESPONSE_STATE.OKAY,response.raw().toString())
            }
        })

    }
}