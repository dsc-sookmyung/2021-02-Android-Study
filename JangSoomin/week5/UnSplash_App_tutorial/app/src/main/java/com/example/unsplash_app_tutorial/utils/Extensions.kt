package com.example.unsplash_app_tutorial.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


fun String?.isJsonObject():Boolean{
   return this?.startsWith("{")==true&&this.endsWith("}")
}

fun String?.isJsonArray():Boolean{
    return this?.startsWith("[")==true&&this.endsWith("]")
}



fun EditText.onMyTextChange(completion: (Editable?)->Unit){
    this.addTextChangedListener(object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            completion(p0)
        }
    })
}
