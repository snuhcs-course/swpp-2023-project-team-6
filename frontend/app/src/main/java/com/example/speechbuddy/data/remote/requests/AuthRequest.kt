package com.example.speechbuddy.data.remote.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthSignupRequest(
    val email: String, val password: String, val nickname: String
)

@JsonClass(generateAdapter = true)
data class AuthLoginRequest(
    val email: String, val password: String
)