package com.example.speechbuddy.ui.models

data class LoginUiState(
    val isValidEmail: Boolean = false,
    val isValidPassword: Boolean = false,
    val error: LoginError? = null
)

data class LoginError(
    val type: LoginErrorType,
    val messageId: Int
)

enum class LoginErrorType {
    EMAIL,
    PASSWORD
}