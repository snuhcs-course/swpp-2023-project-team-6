package com.example.speechbuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Symbol(
    val id: Int,
    val text: String,
    val imageResId: Int,
    val category: Category,
    val isFavorite: Boolean,
    val isMine: Boolean
): Parcelable