package com.capstone.chotracker.ui.main

import HomeFragment
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.chotracker.R
import com.capstone.chotracker.chotrack_cam.ChotrackCamActivity
import com.capstone.chotracker.chotrack_cam.ChotrackCamActivity.Companion.PICKED_MEDIA_LIST
import com.capstone.chotracker.chotrack_cam.ChotrackCamActivity.Companion.REQUEST_CODE_PICKER
import com.capstone.chotracker.chotrack_cam.ChotrackCamOptions
import com.capstone.chotracker.data.UserPreference
import com.capstone.chotracker.databinding.ActivityMainBinding
import com.capstone.chotracker.ui.chochat.ChochatFragment
import com.capstone.chotracker.ui.chotrack.ChotrackActivity
import com.capstone.chotracker.ui.findkes.FindkesFragment
import com.capstone.chotracker.ui.login.LoginViewModel
import com.capstone.chotracker.ui.profile.ProfileFragment
import com.capstone.chotracker.utils.ResultCondition
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mChotrackCamOptions: ChotrackCamOptions
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userPreference: UserPreference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        mChotrackCamOptions = ChotrackCamOptions()
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        userPreference = UserPreference.getInstance(this)
        auth = FirebaseAuth.getInstance()

        navigationHandler()
        setupView()
        chotrackCamButtonHandler()
        isUserAlreadyCreated()
        userAlreadyCreatedObserve()
    }


    private fun isUserAlreadyCreated() {
        CoroutineScope(Dispatchers.Main).launch {
            val token = getFirebaseToken()
            val uid = auth.currentUser?.uid

            token?.let {
                uid?.let { it1 ->
                    loginViewModel.checkUserLogin(it, it1)
                }
            }

        }
    }

    private fun userAlreadyCreatedObserve() {
        loginViewModel.checkUser.observe(this) { result ->
            when(result) {
                is ResultCondition.LoadingState -> {}
                is ResultCondition.SuccessState -> {
                    if(result.data == false) {
                        createUser()
                    }
                }
                is ResultCondition.ErrorState -> {}
            }
        }
    }

    private fun createUser() {
        CoroutineScope(Dispatchers.Main).launch {
            val token = getFirebaseToken()
            val uid = auth.currentUser?.uid

            var name = userPreference.namePref
            var email = userPreference.emailPref

            if (name == null && email == null) {
                name = auth.currentUser?.displayName
                email = auth.currentUser?.email
            }

            token?.let {
                uid?.let { it1 ->
                    if (name != null && email != null) {
                        loginViewModel.createUser(
                            token = it,
                            id = it1,
                            name = name,
                            email = email
                        )
                    }
                }
            }

        }
    }

    private suspend fun getFirebaseToken(): String? {
        val currentUser = auth.currentUser
        return try {
            currentUser?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            Log.e("Firebase Token", "Error getting Firebase token: ${e.message}")
            null
        }
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
            val selectedImagePath = mImageList[0]

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
                R.id.chochat_nav -> replaceFragment(ChochatFragment())
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