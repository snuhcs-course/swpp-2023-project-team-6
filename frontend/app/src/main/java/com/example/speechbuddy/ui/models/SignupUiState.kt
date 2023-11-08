package com.example.speechbuddy.ui.models

data class SignupUiState(
    val isValidEmail: Boolean = false,
    val isValidNickname: Boolean = false,
    val isValidPassword: Boolean = false,
    val error: SignupError? = null,
    val loading: Boolean = false
)

data class SignupError(
    val type: SignupErrorType,
    val messageId: Int
)

enum class SignupErrorType {
    NICKNAME,
    PASSWORD,
    PASSWORD_CHECK
}