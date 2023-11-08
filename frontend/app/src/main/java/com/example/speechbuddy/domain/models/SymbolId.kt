package com.example.speechbuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SymbolId(
    val id: Int? = null
) : Parcelable