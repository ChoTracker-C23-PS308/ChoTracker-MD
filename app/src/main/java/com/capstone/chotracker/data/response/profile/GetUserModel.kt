package com.capstone.chotracker.data.response.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetUserModel (

    val ID: String?,
    val Name: String,
    val Email: String,
    val BirthDate: String,
    val Gender: String,
    val ImageUrl: String?,

    ): Parcelable