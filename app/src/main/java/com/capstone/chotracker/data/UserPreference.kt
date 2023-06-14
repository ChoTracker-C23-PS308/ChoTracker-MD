package com.capstone.chotracker.data

import android.content.Context
import android.content.SharedPreferences

class UserPreference private constructor(context: Context) {

    companion object {
        private const val PREF_NAME = "UserPreference"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"

        @Volatile
        private var instance: UserPreference? = null

        fun getInstance(context: Context): UserPreference {
            return instance ?: synchronized(this) {
                instance ?: UserPreference(context).also { instance = it }
            }
        }
    }

    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var namePref: String?
        get() = sharedPreferences.getString(KEY_NAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_NAME, value).apply()

    var emailPref: String?
        get() = sharedPreferences.getString(KEY_EMAIL, null)
        set(value) = sharedPreferences.edit().putString(KEY_EMAIL, value).apply()

}