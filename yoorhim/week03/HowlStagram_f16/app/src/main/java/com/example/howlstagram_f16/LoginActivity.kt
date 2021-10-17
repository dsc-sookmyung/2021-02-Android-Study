package com.example.howlstagram_f16

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.media.tv.TvContract.Programs.Genres.encode
import android.net.Uri.encode
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64.encode
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import bolts.Task
import com.example.howlstagram_f16.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.google.zxing.aztec.encoder.Encoder.encode
import com.google.zxing.qrcode.encoder.Encoder.encode
import java.security.MessageDigest
import java.util.*

class LoginActivity : AppCompatActivity() {

    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!

    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var callbackManager : CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.emailLoginButton.setOnClickListener{
            signinAndSignup()
        }

        binding.googleSignInButton.setOnClickListener {
            googleLogin()
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        callbackManager = CallbackManager.Factory.create()
    }

    override fun onStart() {
        super.onStart()
        if (auth?.currentUser != null)
            moveMainPage(auth?.currentUser)
    }

    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(binding.emailEdittext.text.toString(),
            binding.passwordEdittext.text.toString())?.addOnCompleteListener {
                task ->
                    if(task.isSuccessful) {
                        // Creating a user account
                        moveMainPage(task.result?.user)
                    } else if(task.exception?.message.isNullOrEmpty()){
                        // Show the error message
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    } else {
                        // Login if you have account
                        signinEmail()
                    }
        }
    }

    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        launcher.launch(signInIntent)
    }
    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
        var task = Auth.GoogleSignInApi.getSignInResultFromIntent(result.data)
        if(task.isSuccess){
            var account = task.signInAccount
            firebaseAuthwithGoogle(account)
        }else{
            Toast.makeText(this,"fail to login",Toast.LENGTH_LONG).show()
        }
    }

    fun firebaseAuthwithGoogle(account: GoogleSignInAccount){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    moveMainPage(task.result?.user)
                }else{
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signinEmail(){
        auth?.createUserWithEmailAndPassword(binding.emailEdittext.text.toString(),
            binding.passwordEdittext.text.toString())?.addOnCompleteListener {
                task ->
            if(task.isSuccessful) {
                // Login
                moveMainPage(task.result?.user)
            } else {
                // Show the error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun moveMainPage(user:FirebaseUser?){
        if(user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}