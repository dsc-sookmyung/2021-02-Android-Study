package com.example.cloneinstagram.navigation.util

import com.example.cloneinstagram.navigation.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.okhttp.*
import java.io.IOException

class FcmPush {
    var JSON = MediaType.parse("")
    var url = null
    var serverKey= null
    var gsom : Gson? = null
    var okHttpClient : OkHttpClient?=null
    companion object{
        var instance = FcmPush()
    }

    init{
        gsom = Gson()
        okHttpClient = OkHttpClient()
    }
    fun sendMessage(destinationUid :String,title:String,message:String){
        FirebaseFirestore.getInstance().collection("pushtoken").document(destinationUid).get().addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                var token = task?.result?.get("pushToken").toString()

                var PushDTO = PushDTO()
                PushDTO.to = token
                PushDTO.notification.title = title
                PushDTO.notification.body = message

                var body = RequestBody.create(JSON.gson?.toJson(pushDTO))
                var request = Request.Builder(
                    .addHeader("Content-Type","application/json")
                    .addHeader("Authorization","key="+serverkey)
                    .url(url)
                    .post(body)
                    .build()

                okHttpClient?.newCall(request)?.enqueue(object : Callback {
                    override fun onFailure(request: Request?, e: IOException?) {

                    }

                    override fun onResponse(response: Response?) {
                        println(response?.body()?.string())
                    }

                })
                )
            }
        }
    }






}
