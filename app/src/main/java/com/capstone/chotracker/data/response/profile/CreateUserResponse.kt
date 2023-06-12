package com.capstone.chotracker.data.response.profile

import com.google.gson.annotations.SerializedName

data class CreateUserResponse(

	@field:SerializedName("data")
	val data: String,

	@field:SerializedName("message")
	val message: String
)
