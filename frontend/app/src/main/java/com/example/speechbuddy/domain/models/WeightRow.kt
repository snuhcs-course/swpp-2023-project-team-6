package com.example.speechbuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeightRow(
    val id: Int,
    val weights: List<Int>
) : Parcelable
