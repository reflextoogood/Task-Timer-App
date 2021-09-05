package com.example.tasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var img : ImageView
    private lateinit var splash_text:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        img=findViewById(R.id.ic_logo)
//        img.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_in))
//        Handler(Looper.getMainLooper()).postDelayed({
//                     img.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_out))
//            Handler(Looper.getMainLooper()).postDelayed({
//                img.visibility = View.GONE
//                startActivity(Intent(this,DashboardActivity::class.java))
//                finish()
//            },500)
//        },1500)

        splash_text=findViewById(R.id.splashtext)
        splash_text.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_in))
        Handler(Looper.getMainLooper()).postDelayed({
            splash_text.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_out))
            Handler(Looper.getMainLooper()).postDelayed({
                splash_text.visibility = View.GONE
                startActivity(Intent(this,DashboardActivity::class.java))
                finish()
            },500)
        },1500)

    }
}