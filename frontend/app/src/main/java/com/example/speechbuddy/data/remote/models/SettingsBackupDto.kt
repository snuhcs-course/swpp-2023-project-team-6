package com.example.speechbuddy.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SettingsBackupDto(
    @Json(name = "display_mode") val displayMode: Int? = null,
    @Json(name = "default_menu") val defaultMenu: Int? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)