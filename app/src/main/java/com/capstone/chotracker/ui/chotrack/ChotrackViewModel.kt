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
import com.capstone.chotracker.data.api.general.ApiConfigGeneral
import com.capstone.chotracker.data.response.chotrack.ChotrackResponseModel
import com.capstone.chotracker.data.response.history.HistoryModel


class ChotrackViewModel : ViewModel() {

    private val _predictState = MutableLiveData<ResultCondition<Float?>>()
    val predictState: LiveData<ResultCondition<Float?>> get() = _predictState

    private val _addHistoryState = MutableLiveData<ResultCondition<String>>()
    val addHistoryState: LiveData<ResultCondition<String>> get() = _addHistoryState

    private val _addImageState = MutableLiveData<ResultCondition<String>>()
    val addImageState: LiveData<ResultCondition<String>> get() = _addHistoryState

    fun predictCholesterol(imageFile: MultipartBody.Part) {
        _predictState.value = ResultCondition.LoadingState

        viewModelScope.launch {
            try {
                val apiInterface = ApiConfigModelML.getApiModel()
                val result = apiInterface.predict(imageFile)

                val cholesterolResult = result as? ChotrackResponseModel
                if (cholesterolResult != null) {
                    _predictState.value = ResultCondition.SuccessState(cholesterolResult.prediction)
                } else {
                    _predictState.value = ResultCondition.ErrorState(R.string.error_message)
                }
            } catch (e: Exception) {
                _predictState.value = ResultCondition.ErrorState(R.string.error_message)
            }
        }
    }

    fun saveResult(token: String, uid: String, imagePart: MultipartBody.Part, totalKolestrol: Float, tingkat: String) {
        _addImageState.value = ResultCondition.LoadingState

        viewModelScope.launch {
            try {
                val apiService = ApiConfigGeneral.getApiGeneral()
                val response = apiService.addImage(
                    token = "Bearer $token",
                    uid = uid,
                    file = imagePart
                )

                val imageUrl = response.data
                _addImageState.value = ResultCondition.SuccessState(imageUrl)

                _addHistoryState.value = ResultCondition.LoadingState

                viewModelScope.launch {
                    try {
                        val responseHistory = apiService.addHistory(
                            token = "Bearer $token",
                            uid = uid,
                            data = HistoryModel(
                                uid = uid,
                                total_kolestrol = totalKolestrol,
                                tingkat = tingkat,
                                image_url = imageUrl
                            )
                        )

                        val addHistoryMessage = responseHistory.message
                        _addHistoryState.value = ResultCondition.SuccessState(addHistoryMessage)

                    } catch (e: Exception) {
                        _addHistoryState.value = ResultCondition.ErrorState(R.string.error_message)
                    }
                }

            } catch (e: Exception) {
                _addImageState.value = ResultCondition.ErrorState(R.string.error_message)
            }
        }
    }

}

