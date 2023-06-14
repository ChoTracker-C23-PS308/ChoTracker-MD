package com.capstone.chotracker.ui.signup

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capstone.chotracker.R
import com.capstone.chotracker.custom_view.CustomPopUpAlert
import com.capstone.chotracker.data.UserPreference
import com.capstone.chotracker.databinding.ActivitySignupBinding
import com.capstone.chotracker.ui.login.LoginActivity
import com.capstone.chotracker.utils.ResultCondition
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var signupViewModel: SignupViewModel
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        signupViewModel = ViewModelProvider(this)[SignupViewModel::class.java]

        userPreference = UserPreference.getInstance(this)

        setupView()
        buttonSignupHandler()
        alreadyHaveAccount()
        observeSignupResult()
    }

    private fun saveDataSignup() {
        val name = binding.layoutSignup.nameEdit.text.toString()
        val email = binding.layoutSignup.emailEdit.text.toString()

        userPreference.namePref = name
        userPreference.emailPref = email
    }

    private fun buttonSignupHandler() {
        binding.layoutSignup.signupButton.setOnClickListener {
            val name = binding.layoutSignup.nameEdit.text.toString()
            val email = binding.layoutSignup.emailEdit.text.toString()
            val password = binding.layoutSignup.passwordEdit.text.toString()
            val confirmPassword = binding.layoutSignup.confirmPasswordEdit.text.toString()

            signupViewModel.signup(name, email, password, confirmPassword)
        }
    }

    private fun observeSignupResult() {
        signupViewModel.signupResult.observe(this, Observer { result ->
            when (result) {
                is ResultCondition.SuccessState -> {
                    progressLoading(false)
                    signUpSuccess(result.data)

                    val succes = result.data
                    if (succes) {
                        saveDataSignup()
                    }
                }
                is ResultCondition.ErrorState -> {
                    progressLoading(false)
                    signUpError(result.data)
                }
                ResultCondition.LoadingState -> {
                    progressLoading(true)
                }
            }
        })
    }

    private fun signUpSuccess(success: Boolean) {
        if (success) {
            val alert = CustomPopUpAlert(this, R.string.signup_success)
            alert.show()
            alert.setOnDismissListener {
                navigateToLogin()
            }
        }
    }

    private fun signUpError(error: Int) {
        CustomPopUpAlert(this, error).show()
    }

    private fun progressLoading(loading: Boolean) {
        if (loading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.layoutSignup.root.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.layoutSignup.root.visibility = View.VISIBLE
        }
    }

    private fun alreadyHaveAccount() {
        binding.layoutSignup.tvAlreadyHaveAccount.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this@SignupActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
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
}