package com.example.speechbuddy.utils

class Constants {
    companion object {
        const val BASE_URL = "http://54.180.112.72:8000/"

        const val AUTH_TOKEN_PREFS: String = "com.example.speechbuddy.AUTH_TOKEN_PREFS"
        const val ACCESS_TOKEN_PREF: String = "com.example.speechbuddy.ACCESS_TOKEN_PREF"
        const val REFRESH_TOKEN_PREF: String = "com.example.speechbuddy.REFRESH_TOKEN_PREF"

        const val MINIMUM_PASSWORD_LENGTH = 8
        const val MAXIMUM_NICKNAME_LENGTH = 15
        const val CODE_LENGTH = 6
        const val MAXIMUM_LINES_FOR_SYMBOL_TEXT = 2
    }
}