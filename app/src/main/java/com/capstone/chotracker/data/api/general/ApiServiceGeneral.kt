package com.capstone.chotracker.data.api.general
import com.capstone.chotracker.data.response.article.Response
import com.capstone.chotracker.data.response.history.AddHistoryResponseModel
import com.capstone.chotracker.data.response.history.AddImageResponseModel
import com.capstone.chotracker.data.response.history.HistoryModel
import com.capstone.chotracker.data.response.history.HistoryResponseModel
import com.capstone.chotracker.data.response.profile.*
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

    // USER
    @Headers("Content-Type: application/json; charset=UTF-8")
    @GET("users/{uid}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("uid") uid: String
    ): UserProfileResponseModel

    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("users")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body user: UserModel
    ): CreateUserResponseModel

    @Headers("Content-Type: application/json; charset=UTF-8")
    @PUT("users/{uid}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("uid") uid: String,
        @Body user: UserModel
    ): UpdateUserResponseModel

    @Multipart
    @PUT("users/{uid}/image")
    suspend fun updateImageUser(
        @Header("Authorization") token: String,
        @Path("uid") uid: String,
        @Part file: MultipartBody.Part
    ): UpdateUserImageResponseModel


}