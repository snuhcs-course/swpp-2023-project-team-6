package com.example.speechbuddy.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MySymbolDto (
    @Json(name="id") val id: Int? = null,
    @Json(name="image_url") val imageUrl : String? = null
)