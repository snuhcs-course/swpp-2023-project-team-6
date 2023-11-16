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

@JsonClass(generateAdapter = true)
data class AuthSendCodeRequest(
    val email: String
)

@JsonClass(generateAdapter = true)
data class AuthVerifyEmailRequest(
    val email: String, val code: String
)

@JsonClass(generateAdapter = true)
data class AuthResetPasswordRequest(
    val password: String
)

@JsonClass(generateAdapter = true)
data class AuthRefreshRequest(
    val refresh: String
)