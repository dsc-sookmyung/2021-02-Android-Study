package com.example.howlstagram_f16

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.media.tv.TvContract.Programs.Genres.encode
import android.net.Uri.encode
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Base64.encode
import android.util.Log
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
import com.google.android.gms.auth.api.credentials.IdToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.google.zxing.aztec.encoder.Encoder.encode
import com.google.zxing.qrcode.encoder.Encoder.encode
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
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

        binding.facebookLoginButton.setOnClickListener {
            facebookLogin()
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("221778989920-pgo3cptir6ruvjdtqabc84cg2dqfiipa.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        callbackManager = CallbackManager.Factory.create()
    }

    // 로그인 유지 기능
    override fun onStart() {
        super.onStart()
        if (auth?.currentUser != null)
            moveMainPage(auth?.currentUser)
    }

    fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email","user_friends"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {
                    TODO("Not yet implemented")
                }

                override fun onError(error: FacebookException?) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun handleFacebookAccessToken(token: AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
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

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        var value = Auth.GoogleSignInApi.getSignInResultFromIntent(result.data)!!

        if (value.isSuccess) {
            var account = value.signInAccount
            firebaseAuthwithGoogle(account)
            Toast.makeText(this, "성공", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, "실패", Toast.LENGTH_LONG).show()
        }
    }

    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        launcher.launch(signInIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun firebaseAuthwithGoogle(account: GoogleSignInAccount){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth!!.signInWithCredential(credential)?.addOnCompleteListener {
                    task: com.google.android.gms.tasks.Task<AuthResult> ->
                if(task.isSuccessful){
                    //moveMainPage(task.result?.user)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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
            finish()
        }
    }

}