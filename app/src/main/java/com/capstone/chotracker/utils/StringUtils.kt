package com.capstone.chotracker.utils

object StringUtils {
    fun cutStringFromAPI(apiData: String?, startIndex: Int, endIndex: Int): String {
        if (apiData.isNullOrEmpty()) {
            return ""
        }

        return try {
            apiData.substring(startIndex, endIndex)
        } catch (e: IndexOutOfBoundsException) {
            // Penanganan jika indeks tidak valid
            ""
        }
    }
}
