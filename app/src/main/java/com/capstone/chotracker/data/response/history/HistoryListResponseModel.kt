package com.capstone.chotracker.data.response.history

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.sql.Time

@Parcelize
data class HistoryListResponseModel (

    @field:SerializedName("uid")
    val uid: String,

    @field:SerializedName("total_kolestrol")
    val totalCholesterol: Float,

    @field:SerializedName("tingkat")
    val tingkat: String,

    @field:SerializedName("image_url")
    val imageUrl: String,

    @field:SerializedName("created_at")
    val date: String,

): Parcelable
