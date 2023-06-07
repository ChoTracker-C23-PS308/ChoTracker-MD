package com.capstone.chotracker.data.response.history

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.sql.Time

@Parcelize
data class HistoryListResponseModel (

    @field:SerializedName("Uid")
    val uid: String,

    @field:SerializedName("TotalCholesterol")
    val totalCholesterol: Float,

    @field:SerializedName("Tingkat")
    val tingkat: String,

    @field:SerializedName("ImageUrl")
    val imageUrl: String,

    @field:SerializedName("CreatedAt")
    val date: Time,

    ): Parcelable
