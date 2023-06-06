package com.capstone.chotracker.data.api

import com.capstone.chotracker.data.response.ChotrackResponseModel
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiInterface {

    @Multipart
    @POST("api/v1/predict/regression")
    suspend fun predict(
        @Part file: MultipartBody.Part,
    ): ChotrackResponseModel

}