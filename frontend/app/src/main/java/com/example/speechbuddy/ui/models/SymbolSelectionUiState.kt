package com.example.speechbuddy.ui.models

import com.example.speechbuddy.domain.models.Symbol

data class SymbolSelectionUiState(
    val displayMode: DisplayMode = DisplayMode.CATEGORY
)

enum class DisplayMode {
    ALL,
    SYMBOL,
    CATEGORY,
    FAVORITE
}

/**
 * This data class is necessary to distinguish multiple same symbols
 * within the selected symbols list.
 */
data class SymbolItem(
    val id: Int, val symbol: Symbol
)