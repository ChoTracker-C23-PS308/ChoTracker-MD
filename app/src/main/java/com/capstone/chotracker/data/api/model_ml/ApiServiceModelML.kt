package com.capstone.chotracker.data.api.model_ml

import com.capstone.chotracker.data.response.chobot.ChobotResponseModel
import com.capstone.chotracker.data.response.chotrack.ChotrackResponseModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiServiceModelML {

    @Multipart
    @POST("api/v1/predict/regression")
    suspend fun predict(
        @Part file: MultipartBody.Part,
    ): ChotrackResponseModel

    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("api/v1/predict/chobot")
    suspend fun chat(
        @Body text: JsonObject
    ): ChobotResponseModel

}