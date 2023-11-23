package com.example.speechbuddy.data.remote.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BackupWeightTableRequest(
    val weight_table: List<WeightTableEntity>
)

@JsonClass(generateAdapter = true)
data class WeightTableEntity(
    val id: Int,
    val weight: String
)