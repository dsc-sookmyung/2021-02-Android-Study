package com.example.howlstagram_f16.navigation.model

import java.sql.Timestamp

data class AlarmDTO(
    var destinationUid : String? = null,
    var userId : String? = null,
    var uid : String? = null,
    var kind : Int? = null,
    var message : String? = null,
    var timestamp: Long? = null
)