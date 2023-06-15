	package com.capstone.chotracker.data.response.article

	import android.os.Parcelable
	import com.google.gson.annotations.SerializedName
	import kotlinx.parcelize.Parcelize

	data class Response(

		@field:SerializedName("data")
		val data: List<DataItem>,

		@field:SerializedName("message")
		val message: String
	) {

	}
	@Parcelize
	data class DataItem(

		@field:SerializedName("ID")
		val iD: String,

		@field:SerializedName("AuthorID")
		val authorID: String,

		@field:SerializedName("JudulArticle")
		val judulArticle: String,

		@field:SerializedName("IsiArticle")
		val isiArticle: String,

		@field:SerializedName("Author")
		val author: String,

		@field:SerializedName("ImageUrl")
		val imageUrl: String,

		@field:SerializedName("CreatedAt")
		val createdAt: String,


		@field:SerializedName("UpdatedAt")
		val updatedAt: String
	): Parcelable



