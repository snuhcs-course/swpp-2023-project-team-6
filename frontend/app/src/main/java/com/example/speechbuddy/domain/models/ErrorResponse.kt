package com.example.speechbuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorResponse(
    val code: Int = -1,
    val key: String = "Unknown Error With Error Response",
    val description: String = "Unknown Error With Error Response"
) : Parcelable