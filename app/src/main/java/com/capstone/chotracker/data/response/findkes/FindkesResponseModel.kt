package com.capstone.chotracker.data.response.findkes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FindkesResponseModel(

    @field:SerializedName("results")
    val results: List<FindkesModel>,

    @field:SerializedName("status")
    val status: String

): Parcelable