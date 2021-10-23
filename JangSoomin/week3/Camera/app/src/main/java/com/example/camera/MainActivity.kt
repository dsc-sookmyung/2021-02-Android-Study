package com.example.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.camera.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    val REQUEST_IMAGE_CAPTURES =1 //촬영 요청 코드 임의 설정
    lateinit var curPhotoPath: String // 사진 경로 값 null 허용


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPermission() //앱 실행 시 권한 허용 안내

        binding.btnCamera.setOnClickListener {
            takeCapture()
        }

    }
    //카메라 앱 실행 , 사진 촬영
    private fun takeCapture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{ takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try{
                    createImageFile()
                }catch (ex:IOException){
                    null
                }
                photoFile?.also{
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.camera.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
                    //수정.........카메라 앱 실행 결과 값 가지고 main으로 돌아오기 메소드
                    //startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURES)
                    launcher.launch(takePictureIntent)
                }
            }

        }

    }

    //이미지 파일 생성
    private fun createImageFile(): File? {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_",".jpg",storageDir)
            .apply{curPhotoPath = absolutePath}
    }


    // 권한 허용 요청
    private fun setPermission(){
        val permission = object : PermissionListener{
            override fun onPermissionGranted() {
                Toast.makeText(this@MainActivity,"권한 허용",Toast.LENGTH_SHORT).show()

            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity,"권한 거부",Toast.LENGTH_SHORT).show()

            }
        }

        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleMessage("카메라앱 사용 시 권한 허용 필요")
            .setDeniedMessage("권한 거부.[앱 실행] - > [권한] 항목에서 허용해주세요.")
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA)
            .check()

    }
    //수정 2......startActivityforresult로 들어간 카메라 앱에서의 사진 결과 값
    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if(result.resultCode == Activity.RESULT_OK){
            val bitmap :Bitmap
            val file = File(curPhotoPath)
            if(Build.VERSION.SDK_INT <28){
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver,Uri.fromFile(file))
                binding.ivProfile.setImageBitmap((bitmap))
            }else{
                val decode = ImageDecoder.createSource(
                    this.contentResolver,
                    Uri.fromFile(file)
                )
                bitmap = ImageDecoder.decodeBitmap(decode)
                binding.ivProfile.setImageBitmap(bitmap)
            }
            savePhoto(bitmap)

        }else{
            finish()
        }
    }

    private fun savePhoto(bitmap: Bitmap){
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/"
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timestamp}.jpeg"
        val folder = File(folderPath)
        if(!folder.isDirectory){
            folder.mkdirs()
        }
        val out = FileOutputStream(folderPath+fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out)
        Toast.makeText(this,"사진 저장 완료",Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}