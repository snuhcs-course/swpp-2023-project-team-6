package com.example.speechbuddy.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity (
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    val text: String
)