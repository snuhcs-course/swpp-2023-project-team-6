package com.example.speechbuddy.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SymbolDto(
    @Json(name = "id") val id: Int,
    @Json(name = "text") val text: String,
    @Json(name = "category") val category: Int,
    @Json(name = "image") val image: String,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class SymbolListDto(
    @Json(name = "my_symbols") val mySymbols: List<SymbolDto>
)