package com.example.speechbuddy.viewmodel

import androidx.lifecycle.ViewModel
import com.example.speechbuddy.ui.models.DisplaySettingsUiState
import com.example.speechbuddy.ui.models.InitialPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DisplaySettingsViewModel @Inject internal constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DisplaySettingsUiState())
    val uiState: StateFlow<DisplaySettingsUiState> = _uiState.asStateFlow()

    fun setDarkMode(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isDarkModeEnabled = value
            )
        }
    }

    fun setInitialPage(page: InitialPage) {
        _uiState.update { currentState ->
            currentState.copy(
                initialPage = page
            )
        }
    }
}