package com.example.unsplash_app_tutorial.retrofit

import android.util.Log
import com.example.unsplash_app_tutorial.utils.API
import com.example.unsplash_app_tutorial.retrofit.IRetrofit
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import com.example.unsplash_app_tutorial.utils.RESPONSE_STATE
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {
    companion object{
        val instance = RetrofitManager()
    }

    private val iRetrofit : IRetrofit? = RetrofitClient.getClient(API.Base_URL)?.create(IRetrofit::class.java)

    fun searchPhotos(searchTerm: String?, completion:(RESPONSE_STATE,String)->Unit){

        val term:String = searchTerm.let{
            it
        }?:""

        val call : Call<JsonElement> = iRetrofit?.searchPhotos(searchTerm = term).let{
            it
        }?:return

        call.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG,"RetrofitManger-onResponse() called / response: ${response.body()}")
                completion(RESPONSE_STATE.OKAY,response.body().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG,"RetrofitManger-onFailure() called / t: $t")
                completion(RESPONSE_STATE.FAIL,t.toString())
            }

        })

    }

}