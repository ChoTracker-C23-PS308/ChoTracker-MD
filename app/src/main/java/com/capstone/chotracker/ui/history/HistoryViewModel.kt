package com.capstone.chotracker.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.chotracker.R
import com.capstone.chotracker.data.api.general.ApiConfigGeneral
import com.capstone.chotracker.data.response.history.HistoryListResponseModel
import com.capstone.chotracker.utils.ResultCondition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val _resultCondition = MutableLiveData<ResultCondition<List<HistoryListResponseModel>>>()
    val resultCondition: LiveData<ResultCondition<List<HistoryListResponseModel>>> = _resultCondition

    fun getHistories(token: String, uid: String) {

        _resultCondition.value = ResultCondition.LoadingState

        viewModelScope.launch {
            try {
                val apiService = ApiConfigGeneral.getApiGeneral()
                val response = apiService.getHistories(
                    token = "Bearer $token",
                    uid = uid,
                )

                val historyData = response.data
                _resultCondition.value = ResultCondition.SuccessState(historyData)

            } catch (e: Exception) {
                _resultCondition.value = ResultCondition.ErrorState(R.string.error_message)
            }
        }
    }

}