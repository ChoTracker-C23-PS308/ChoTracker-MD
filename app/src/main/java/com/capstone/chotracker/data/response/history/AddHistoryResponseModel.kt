package com.capstone.chotracker.data.response.history

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddHistoryResponseModel (

    @field:SerializedName("data")
    val id: String,

    @field:SerializedName("message")
    val message: String

): Parcelable
