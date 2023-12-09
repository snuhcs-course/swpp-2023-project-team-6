package com.example.speechbuddy.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "nickname") val nickname: String? = null
)