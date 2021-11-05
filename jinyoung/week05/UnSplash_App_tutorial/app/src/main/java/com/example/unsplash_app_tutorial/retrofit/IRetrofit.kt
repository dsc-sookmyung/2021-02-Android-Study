package com.example.unsplash_app_tutorial.retrofit

import com.example.unsplash_app_tutorial.utils.API
import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofit {

    @GET(API.SEARCH_PHOTOS)
    fun searchPhotos(@Query("query") searchTerm:String) : retrofit2.Call<JsonElement>

    @GET(API.SEARCH_USERS)
    fun searchUsers(@Query("query") searchTerm:String) : retrofit2.Call<JsonElement>
}