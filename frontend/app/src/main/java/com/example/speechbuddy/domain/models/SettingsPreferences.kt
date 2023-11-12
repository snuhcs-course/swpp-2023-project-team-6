package com.example.speechbuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsPreferences(
    val darkMode: Boolean = false,
    val autoBackup: Boolean = true,
    val initialPage: Boolean = true
) : Parcelable