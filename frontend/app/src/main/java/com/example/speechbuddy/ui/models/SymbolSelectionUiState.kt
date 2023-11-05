package com.example.speechbuddy.ui.models

data class SymbolSelectionUiState(
    val isMenuExpanded: Boolean = false,
    val displayMode: DisplayMode = DisplayMode.CATEGORY
)

enum class DisplayMode {
    ALL,
    SYMBOL,
    CATEGORY,
    FAVORITE
}