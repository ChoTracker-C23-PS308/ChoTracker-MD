package com.capstone.chotracker.data.response.profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateUserResponseModel(

	@field:SerializedName("data")
	val data: String,

	@field:SerializedName("message")
	val message: String

): Parcelable
