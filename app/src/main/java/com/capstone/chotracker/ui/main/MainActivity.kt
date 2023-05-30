package com.capstone.chotracker.ui.main

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.capstone.chotracker.R
import com.capstone.chotracker.databinding.ActivityMainBinding
import com.capstone.chotracker.ui.chochat.ChoChatLandingPageFragment
import com.capstone.chotracker.ui.home.HomeFragment
import com.capstone.chotracker.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
        when(it.itemId){

            R.id.home_nav -> replaceFragment(HomeFragment())
            R.id.chochat_nav -> replaceFragment(ChoChatLandingPageFragment())
            R.id.profil_nav -> replaceFragment(ProfileFragment())

            else -> {

            }

        }
            true
        }

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

    private fun replaceFragment(fragment : Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction =fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

}