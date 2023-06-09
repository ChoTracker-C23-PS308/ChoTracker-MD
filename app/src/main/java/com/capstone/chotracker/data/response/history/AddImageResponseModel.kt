package com.capstone.chotracker.data.response.history

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddImageResponseModel (

    @SerializedName("data")
    val data: String,

    @SerializedName("message")
    val message: String

): Parcelable

