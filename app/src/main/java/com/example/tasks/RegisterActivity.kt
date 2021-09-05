package com.example.tasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.tasks.firestore.FirestoreClass
import com.example.tasks.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private lateinit var registerButton: Button
    private lateinit var name: TextView
    private lateinit var password: TextView
    private lateinit var email: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton = findViewById(R.id.sendemailid)
        name = findViewById(R.id.fullnameid)
        email = findViewById(R.id.emailboxedit)
        password = findViewById(R.id.currentpasswordid)

        auth = FirebaseAuth.getInstance()

        register()
    }

    private fun register() {
        registerButton.setOnClickListener {
            if (TextUtils.isEmpty(name.text.toString())) {
                name.setError("Please enter your name")
                return@setOnClickListener
            } else if (TextUtils.isEmpty(email.text.toString())) {
                email.setError("Please enter your email")
                return@setOnClickListener
            } else if (TextUtils.isEmpty(password.text.toString())) {
                password.setError("Please enter your password")
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(
                email.text.toString().trim { it <= ' ' },
                password.text.toString()
            )
                .addOnCompleteListener { task ->
                    // If registration is successfully done
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = User(
                            firebaseUser.uid,
                            name.text.toString().trim { it <= ' ' },
                            email.text.toString().trim { it <= ' ' }
                        )
                        FirestoreClass().registerUser(this, user)
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            task.exception!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }


    fun userRegisterationtofirestore() {
        Toast.makeText(this@RegisterActivity, "Registration Successful! ", Toast.LENGTH_LONG).show()
        startActivity(Intent(this@RegisterActivity, MainScreenActivity::class.java))
        finish()
    }

    fun failed() {
        Toast.makeText(
            this@RegisterActivity,
            "There was some error while entering to database",
            Toast.LENGTH_LONG
        ).show()
    }
}