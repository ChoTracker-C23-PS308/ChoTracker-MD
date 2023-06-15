package com.capstone.chotracker.ui.chochat.mycho_bot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.chotracker.R
import com.capstone.chotracker.data.api.model_ml.ApiConfigModelML
import com.capstone.chotracker.utils.ResultCondition
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MychoBotViewModel() : ViewModel() {

    private val _messageResult = MutableLiveData<ResultCondition<String>>()
    val messageResult: LiveData<ResultCondition<String>> = _messageResult

    fun sendMessage(message: String) {
        _messageResult.value = ResultCondition.LoadingState

        viewModelScope.launch {
            try {
                val apiService = ApiConfigModelML.getApiModel()

                val requestBody = JsonObject().apply {
                    addProperty("text", message)
                }

                val response = apiService.chat(
                    text = requestBody
                )

                val messageReceived = response.message
                _messageResult.value = ResultCondition.SuccessState(messageReceived)

            } catch (e: Exception) {
                _messageResult.value = ResultCondition.ErrorState(R.string.error_message)
            }
        }
    }

}