package com.example.speechbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.BackupSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupSettingsViewModel @Inject internal constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupSettingsUiState())
    val uiState: StateFlow<BackupSettingsUiState> = _uiState.asStateFlow()

    fun setAutoBackup(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isAutoBackupEnabled = value
            )
        }
    }

    fun backup() {
        viewModelScope.launch {
            //repository.backup()
        }
    }
}