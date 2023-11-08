package com.example.speechbuddy.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symbols")
data class SymbolEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    val text: String,
    val imageUrl: String?,
    val categoryId: Int,
    val isFavorite: Boolean,
    val isMine: Boolean
)