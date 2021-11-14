package com.example.howlstagram_f16

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.howlstagram_f16.databinding.ActivityMainBinding
import com.example.howlstagram_f16.navigation.*
import com.example.howlstagram_f16.navigation.util.FcmPush
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener(this)

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        binding.bottomNavigation.selectedItemId = R.id.action_home
        setToolbarDefault()
        registerPushToken()
    }

    fun setToolbarDefault(){
        toolbar_username.visibility = View.GONE
        toolbar_btn_back.visibility = View.GONE
        toolbar_title_image.visibility = View.VISIBLE
    }

    fun registerPushToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fatching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mutableMapOf<String, Any>()
            map["pushToken"] = token!!

            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }
    }

    override fun onStop() {
        super.onStop()
        FcmPush.instance.sendMessage("rdOMKgXoj8alPEtjmD8mS9IdIrz2", "hi", "bye")
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setToolbarDefault()

        when(item.itemId){
            R.id.action_home->{
                var detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,detailViewFragment).commit()
                return true
            }
            R.id.action_search->{
                var gridFragment = GridFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,gridFragment).commit()
                return true
            }
            R.id.action_add_photo->{
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, AddPhotoActivity::class.java))
                }
                return true
            }
            R.id.action_favorite_alarm->{
                var alarmFragment = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,alarmFragment).commit()
                return true
            }
            R.id.action_account->{
                var userFragment = UserFragment()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                bundle.putString("destinationUid",uid)
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                return true
            }
        }
        return false
    }

}