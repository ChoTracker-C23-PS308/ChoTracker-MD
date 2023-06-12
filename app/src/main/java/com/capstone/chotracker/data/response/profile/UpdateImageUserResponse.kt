package com.capstone.chotracker.data.response.profile

import com.google.gson.annotations.SerializedName

data class UpdateImageUserResponse(

	@field:SerializedName("data")
	val data: String,

	@field:SerializedName("message")
	val message: String
)
