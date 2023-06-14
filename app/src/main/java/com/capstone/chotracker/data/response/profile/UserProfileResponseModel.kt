package com.capstone.chotracker.data.response.profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfileResponseModel(

	@field:SerializedName("data")
	val data: GetUserModel,

	@field:SerializedName("message")
	val message: String

): Parcelable