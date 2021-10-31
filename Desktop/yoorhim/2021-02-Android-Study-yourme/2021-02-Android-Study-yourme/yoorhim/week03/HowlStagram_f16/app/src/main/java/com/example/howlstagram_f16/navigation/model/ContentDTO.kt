package com.example.howlstagram_f16.navigation.model

import java.sql.Timestamp

class ContentDTO(var explain : String? = null,
                 var imageUrl : String? = null,
                 var uid : String? = null,
                 var userId : String? = null,
                 var timestamp: Long? = null,
                 var favoriteCount : Int = 0,
                 var favorites : Map<String, Boolean> = HashMap()){
    data class Comment (var uid: String? = null,
                        var userId: String? = null,
                        var comment: String? = null,
                        var timestamp: Long? = null)
}