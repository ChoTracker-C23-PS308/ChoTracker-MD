package com.capstone.chotracker.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.chotracker.R
import com.capstone.chotracker.utils.ResultCondition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignupViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _signupResult = MutableLiveData<ResultCondition<Boolean>>()
    val signupResult: LiveData<ResultCondition<Boolean>> get() = _signupResult

    fun signup(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val emailExists = !task.result.signInMethods.isNullOrEmpty()
                        if (emailExists) {
                            _signupResult.value = ResultCondition.ErrorState(R.string.email_has_been_used_error)
                        } else {
                            if (password == confirmPassword) {
                                _signupResult.value = ResultCondition.LoadingState
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener {
                                        // Kirim email verifikasi
                                        val user = auth.currentUser
                                        user?.sendEmailVerification()
                                            ?.addOnSuccessListener {
                                                _signupResult.value = ResultCondition.SuccessState(true)
                                            }
                                            ?.addOnFailureListener { exception ->
                                                _signupResult.value = ResultCondition.ErrorState(R.string.error_message)
                                            }
                                    }
                                    .addOnFailureListener { exception ->
                                        val errorMessage = when (exception) {
                                            is FirebaseAuthUserCollisionException -> R.string.email_has_been_used_error
                                            else -> R.string.error_message
                                        }
                                        _signupResult.value = ResultCondition.ErrorState(errorMessage)
                                    }
                            } else {
                                _signupResult.value = ResultCondition.ErrorState(R.string.confirm_password_not_match)
                            }
                        }
                    } else {
                        _signupResult.value = ResultCondition.ErrorState(R.string.error_message)
                    }
                }
        } else {
            _signupResult.value = ResultCondition.ErrorState(R.string.make_sure_all_data)
        }
    }
}


