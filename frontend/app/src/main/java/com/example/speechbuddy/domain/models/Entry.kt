package com.example.speechbuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Entry {
    val id: Int
    val text: String
    val imageResId: Int
}

@Parcelize
data class Category(
    override val id: Int,
    override val text: String,
    override val imageResId: Int
) : Parcelable, Entry

@Parcelize
data class Symbol(
    override val id: Int,
    override val text: String,
    override val imageResId: Int,
    val categoryId: Int,
    val isFavorite: Boolean,
    val isMine: Boolean
) : Parcelable, Entry