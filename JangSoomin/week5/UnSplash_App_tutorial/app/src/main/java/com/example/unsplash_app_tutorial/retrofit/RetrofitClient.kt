package com.example.unsplash_app_tutorial.retrofit

import android.util.Log
import com.example.unsplash_app_tutorial.utils.API
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import com.example.unsplash_app_tutorial.utils.isJsonArray
import com.example.unsplash_app_tutorial.utils.isJsonObject
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var retrofitClient : Retrofit?=null

    fun getClient(baseUrl:String):Retrofit?{
        Log.d(TAG,"RetrofitClient - getClient() called")

        val client = OkHttpClient.Builder()

        val loggingInterceptor = HttpLoggingInterceptor(object :HttpLoggingInterceptor.Logger{

            override fun log(message: String) {
                Log.d(TAG,"RetrofitClient - log() called / message: $message")

                when{
                    message.isJsonObject()->
                        Log.d(TAG,JSONObject(message).toString(4))
                    message.isJsonArray()->
                        Log.d(TAG,JSONObject(message).toString(4))
                    else->{
                        try{
                            Log.d(TAG,JSONObject(message).toString(4))
                        }catch (e:Exception){
                            Log.d(TAG,message)
                        }

                    }
                }

            }

        })

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        client.addInterceptor(loggingInterceptor)

        val baseParameterInterceptor : Interceptor =(object :Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                Log.d(TAG,"RetrofitClient - intercept() called")
                val originalRequest: Request = chain.request()

                val addedUrl: HttpUrl = originalRequest.url.newBuilder().addQueryParameter("client_id",API.CLIENT_ID).build()
                val finalRequest = originalRequest.newBuilder().url(addedUrl).method(originalRequest.method,originalRequest.body).build()

                return chain.proceed(finalRequest)
            }
        })

        client.addInterceptor(baseParameterInterceptor)


        client.connectTimeout(10,TimeUnit.SECONDS)
        client.readTimeout(10,TimeUnit.SECONDS)
        client.writeTimeout(10,TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)


        if(retrofitClient == null){
            retrofitClient=Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
        }

        return retrofitClient
    }


}