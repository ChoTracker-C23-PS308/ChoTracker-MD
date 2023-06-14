package com.capstone.chotracker.data.response.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel (

    val id: String?,
    val name: String,
    val email: String,
    val birth_date: String,
    val gender: String,
    val image_url: String?,

): Parcelable
