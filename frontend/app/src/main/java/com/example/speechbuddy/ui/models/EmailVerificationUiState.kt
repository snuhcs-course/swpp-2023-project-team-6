package com.example.speechbuddy.ui.models

data class EmailVerificationUiState(
    val isValidEmail: Boolean = false,
    val isValidVerifyCode: Boolean = false,
    // whether verification email send API was successful
    val isSuccessfulSend: Boolean = false,
    val error: EmailVerificationError? = null
)

data class EmailVerificationError(
    val type: EmailVerificationErrorType,
    val messageId: Int
)

enum class EmailVerificationErrorType {
    EMAIL,
    VERIFY_CODE
}