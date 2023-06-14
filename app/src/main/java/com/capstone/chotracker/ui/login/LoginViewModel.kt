package com.capstone.chotracker.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.chotracker.R
import com.capstone.chotracker.data.api.general.ApiConfigGeneral
import com.capstone.chotracker.data.response.profile.UserModel
import com.capstone.chotracker.utils.ResultCondition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<ResultCondition<Boolean>>()
    val loginResult: LiveData<ResultCondition<Boolean>> get() = _loginResult

    private val _checkUser = MutableLiveData<ResultCondition<Boolean>>()
    val checkUser: LiveData<ResultCondition<Boolean>> get() = _checkUser

    private val _createUserState = MutableLiveData<ResultCondition<String>>()
    val createUserState: LiveData<ResultCondition<String>> get() = _createUserState

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

    fun checkUserLogin(token: String, uid: String) {
        viewModelScope.launch {
            try {
                val apiService = ApiConfigGeneral.getApiGeneral()
                val response = apiService.getUserById(
                    token = "Bearer $token",
                    uid = uid
                )

                val check = response.data.ID
                if (check == uid) {
                    _checkUser.value = ResultCondition.SuccessState(true)
                } else {
                    _checkUser.value = ResultCondition.SuccessState(false)
                }

            } catch (e: Exception) {
                _checkUser.value = ResultCondition.SuccessState(false)
            }
        }
    }


    fun createUser(token: String, id: String, email: String, name: String) {
        viewModelScope.launch {
            _createUserState.value = ResultCondition.LoadingState
            try {
                val apiService = ApiConfigGeneral.getApiGeneral()

                val response = apiService.createUser(
                    token = "Bearer $token",
                    user = UserModel(
                        id = id,
                        name = name,
                        email = email,
                        birth_date = " ",
                        gender = " ",
                        image_url = " "
                    )
                )

                val result = response.data
                _createUserState.value = ResultCondition.SuccessState(result)

            } catch (e: Exception) {
                _createUserState.value = ResultCondition.ErrorState(R.string.error_message)
            }
        }
    }

}