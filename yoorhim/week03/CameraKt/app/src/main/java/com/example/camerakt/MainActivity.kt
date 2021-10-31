package com.example.camerakt

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
import com.example.camerakt.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    //val REQUEST_IMAGE_CAPTURE = 1       // 카메라 사진 촬영 요청코드
    lateinit var curPhotoPath: String   // 문자열 형태의 사진 경로 값 (초기 값을 null로 시작하고 싶을 때)

    // MainActivity 뷰바인딩
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPermission() // 권한을 체크하는 메소드 수행

        binding.btnCamera.setOnClickListener {
            takeCapture()   // 기본 카메라 앱을 실행하여 사진 촬영
        }

    }

    /**
     * 카메라 촬영
     */
    private fun takeCapture() {
        // 기본 카메라 앱 실행
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFil: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFil?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.camerakt.fileprovider",
                        it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    launcher.launch(takePictureIntent)
                }
            }
        }
    }

    /**
     * 이미지 파일 생성
     */
    private fun createImageFile(): File? {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_",".jpg", storageDir)
            .apply { curPhotoPath = absolutePath }
    }

    /**
     * tedPermission 설정
     */
    private fun setPermission() {
        val permission = object : PermissionListener {
            override fun onPermissionGranted() { // 설정해놓은 위험권한들이 허용 되었을 경우 이 곳을 수행함.
                Toast.makeText(this@MainActivity, "권한이 허용 되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(p0: MutableList<String>?) { // 설정해놓은 위험권한 둘 중 거부를 한 경우 이곳을 수행함.
                Toast.makeText(this@MainActivity, "권한이 거부 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleMessage("카메라 앱을 사용하시려면 권한을 허용해주세요.")
            .setDeniedMessage("권한을 거부하셨습니다.\n[앱 설정] -> [권한] 항목에서 허용해주세요.")
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
            .check()
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap
            val file = File(curPhotoPath)
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
                binding.ivProfile.setImageBitmap(bitmap)
            } else {
                val decode = ImageDecoder.createSource(
                    this.contentResolver,
                    Uri.fromFile(file)
                )
                bitmap = ImageDecoder.decodeBitmap(decode)
                binding.ivProfile.setImageBitmap(bitmap)
            }
            savePhoto(bitmap)
        } else {
            finish()
        }
    }

    private fun savePhoto(bitmap: Bitmap) {
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/"
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timestamp}.jpeg"
        val folder = File(folderPath)
        if (!folder.isDirectory) { // 현재 해당 경로에 폴더가 존재하지 않는다면
            folder.mkdirs()        // 해당 경로에 폴더 자동으로 새로 만들기
        }

        // 실제적인 저장처리
        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_LONG).show()
    }

    // 액티비티가 파괴될 때
    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}