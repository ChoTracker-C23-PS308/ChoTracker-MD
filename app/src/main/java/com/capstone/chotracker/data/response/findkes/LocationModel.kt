package com.capstone.chotracker.data.response.findkes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationModel(

    @field:SerializedName("lat")
    val latitude: Double,

    @field:SerializedName("lng")
    val longitude: Double

): Parcelable
