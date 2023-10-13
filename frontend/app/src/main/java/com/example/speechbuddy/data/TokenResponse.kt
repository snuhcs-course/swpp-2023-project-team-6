package com.example.speechbuddy.data

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @field:SerializedName("access") val accessToken: String,
    @field:SerializedName("refresh") val refreshToken: String
)
