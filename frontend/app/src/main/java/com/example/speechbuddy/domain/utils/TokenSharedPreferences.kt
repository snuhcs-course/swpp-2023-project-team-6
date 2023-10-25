package com.example.speechbuddy.domain.utils

import android.content.Context

class TokenSharedPreferences(context: Context) {
    private val prefsFilename = "token_prefs"
    private val prefs = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)

    fun setAccessToken(accessToken: String) {
        prefs.edit().putString("accessToken", accessToken).apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString("accessToken", null)
    }

    fun clearAccessToken() {
        prefs.edit().remove("accessToken").apply()
    }

    fun setRefreshToken(refreshToken: String) {
        prefs.edit().putString("refreshToken", refreshToken).apply()
    }

    fun getRefreshToken(): String? {
        return prefs.getString("refreshToken", null)
    }

    fun clearRefreshToken() {
        prefs.edit().remove("refreshToken").apply()
    }
}