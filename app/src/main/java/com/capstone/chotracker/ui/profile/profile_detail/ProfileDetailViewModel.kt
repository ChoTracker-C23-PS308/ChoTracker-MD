package com.capstone.chotracker.ui.profile.profile_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.chotracker.R
import com.capstone.chotracker.data.api.general.ApiConfigGeneral
import com.capstone.chotracker.data.response.profile.GetUserModel
import com.capstone.chotracker.data.response.profile.UserModel
import com.capstone.chotracker.utils.ResultCondition
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileDetailViewModel : ViewModel() {


    private val _updateUserState = MutableLiveData<ResultCondition<String>>()
    val updateUserState: LiveData<ResultCondition<String>> get() = _updateUserState

    private val _updateImageState = MutableLiveData<ResultCondition<String>>()
    val updateImageState: LiveData<ResultCondition<String>> get() = _updateImageState

    private val _userProfileState = MutableLiveData<ResultCondition<GetUserModel>>()
    val userProfileState: LiveData<ResultCondition<GetUserModel>> get() = _userProfileState

    fun getUserProfile(token: String, id: String) {
        _userProfileState.value = ResultCondition.LoadingState

        viewModelScope.launch {
            try {
                val apiService = ApiConfigGeneral.getApiGeneral()

                val response = apiService.getUserById(
                    token = "Bearer $token",
                    uid = id,
                )

                val result = response.data
                _userProfileState.value = ResultCondition.SuccessState(result)

            } catch (e: Exception) {
                _userProfileState.value = ResultCondition.ErrorState(R.string.error_message)
            }

        }
    }

    fun updateUserProfile(token: String, id: String, email: String, name: String, birth_date: String, gender: String) {
        _updateUserState.value = ResultCondition.LoadingState

        viewModelScope.launch {
            try {
                val apiService = ApiConfigGeneral.getApiGeneral()

                val response = apiService.updateUser(
                    token = "Bearer $token",
                    uid = id,
                    user = UserModel(
                        id = id,
                        name = name,
                        email = email,
                        birth_date = birth_date,
                        gender = gender,
                        image_url = "picture.jpg"
                    )
                )

                val result = response.data
                _updateUserState.value = ResultCondition.SuccessState(result)

            } catch (e: Exception) {
                _updateUserState.value = ResultCondition.ErrorState(R.string.error_message)
            }

        }
    }

    fun updateImageProfile(token: String, uid: String, imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            _updateImageState.value = ResultCondition.LoadingState
            try {
                val apiService = ApiConfigGeneral.getApiGeneral()

                val response = apiService.updateImageUser(
                    token = "Bearer $token",
                    uid = uid,
                    file = imagePart
                )

                val result = response.data
                _updateImageState.value = ResultCondition.SuccessState(result)

            } catch (e: Exception) {
                _updateImageState.value = ResultCondition.ErrorState(R.string.error_message)
            }
        }
    }

}