package com.capstone.chotracker.data.response.chobot

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChobotResponseModel (

    @field:SerializedName("message")
    val message: String

): Parcelable
