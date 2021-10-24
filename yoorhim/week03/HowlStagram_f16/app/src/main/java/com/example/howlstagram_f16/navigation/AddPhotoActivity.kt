package com.example.howlstagram_f16.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.howlstagram_f16.R
import com.example.howlstagram_f16.databinding.ActivityAddPhotoBinding
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    private var mBinding: ActivityAddPhotoBinding? = null
    private val binding get() = mBinding!!

    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initiate storage
        storage = FirebaseStorage.getInstance()

        //Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        launcher.launch(photoPickerIntent)

        binding.addphotoBtnUpload.setOnClickListener {
            contentUpload()
        }
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoUri = result.data?.data
            binding.addphotoImage.setImageURI(photoUri)

        } else {
            finish()
        }
    }

    fun contentUpload() {
        //Make filename

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "Image_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //FileUpload
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG).show()
        }
    }
}

