package com.example.speechbuddy.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthTokenDto(
    @Json(name = "access") val accessToken: String? = null,
    @Json(name = "refresh") val refreshToken: String? = null
)