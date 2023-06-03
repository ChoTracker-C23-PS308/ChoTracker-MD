package com.capstone.chotracker.ui.splash_screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.capstone.chotracker.databinding.ActivitySplashScreenBinding
import com.capstone.chotracker.ui.main.MainActivity
import com.capstone.chotracker.ui.on_boarding.OnBoardingActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        navigateTo()
    }

    private fun intentSplash(intent: Intent) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, 1500)
    }

    private fun navigateTo() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intentSplash(intent)
        } else {
            val intent = Intent(this, OnBoardingActivity::class.java)
            intentSplash(intent)
        }
    }

}