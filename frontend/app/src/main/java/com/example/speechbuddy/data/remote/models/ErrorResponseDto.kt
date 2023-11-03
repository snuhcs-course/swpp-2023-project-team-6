package com.example.speechbuddy.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponseDto(
    @Json(name = "error") val error: ErrorDto? = null
)

@JsonClass(generateAdapter = true)
data class ErrorDto(
    @Json(name = "code") val code: Int? = null,
    @Json(name = "message") val message: Map<String, List<String>>? = null
)