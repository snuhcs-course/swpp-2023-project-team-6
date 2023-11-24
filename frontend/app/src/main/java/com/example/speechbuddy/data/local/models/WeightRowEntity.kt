package com.example.speechbuddy.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weighttable")
data class WeightRowEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    val weights: List<Int>
)
