package com.example.speechbuddy.ui.models

data class TextToSpeechUiState(
    val isButtonEnabled: ButtonStatusType = ButtonStatusType.PLAY
)

enum class ButtonStatusType {
    PLAY,
    STOP
}