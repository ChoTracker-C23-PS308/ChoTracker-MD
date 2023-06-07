package com.capstone.chotracker.data.response.chotrack

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChotrackResponseModel (

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("prediction")
    val prediction: Float? = null

): Parcelable