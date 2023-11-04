package com.example.speechbuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Entry {
    val id: Int
    val text: String
}

@Parcelize
data class Category(
    override val id: Int,
    override val text: String
) : Parcelable, Entry

@Parcelize
data class Symbol(
    override val id: Int,
    override val text: String,
    val imageUrl: String?,
    val categoryId: Int,
    val isFavorite: Boolean,
    val isMine: Boolean
) : Parcelable, Entry