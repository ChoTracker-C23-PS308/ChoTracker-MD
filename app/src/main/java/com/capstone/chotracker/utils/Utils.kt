package com.capstone.chotracker.utils

import android.text.TextUtils
import android.util.Patterns

private const val PASSWORD_MIN_LENGTH = 8

fun validateEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validatePassword(password: String): Boolean {
    return !TextUtils.isEmpty(password) && password.length >= PASSWORD_MIN_LENGTH
}
