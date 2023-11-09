package com.example.speechbuddy.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    val email: String,
    val nickname: String
)