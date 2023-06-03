package com.capstone.chotracker.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.chotracker.R
import com.capstone.chotracker.utils.ResultCondition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<ResultCondition<Boolean>>()
    val loginResult: LiveData<ResultCondition<Boolean>> get() = _loginResult

    fun loginWithEmailPassword(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()

        _loginResult.value = ResultCondition.LoadingState

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val emailVerified = user?.isEmailVerified ?: false
                    _loginResult.value = ResultCondition.SuccessState(emailVerified)
                } else {
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> R.string.wrong_email_password
                        is FirebaseAuthInvalidCredentialsException -> R.string.wrong_email_password
                        else -> R.string.error_message
                    }
                    _loginResult.value = ResultCondition.ErrorState(errorMessage)
                }
            }
    }

    fun loginWithGoogle(idToken: String) {
        val auth = FirebaseAuth.getInstance()

        _loginResult.value = ResultCondition.LoadingState

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val emailVerified = user?.isEmailVerified ?: false
                    _loginResult.value = ResultCondition.SuccessState(emailVerified)
                } else {
                    val errorMessage = R.string.error_message
                    _loginResult.value = ResultCondition.ErrorState(errorMessage)
                }
            }
    }
}