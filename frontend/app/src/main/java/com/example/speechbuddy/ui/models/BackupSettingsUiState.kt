package com.example.speechbuddy.ui.models

data class BackupSettingsUiState(
    /* TODO */
    val lastBackupDate: String = "2023.09.09",
    val isAutoBackupEnabled: Boolean = true,
    val alert: BackupSettingsAlert? = null
)

enum class BackupSettingsAlert {
    SUCCESS,
    CONNECTION
}