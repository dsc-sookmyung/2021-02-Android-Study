package com.example.jinstargram

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_login.*
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.Arrays.asList

class LoginActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener {
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener {
            //First step
            googleLogin()
        }
        facebook_login_button.setOnClickListener {
            facebookLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1012494402794-me519c7dnjfn3apitl4sm86fckaqgj8u.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //printHashKey()
        callbackManager = CallbackManager.Factory.create()
    }
    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(),0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth?.currentUser != null)
            moveMainPage(auth?.currentUser)
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        var value = Auth.GoogleSignInApi.getSignInResultFromIntent(result.data)!!

        if (value.isSuccess) {
            var account = value.signInAccount
            firebaseAuthWithGoogle(account)
            Toast.makeText(this, "성공", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, "실패", Toast.LENGTH_LONG).show()
        }
    }
    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        launcher.launch(signInIntent)
    }

    fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, asList("public_profile", "email", "user_friends"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }
                override fun onCancel() {
                }
                override fun onError(error: FacebookException?) {
                }
            })
    }
    fun handleFacebookAccessToken(token : AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener{
                    task->
                if (task.isSuccessful){
                    // Login
                    moveMainPage(task.result?.user)
                } else {
                    // Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data);
    }

    private fun firebaseAuthWithGoogle(idToken: GoogleSignInAccount?) {
        // it가 tokenId, credential은 Firebase 사용자 인증 정보
        var credential = GoogleAuthProvider.getCredential(idToken?.idToken, null)

        // credential로 Firebase 인증
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) {
                    task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    // Login
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인 성공",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    // Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Creating a user account
                    moveMainPage(task.result?.user)
                } else if (task.exception?.message.isNullOrEmpty()) {
                    // Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    // Login if you have account
                    signinEmail()
                }
            }
    }

    fun signinEmail() {
        auth?.signInWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Login
                    moveMainPage(task.result?.user)
                } else {
                    //show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) { // 유저가 존재할
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}