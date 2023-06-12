package com.capstone.chotracker.data.api.general
import com.capstone.chotracker.data.response.article.Response
import com.capstone.chotracker.data.response.history.AddHistoryResponseModel
import com.capstone.chotracker.data.response.history.AddImageResponseModel
import com.capstone.chotracker.data.response.history.HistoryModel
import com.capstone.chotracker.data.response.history.HistoryResponseModel
import com.capstone.chotracker.data.response.profile.CreateUserResponse
import com.capstone.chotracker.data.response.profile.ProfileUserResponse
import com.capstone.chotracker.data.response.profile.UpdateUserResponse
import okhttp3.MultipartBody
import retrofit2.Call

import retrofit2.http.*

interface ApiServiceGeneral {

    // HISTORY
    @Headers("Content-Type: application/json; charset=UTF-8")
    @GET("history/{uid}")
    suspend fun getHistories(
        @Header("Authorization") token: String,
        @Path("uid") uid: String
    ): HistoryResponseModel

    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("history/{uid}")
    suspend fun addHistory(
        @Header("Authorization") token: String,
        @Path("uid") uid: String,
        @Body data: HistoryModel
    ): AddHistoryResponseModel

    @Multipart
    @POST("history/{uid}/image")
    suspend fun addImage(
        @Header("Authorization") token: String,
        @Path("uid") uid: String,
        @Part file: MultipartBody.Part
    ): AddImageResponseModel


    // ARTICLE
    @Headers("Content-Type: application/json; charset=UTF-8")
    @GET("articles")
    fun getArticle(
        @Header("Authorization") token: String

    ) : Call<Response>

    @Headers("Content-Type: application/json; charset=UTF-8")
    @GET("users/{id}")
    fun getUserById(@Path("id") id: String): Call<ProfileUserResponse>

    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("users")
    fun createUser(@Body createUserResponse: CreateUserResponse): Call<Unit>

    @Headers("Content-Type: application/json; charset=UTF-8")
    @PUT("users/{id}")
    fun updateUser(@Path("id") id: String, @Body updateUserResponse: UpdateUserResponse): Call<Unit>

    @Multipart
    @PUT("users/{uid}/image")
    suspend fun addImageUser(
        @Header("Authorization") token: String,
        @Path("uid") uid: String,
        @Part file: MultipartBody.Part
    ): UpdateUserResponse


}