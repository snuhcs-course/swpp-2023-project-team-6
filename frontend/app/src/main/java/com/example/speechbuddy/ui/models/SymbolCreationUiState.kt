package com.example.speechbuddy.ui.models

data class SymbolCreationUiState(
    val isValidSymbolText: Boolean = false,
    val isValidCategory: Boolean = false,
    val isValidPhotoInput: Boolean = false,
    val isCategoryExpanded: Boolean = false,
    val error: SymbolCreationError? = null,
    val loading: Boolean = false,
    val buttonEnabled: Boolean = true
)

data class SymbolCreationError(
    val type: SymbolCreationErrorType,
    val messageId: Int
)

enum class SymbolCreationErrorType {
    SYMBOL_TEXT,
    CATEGORY,
    PHOTO_INPUT,
    CONNECTION
}

enum class PhotoType {
    GALLERY,
    CAMERA
}

enum class DialogState {
    SHOW,
    HIDE
}

enum class ToastState {
    SHOW,
    HIDE
}