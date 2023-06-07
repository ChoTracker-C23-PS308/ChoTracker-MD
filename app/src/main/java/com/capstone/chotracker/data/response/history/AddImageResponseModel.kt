package com.capstone.chotracker.data.response.history

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddImageResponseModel (

    @SerializedName("Data")
    val data: String,

    @SerializedName("Message")
    val message: String

): Parcelable

