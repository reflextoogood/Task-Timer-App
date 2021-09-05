package com.example.tasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    val user=FirebaseAuth.getInstance().currentUser
    val auth=FirebaseAuth.getInstance()

    private lateinit var passwordButton: Button
    private lateinit var oldpass: TextView
    private lateinit var newpass: TextView
    private lateinit var confnewpass: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        passwordButton=findViewById(R.id.buttonid)
        oldpass=findViewById(R.id.currentpassid)
        newpass=findViewById(R.id.newpassid)
        confnewpass=findViewById(R.id.confnewpassid)
        update()
    }
    private fun update(){
        passwordButton.setOnClickListener {
            if (TextUtils.isEmpty(oldpass.text.toString())) {
                oldpass.setError("Please enter your old password")
                return@setOnClickListener
            } else if (TextUtils.isEmpty(newpass.text.toString())) {
                newpass.setError("Please enter the new password")
                return@setOnClickListener
            } else if (TextUtils.isEmpty(confnewpass.text.toString())) {
                confnewpass.setError("Please confirm your new password")
                return@setOnClickListener
            } else if(newpass.text.toString() != confnewpass.text.toString()){
                confnewpass.setError("Password don't match")
                return@setOnClickListener
            }else if(user!=null && user.email!=null){

                val credential=EmailAuthProvider.getCredential(user.email!!,oldpass.text.toString())

                user?.reauthenticate(credential)?.addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this@ChangePasswordActivity,"Re-Authentication success",Toast.LENGTH_SHORT).show()
                        user?.updatePassword(confnewpass.text.toString())
                            ?.addOnCompleteListener { task->
                                if(task.isSuccessful){
                                    Toast.makeText(this@ChangePasswordActivity,"Password changed successfully",Toast.LENGTH_LONG).show()

                                    //Logout the user
                                    auth.signOut()
                                    val logoutIntent=Intent(this,DashboardActivity::class.java)
                                    logoutIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(logoutIntent)
                                }
                                else{
                                    Toast.makeText(this@ChangePasswordActivity,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                    else{
                        Toast.makeText(this@ChangePasswordActivity,"Re-Authentication failed",Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }
    }

}