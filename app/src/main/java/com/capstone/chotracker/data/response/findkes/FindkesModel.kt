package com.capstone.chotracker.data.response.findkes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FindkesModel(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("vicinity")
    val vicinity: String,

    @field:SerializedName("place_id")
    val placeId: String,

    @field:SerializedName("rating")
    val rating: Double?,

    @field:SerializedName("geometry")
    val geometry: GeometryModel

): Parcelable