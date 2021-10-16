package com.example.cloneinstagram.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.cloneinstagram.R
import com.example.cloneinstagram.databinding.ActivityAddPhotoBinding
import com.example.cloneinstagram.databinding.ActivityMainBinding
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    private var mBinding: ActivityAddPhotoBinding? = null
    private val binding get() = mBinding!!

    var storage : FirebaseStorage? =null
    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()

        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type="image/*"
        launcher.launch(photoPickerIntent)

        binding.addphotoBtnUpload.setOnClickListener {
            contentUpload()
        }
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if(result.resultCode == Activity.RESULT_OK){
            photoUri = result.data?.data
            binding.addphotoImage.setImageURI(photoUri)
        }else{
            finish()
        }
    }

    fun contentUpload(){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName ="IMAGE_"+timestamp+"_png"
        var storageRef = storage?.reference?.child("image")?.child(imageFileName)

        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this,getString(R.string.upload_success),Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}