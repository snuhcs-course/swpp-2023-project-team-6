package com.example.speechbuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthToken(
    val accessToken: String? = null, val refreshToken: String? = null
) : Parcelable

@Parcelize
data class AccessToken(
    val accessToken: String? = null
) : Parcelable