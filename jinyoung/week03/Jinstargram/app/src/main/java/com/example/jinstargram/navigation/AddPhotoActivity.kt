package com.example.jinstargram.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.jinstargram.R
import com.example.jinstargram.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity: AppCompatActivity() {
    var storage : FirebaseStorage ?= null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Initiate
        storage= FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //open the album
        var photoPickerIntent= Intent(Intent.ACTION_PICK)
        photoPickerIntent.type="image/*"
        launcher.launch(photoPickerIntent);

        //add image upload event
        addphoto_btn_upload.setOnClickListener{
            contentUpload()
        }
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //This is path to the selected image
            photoUri = result.data?.data
            addphoto_image.setImageURI(photoUri)

        } else {
            // Exit the addPhotoActivity if you leave the album without selecting it
            finish()
        }
    }

    fun contentUpload(){
        //make filename
        var timestamp= SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName="IMAGE_"+timestamp+"_.png"
        var storageRef=storage?.reference?.child("images")?.child(imageFileName)

        //promise method도 있음 callback method 중 하나 선택

        //callback method
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener{
                uri->
                var contentDTO= ContentDTO()

                //insert downloadurl of image
                contentDTO.imageUrl=uri.toString()

                //insert uid of user
                contentDTO.uid=auth?.currentUser?.uid

                //inset userid
                contentDTO.userId=auth?.currentUser?.email

                //insert explain of content
                contentDTO.explain=addphoto_edit_explain.text.toString()

                //insert timestamp
                contentDTO.timestamp=System.currentTimeMillis()

                firestore?.collection("images")?.document()?.set(contentDTO)

                setResult(Activity.RESULT_OK)

                finish()
            }
        }
    }
}