package com.example.speechbuddy.ui.models

data class AccountSettingsUiState(
    /**
     * TODO
     * email, nickname은 나중에 user 모델 하나로 묶어야 할 듯
     */
    val email: String = "example@gmail.com",
    val nickname: String = "nickname",
    val alert: AccountSettingsAlert? = null
)

enum class AccountSettingsAlert {
    LOGOUT,
    WITHDRAW,
    WITHDRAW_PROCEED,
    INTERNET_ERROR
}