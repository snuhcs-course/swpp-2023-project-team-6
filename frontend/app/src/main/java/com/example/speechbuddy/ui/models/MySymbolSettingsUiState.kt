package com.example.speechbuddy.ui.models

data class MySymbolSettingsUiState(
    val mySymbolSettingsDisplayMode: MySymbolSettingsDisplayMode = MySymbolSettingsDisplayMode.SYMBOL
)

enum class MySymbolSettingsDisplayMode {
    SYMBOL,
    FAVORITE
}