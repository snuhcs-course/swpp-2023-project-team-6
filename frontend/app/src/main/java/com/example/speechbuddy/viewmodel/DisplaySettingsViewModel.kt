package com.example.speechbuddy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.repository.SettingsRepository
import com.example.speechbuddy.ui.models.DisplaySettingsUiState
import com.example.speechbuddy.ui.models.InitialPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisplaySettingsViewModel @Inject internal constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val initialPageBoolean = getInitialPage()
    private val initialInitialPage = if (initialPageBoolean) {
        InitialPage.SYMBOL_SELECTION
    } else {
        InitialPage.TEXT_TO_SPEECH
    }

    private val _uiState = MutableStateFlow(DisplaySettingsUiState(
        getDarkMode(), initialInitialPage))
    val uiState: StateFlow<DisplaySettingsUiState> = _uiState.asStateFlow()

    fun setDarkMode(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isDarkModeEnabled = value
            )
        }
        viewModelScope.launch {
            repository.setDarkMode(value)
        }
    }

    fun setInitialPage(page: InitialPage) {
        _uiState.update { currentState ->
            currentState.copy(
                initialPage = page
            )
        }
        viewModelScope.launch {
            repository.setInitialPage(page)
        }
    }

    fun getDarkMode(): Boolean {
        var darkMode: Boolean = false
        viewModelScope.launch {
            repository.getDarkMode().collect {
                darkMode = it.data?: false
            }
        }
        return darkMode
    }

    fun getInitialPage(): Boolean {
        var initialPage: Boolean = true
        viewModelScope.launch {
            repository.getInitialPage().collect {
                initialPage = it.data?: true
            }
        }
        return initialPage
    }
}