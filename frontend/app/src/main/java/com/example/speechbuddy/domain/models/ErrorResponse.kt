package com.example.speechbuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorResponse (
    val code : Int? = null, val key: String? = null, val description: String? = null
) : Parcelable