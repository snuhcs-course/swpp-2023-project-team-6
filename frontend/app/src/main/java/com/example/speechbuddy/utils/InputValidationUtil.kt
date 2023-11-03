package com.example.speechbuddy.utils

import androidx.core.util.PatternsCompat
import com.example.speechbuddy.utils.Constants.Companion.CODE_LENGTH
import com.example.speechbuddy.utils.Constants.Companion.MINIMUM_PASSWORD_LENGTH

fun isValidEmail(input: String): Boolean {
    return input.isNotBlank() && PatternsCompat.EMAIL_ADDRESS.matcher(input).matches()
}

fun isValidPassword(input: String): Boolean {
    return input.length >= MINIMUM_PASSWORD_LENGTH
}

fun isValidVerifyCode(input: String): Boolean{
    return input.length == CODE_LENGTH
}