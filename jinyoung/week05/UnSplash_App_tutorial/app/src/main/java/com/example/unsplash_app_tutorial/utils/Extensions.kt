package com.example.unsplash_app_tutorial.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

//string이 제이슨 형태인지 확인
fun String?.isJsonObject():Boolean{
    /*
    if(this?.startsWith("{")==true && this.endsWith("}")){
        return true
    }else{
        return false
    }
    */

    return this?.startsWith("{")==true && this.endsWith("}")
}

//string이 제이슨 배열 형태인지 확인
fun String?.isJsonArray():Boolean{
    /*
    if(this?.startsWith("{")==true && this.endsWith("}")){
        return true
    }else{
        return false
    }
    */

    return this?.startsWith("{")==true && this.endsWith("}")
}

//edit text에 대한 extension
fun EditText.onMyTextChanged(completion:(Editable?)->Unit){
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            completion(editable)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    })
}