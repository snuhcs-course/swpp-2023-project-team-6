package com.example.speechbuddy.ui.models

data class ResetPasswordUiState(
    val isValidPassword: Boolean = false,
    val error: ResetPasswordError? = null,
    val loading: Boolean = false,
    val buttonEnabled: Boolean = true
)

data class ResetPasswordError(
    val type: ResetPasswordErrorType,
    val messageId: Int
)

enum class ResetPasswordErrorType {
    PASSWORD,
    PASSWORD_CHECK,
    CONNECTION
}