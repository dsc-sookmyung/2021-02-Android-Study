package com.example.cloneinstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.cloneinstagram.databinding.ActivityLoginBinding
import com.example.cloneinstagram.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!

    var auth : FirebaseAuth? =null

    var googleSignInClient : GoogleSignInClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.emailLoginBtn.setOnClickListener {
            signingAndSignup()
        }
        binding.googleLoginBtn.setOnClickListener {
            googleLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("439803585796-ulbjr90c12rdagiu58ini8rht1sdge61.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }
    override fun onStart(){
        super.onStart()
        moveMainPage(auth?.currentUser)
    }




    // 수정3.......................................
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



    fun signingAndSignup(){
        auth?.createUserWithEmailAndPassword(binding.emailEdittext.text.toString(),binding.passwordEdittext.text.toString())
            ?.addOnCompleteListener {
                task ->
                if(task.isSuccessful){
                    moveMainPage(task.result?.user)
                }else if(task.exception?.message.isNullOrEmpty()){
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }else{
                    signingEmail()
                }
            }
    }

    fun signingEmail(){
        auth?.signInWithEmailAndPassword(binding.emailEdittext.text.toString(),binding.passwordEdittext.text.toString())
            ?.addOnCompleteListener {
                task->
                if(task.isSuccessful){
                    moveMainPage(task.result?.user)
                }else{
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()

                }
            }
    }
    fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }



    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}