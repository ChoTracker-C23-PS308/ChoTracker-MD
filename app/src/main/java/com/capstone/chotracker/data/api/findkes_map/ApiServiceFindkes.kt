package com.capstone.chotracker.data.api.findkes_map

import com.capstone.chotracker.data.response.findkes.FindkesResponseModel
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceFindkes {

    @GET("place/nearbysearch/json")
    suspend fun getNearbyFindkes(
        @Query("key") apiKey: String,
        @Query("type") type: String,
        @Query("location") location: String,
        @Query("rankby") rankBy: String,
    ): FindkesResponseModel

}