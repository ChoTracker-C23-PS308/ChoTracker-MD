package com.capstone.chotracker.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capstone.chotracker.R
import com.capstone.chotracker.custom_view.CustomPopUpAlert
import com.capstone.chotracker.databinding.ActivityLoginBinding
import com.capstone.chotracker.ui.main.MainActivity
import com.capstone.chotracker.ui.signup.SignupActivity
import com.capstone.chotracker.utils.ResultCondition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        navigateToSignUp()

        // Configure Google Login
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        loginWithEmailPassword()
        loginWithGoogle()
        observeLoginResult()

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

    private fun navigateToSignUp() {
        binding.layoutLogin.tvDoNotHaveAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity()
            System.exit(0)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun loginWithEmailPassword() {
        binding.layoutLogin.loginButton.setOnClickListener {
            val email = binding.layoutLogin.emailEdit.text.toString()
            val password = binding.layoutLogin.passwordEdit.text.toString()

            loginViewModel.loginWithEmailPassword(email, password)
        }
    }

    private fun loginWithGoogle() {
        binding.layoutLogin.buttonGoogle.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            resultLauncher.launch(intent)
        }
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                loginViewModel.loginWithGoogle(account.idToken!!) // Gunakan fungsi loginWithGoogle dari LoginViewModel
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google login failed", e)
            }
        }
    }


    private fun redirectToMainPage() {
        if (intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0) {
            // User is already logged in, navigate to MainActivity
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(this, Observer { result ->
            when (result) {
                is ResultCondition.SuccessState -> {
                    progressLoading(false)
                    loginSuccess(result.data)
                }
                is ResultCondition.ErrorState -> {
                    progressLoading(false)
                    loginError(result.data)
                }
                ResultCondition.LoadingState -> {
                    progressLoading(true)
                }
            }
        })
    }

    private fun loginSuccess(emailVerified: Boolean) {
        if (emailVerified) {
            // Redirect to main page
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        } else {
            // Show email verification error
            CustomPopUpAlert(this, R.string.email_verification_error).show()
        }
    }

    private fun loginError(errorMessage: Int) {
        // Show login error
        CustomPopUpAlert(this, errorMessage).show()
    }


    private fun progressLoading(loading: Boolean) {
        if (loading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.layoutLogin.root.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.layoutLogin.root.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}