package com.capstone.chotracker.data.api.general

import com.capstone.chotracker.data.response.article.Response
import com.capstone.chotracker.data.response.history.AddHistoryResponseModel
import com.capstone.chotracker.data.response.history.AddImageResponseModel
import com.capstone.chotracker.data.response.history.HistoryModel
import com.capstone.chotracker.data.response.history.HistoryResponseModel
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceGeneral {

    // HISTORY
    @Headers("Content-Type: application/json; charset=UTF-8")
    @GET("history/{uid}")
    suspend  fun getHistories(
        @Header("Authorization") token: String,
        @Path("uid") uid: String
    ): HistoryResponseModel

    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("history/{id}")
    fun addHistory(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body data: HistoryModel
    ): AddHistoryResponseModel

    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("history/{id}/image")
    fun addImage(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): AddImageResponseModel

    @Headers("Content-Type: application/json; charset=UTF-8")
    @GET("articles")
    fun getArticle(
        @Header("Authorization") token: String
    ) : Response<Any?>


}