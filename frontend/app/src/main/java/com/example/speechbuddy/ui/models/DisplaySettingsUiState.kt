package com.example.speechbuddy.ui.models

data class DisplaySettingsUiState(
    val isDarkModeEnabled: Boolean = false,
    val initialPage: InitialPage = InitialPage.SYMBOL_SELECTION
)

enum class InitialPage {
    SYMBOL_SELECTION,
    TEXT_TO_SPEECH
}