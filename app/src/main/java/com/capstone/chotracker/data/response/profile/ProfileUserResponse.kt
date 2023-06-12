package com.capstone.chotracker.data.response.profile

import com.google.gson.annotations.SerializedName

data class ProfileUserResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("message")
	val message: String
)

data class Data(

	@field:SerializedName("Email")
	val email: String,

	@field:SerializedName("CreatedAt")
	val createdAt: String,

	@field:SerializedName("ImageUrl")
	val imageUrl: String,

	@field:SerializedName("ID")
	val iD: String,

	@field:SerializedName("Gender")
	val gender: String,

	@field:SerializedName("UpdatedAt")
	val updatedAt: String,

	@field:SerializedName("Name")
	val name: String,

	@field:SerializedName("BirthDate")
	val birthDate: String
)
