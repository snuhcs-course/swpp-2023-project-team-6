package com.example.speechbuddy.ui.models

import com.example.speechbuddy.domain.models.User

data class AccountSettingsUiState(
    val user: User? = null,
    val alert: AccountSettingsAlert? = null,
)

enum class AccountSettingsAlert {
    BACKUP,
    LOADING,
    BACKUP_SUCCESS,
    LOGOUT,
    WITHDRAW,
    WITHDRAW_PROCEED,
    CONNECTION
}