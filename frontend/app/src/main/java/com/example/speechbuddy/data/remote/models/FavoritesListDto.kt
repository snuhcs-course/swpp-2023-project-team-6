package com.example.speechbuddy.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SymbolIdDto(
    @Json(name = "id") val id: Int
)

@JsonClass(generateAdapter = true)
data class FavoritesListDto(
    @Json(name = "results") val results: List<SymbolIdDto>
)