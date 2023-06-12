package com.capstone.chotracker.ui.findkes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.chotracker.BuildConfig
import com.capstone.chotracker.R
import com.capstone.chotracker.data.api.findkes_map.ApiConfigFindkes
import com.capstone.chotracker.data.response.findkes.FindkesModel
import com.capstone.chotracker.utils.ResultCondition
import kotlinx.coroutines.launch

class FindkesViewModel : ViewModel() {

    private val _resultFindkes = MutableLiveData<ResultCondition<List<FindkesModel>>>()
    val resultFindkes: LiveData<ResultCondition<List<FindkesModel>>> get() = _resultFindkes

    private val gmapApiKey: String = BuildConfig.FINDKES_MAP_API_KEY

    fun getNearbyFindkes(location: String) {
        _resultFindkes.value = ResultCondition.LoadingState

        viewModelScope.launch {
            try {
                val apiService = ApiConfigFindkes.getApiMapFindkes()
                val response = apiService.getNearbyFindkes(
                    apiKey = gmapApiKey,
                    type = "hospital|clinic|health",
                    location = location,
                    rankBy = "distance"
                )

                val result = response.results
                _resultFindkes.value = ResultCondition.SuccessState(result)

            } catch (e: Exception) {
                _resultFindkes.value = ResultCondition.ErrorState(R.string.error_message)
            }
        }
    }

}