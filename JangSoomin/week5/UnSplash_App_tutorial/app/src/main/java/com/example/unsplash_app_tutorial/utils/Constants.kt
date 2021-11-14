package com.example.unsplash_app_tutorial.utils

object Constants{
    const val TAG: String = "로그"
}

enum class SEARCH_TYPE {
    PHOTO,
    USER
}

enum class RESPONSE_STATE{
    OKAY,
    FAIL
}



object API{
    const val Base_URL : String = "http://api.unsplash.com/"
    const val  CLIENT_ID : String =""
    const val SEARCH_PHOTOS: String ="search/photos"
    const val SEARCH_USERS: String = "search/users"
}