package com.capstone.chotracker.ui.chochat.ads

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.capstone.chotracker.R
import com.capstone.chotracker.data.preferences.UserPreference
import com.capstone.chotracker.databinding.ActivityAdsBinding
import com.capstone.chotracker.ui.chochat.ChochatFragment
import com.capstone.chotracker.ui.main.MainActivity

class AdsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdsBinding
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this)
        subscribe()
        setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun subscribe() {
        binding.layoutAds.buttonContinue.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigate_to_chochat", true)
            startActivity(intent)
            finish()
            userPreference.isSubscribePref = true
        }
    }
}