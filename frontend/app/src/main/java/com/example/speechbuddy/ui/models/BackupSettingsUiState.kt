package com.example.speechbuddy.ui.models

data class BackupSettingsUiState(
    val lastBackupDate: String = "",
    val isAutoBackupEnabled: Boolean = true,
    val alert: BackupSettingsAlert? = null,
    val loading: Boolean = false,
    val buttonEnabled: Boolean = true
)

enum class BackupSettingsAlert {
    SUCCESS,
    CONNECTION
}