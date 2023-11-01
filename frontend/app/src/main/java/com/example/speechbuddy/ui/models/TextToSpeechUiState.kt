package com.example.speechbuddy.ui.models

data class TextToSpeechUiState(
    val buttonStatus: ButtonStatusType = ButtonStatusType.PLAY
)

enum class ButtonStatusType {
    PLAY,
    STOP
}