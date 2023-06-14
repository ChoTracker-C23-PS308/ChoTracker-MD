package com.capstone.chotracker.ui.main

import HomeFragment
import ProfileFragment
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.capstone.chotracker.R
import com.capstone.chotracker.chotrack_cam.ChotrackCamActivity
import com.capstone.chotracker.chotrack_cam.ChotrackCamActivity.Companion.PICKED_MEDIA_LIST
import com.capstone.chotracker.chotrack_cam.ChotrackCamActivity.Companion.REQUEST_CODE_PICKER
import com.capstone.chotracker.chotrack_cam.ChotrackCamOptions
import com.capstone.chotracker.databinding.ActivityMainBinding
import com.capstone.chotracker.ui.chochat.ChoChatLandingPageFragment
import com.capstone.chotracker.ui.chotrack.ChotrackActivity
import com.capstone.chotracker.ui.findkes.FindkesFragment



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mChotrackCamOptions: ChotrackCamOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        mChotrackCamOptions = ChotrackCamOptions.init().apply {
            maxCount = 1
        }

        navigationHandler()
        setupView()
        chotrackCamButtonHandler()
    }

    private fun chotrackCamButtonHandler() {
        binding.chotrackCamButton.setOnClickListener {
            ChotrackCamActivity.startPicker(this, mChotrackCamOptions)
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PICKER){
            val mImageList = data?.getStringArrayListExtra(PICKED_MEDIA_LIST) as ArrayList<String>
            val selectedImagePath = mImageList[0] // Ambil path gambar pertama

            val intent = Intent(this, ChotrackActivity::class.java)
            intent.putExtra(EXTRA_IMAGE, selectedImagePath)
            startActivity(intent)
        }
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

    private fun navigationHandler() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.home_nav -> replaceFragment(HomeFragment())
                R.id.chochat_nav -> replaceFragment(ChoChatLandingPageFragment())
                R.id.profil_nav -> replaceFragment(ProfileFragment())
                R.id.findkes_nav -> replaceFragment(FindkesFragment())
            }
            true
        }
    }

    companion object {
        const val EXTRA_IMAGE= "Extra Image"
    }

}