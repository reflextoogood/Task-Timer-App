package com.example.tasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.tasks.firestore.FirestoreClass
import com.example.tasks.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class DashboardActivity : AppCompatActivity() {

    private lateinit var btnsignin: SignInButton
    private lateinit var auth: FirebaseAuth
    private lateinit var loginbutton:Button
    private lateinit var email: TextView
    private lateinit var password: TextView
    private lateinit var signuppage:TextView
    private lateinit var forgotpassword:TextView

    private companion object {
        private const val RC_GOOGLE_SIGN_IN = 4926
        private const val TAG = "DashboardActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        btnsignin = findViewById(R.id.sign_in_button)
        loginbutton=findViewById(R.id.sendemailid)
        email=findViewById(R.id.emailboxedit)
        password=findViewById(R.id.currentpasswordid)
        signuppage=findViewById(R.id.registerid)
        forgotpassword=findViewById(R.id.forgotpasswordid)

//        auth = Firebase.auth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client: GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        btnsignin.setOnClickListener {
            val signInIntent = client.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }

        login()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        //Navigate to the NextActivity
        if (user == null) {
            Log.w(TAG, "User is null, not going to navigate")
            return
        }
        //if email is verified code
//        else if (user.isEmailVerified) {
            startActivity(Intent(this, MainScreenActivity::class.java))
            finish()

//            Toast.makeText(this@DashboardActivity,"Please verify your email",Toast.LENGTH_LONG).show()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    //Here it starts the logic for adding details into firestore
                    var prof_name:String=""
                    var prof_email:String=""
                    var prof_uid:String=""
                    user?.let{
                        prof_name=user.displayName.toString()
                        prof_email=user.email.toString()
                        prof_uid=user.uid
                    }
                    val filluser=User(
                            prof_uid,
                            prof_name,
                            prof_email
                    )
                    FirestoreClass().registerUserfromsignin(this,filluser)
                    //Here it ends the logic for adding details into firestore

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(this,"Authentication Successful",Toast.LENGTH_LONG).show()

                    updateUI(user)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    private fun login(){
        loginbutton.setOnClickListener {
            if(TextUtils.isEmpty(email.text.toString())){
                email.setError("Please Enter the Email")
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(password.text.toString())){
                password.setError("Please Enter a Valid Password")
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email.text.toString().trim { it<=' ' },password.text.toString())
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Toast.makeText(this@DashboardActivity,"Login Successful!!",Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@DashboardActivity,MainScreenActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this@DashboardActivity,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    }
                }
        }
        signuppage.setOnClickListener {
            startActivity(Intent(this@DashboardActivity,RegisterActivity::class.java))
        }
        forgotpassword.setOnClickListener {
            startActivity(Intent(this@DashboardActivity,ForgotPasswordActivity::class.java))
        }
    }
    fun userRegisterationtofirestore(){
        Toast.makeText(this@DashboardActivity,"Content Updated",Toast.LENGTH_LONG).show()
    }

    fun failed(){
        Toast.makeText(this@DashboardActivity,"There was some error while entering to database",Toast.LENGTH_LONG).show()
    }

}