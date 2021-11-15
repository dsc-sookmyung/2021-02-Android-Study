package com.example.unsplash_app_tutorial.utils

object Constants{
    const val TAG : String="로그"
}

enum class SEARCH_TYPE{
    PHOTO,
    USER
}

enum class RESPONSE_STATE{
    OKAY,
    FAIL
}

object API{
    const val BASE_URL : String="https://api.unsplash.com/"

    const val CLIENT_ID : String="Y3JKKKi1VtOb2CAdgXVtPacwLONyW_dmtMPuvnaq5A0"

    const val SEARCH_PHOTOS : String="search/photos"
    const val SEARCH_USERS : String="search/users"
}