package com.example.speechbuddy.ui.models

data class SymbolCreationUiState (
    val isValidSymbolText: Boolean = false,
    val isValidCategory: Boolean = false,
    val isValidPhotoInput: Boolean = false,
    val isCategoryExpanded: Boolean = false,
    val error: SymbolCreationError? = null
)

data class SymbolCreationError(
    val type: SymbolCreationErrorType,
    val messageId: Int
)

enum class SymbolCreationErrorType {
    SYMBOL_TEXT,
    CATEGORY,
    PHOTO_INPUT
}