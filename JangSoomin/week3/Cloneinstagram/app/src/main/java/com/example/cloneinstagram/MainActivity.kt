package com.example.cloneinstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cloneinstagram.databinding.ActivityMainBinding
import com.example.cloneinstagram.navigation.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(){
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigation.setOnItemSelectedListener{
            item->
            when(item.itemId){
                R.id.action_home->{
                    var detailViewFragment = DetailViewFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content,detailViewFragment).commit()
                }
                R.id.action_search->{
                    var gridFragment = GridFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content,gridFragment).commit()
                }
                R.id.action_add_photo->{
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                        startActivity(Intent(this,AddPhotoActivity::class.java))
                    }

                }
                R.id.action_favorite_alarm->{
                    var alarmFragment = AlarmFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content,alarmFragment).commit()
                }
                R.id.action_account->{
                    var userFragment = UserFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                }
            }
            false
        }

        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }



}