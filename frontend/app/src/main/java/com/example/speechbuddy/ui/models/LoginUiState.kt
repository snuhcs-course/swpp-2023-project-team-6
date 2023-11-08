package com.example.speechbuddy.ui.models

data class LoginUiState(
    val isValidEmail: Boolean = false,
    val isValidPassword: Boolean = false,
    val error: LoginError? = null,
    val loading: Boolean = false
)

data class LoginError(
    val type: LoginErrorType,
    val messageId: Int
)

enum class LoginErrorType {
    EMAIL,
    PASSWORD,
    CONNECTION
}