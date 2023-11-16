package com.example.speechbuddy.ui.models

data class EmailVerificationUiState(
    val isValidEmail: Boolean = false,
    val isValidCode: Boolean = false,
    val isCodeSuccessfullySent: Boolean = false,
    val error: EmailVerificationError? = null
)

data class EmailVerificationError(
    val type: EmailVerificationErrorType,
    val messageId: Int
)

enum class EmailVerificationErrorType {
    EMAIL,
    CODE,
    CONNECTION,
    UNKNOWN
}