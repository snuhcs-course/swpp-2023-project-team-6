package com.example.speechbuddy.ui.models

import com.example.speechbuddy.domain.models.User

data class AccountSettingsUiState(
    /**
     * TODO
     * email, nickname은 나중에 user 모델 하나로 묶어야 할 듯
     */
    val user: User? = null,
    val alert: AccountSettingsAlert? = null,
)

enum class AccountSettingsAlert {
    LOGOUT,
    WITHDRAW,
    WITHDRAW_PROCEED,
    CONNECTION
}