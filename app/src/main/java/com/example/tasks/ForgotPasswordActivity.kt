package com.example.tasks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var forgotbutton:Button
    private lateinit var emailfield:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        forgotbutton=findViewById(R.id.sendemailid)
        emailfield=findViewById(R.id.emailboxedit)
        forgotbutton.setOnClickListener {
            val email:String = emailfield.text.toString().trim{ it<=' ' }
            if(email.isEmpty()){
                Toast.makeText(this,"Enter your email address", Toast.LENGTH_SHORT).show()
            }
            else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task->
                        if(task.isSuccessful){
                            Toast.makeText(this,"Email sent successfully to reset our password!", Toast.LENGTH_LONG).show()

                            finish()
                        }
                        else{
                            Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

    }

}