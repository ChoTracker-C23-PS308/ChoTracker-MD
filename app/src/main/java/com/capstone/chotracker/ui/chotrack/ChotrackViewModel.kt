package com.capstone.chotracker.ui.chotrack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.chotracker.data.api.model_ml.ApiConfigModelML
import com.capstone.chotracker.utils.ResultCondition
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import com.capstone.chotracker.R
import com.capstone.chotracker.data.response.chotrack.ChotrackResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChotrackViewModel : ViewModel() {

    private val _successState = MutableLiveData<Float?>()
    val successState: LiveData<Float?> get() = _successState

    private val _errorState = MutableLiveData<Int?>()
    val errorState: LiveData<Int?> get() = _errorState

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    fun predictCholesterol(imageFile: MultipartBody.Part) {
        _successState.value = null
        _errorState.value = null

        viewModelScope.launch {
            _loadingState.value = true

            val response = withContext(Dispatchers.IO) {
                try {
                    val apiInterface = ApiConfigModelML.getApiModel()
                    val result = apiInterface.predict(imageFile)
                    ResultCondition.SuccessState(result)
                } catch (e: Exception) {
                    ResultCondition.ErrorState(R.string.error_message)
                }
            }

            _loadingState.value = false

            when (response) {
                is ResultCondition.SuccessState<*> -> {
                    val result = response.data as? ChotrackResponseModel
                    if (result != null) {
                        _successState.value = result.prediction
                    } else {
                        _errorState.value = R.string.error_message
                    }
                }
                is ResultCondition.ErrorState -> {
                    val errorMessage = response.data
                    _errorState.value = errorMessage
                }
                ResultCondition.LoadingState -> {
                    _loadingState.value = true
                }
            }
        }
    }
}