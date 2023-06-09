package com.capstone.chotracker.data.response.history

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryModel (

    val uid: String,
    val total_kolestrol: Float,
    val tingkat: String,
    val image_url: String

): Parcelable