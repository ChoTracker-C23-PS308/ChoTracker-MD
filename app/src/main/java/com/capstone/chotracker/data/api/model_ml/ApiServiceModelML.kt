package com.capstone.chotracker.data.api.model_ml

import com.capstone.chotracker.data.response.chotrack.ChotrackResponseModel
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiServiceModelML {

    @Multipart
    @POST("api/v1/predict/regression")
    suspend fun predict(
        @Part file: MultipartBody.Part,
    ): ChotrackResponseModel

}