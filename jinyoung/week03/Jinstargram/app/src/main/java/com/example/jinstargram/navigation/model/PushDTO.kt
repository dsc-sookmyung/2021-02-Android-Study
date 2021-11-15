package com.example.jinstargram.navigation.model

import android.app.Notification
import com.google.firebase.messaging.RemoteMessage

data class PushDTO (
    var to : String? = null,
    var notification : Notification = Notification()
){
    data class Notification(
        var body : String? = null,
        var title : String? = null
    )
}