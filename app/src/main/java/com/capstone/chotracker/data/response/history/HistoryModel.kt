package com.capstone.chotracker.data.response.history

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryModel (

    val Uid: String,
    val TotalKolestrol: Float,
    val Tingkat: String,
    val ImageUrl: String

): Parcelable