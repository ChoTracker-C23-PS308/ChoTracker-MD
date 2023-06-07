package com.capstone.chotracker.data.response.history

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryResponseModel (

    @field:SerializedName("data")
    val data: ArrayList<HistoryListResponseModel>,

    @field:SerializedName("message")
    val message: String

): Parcelable
