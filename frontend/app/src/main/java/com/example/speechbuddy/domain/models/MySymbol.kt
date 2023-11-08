package com.example.speechbuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MySymbol(
    val id: Int? = null,
    val imageUrl: String? = null
) : Parcelable