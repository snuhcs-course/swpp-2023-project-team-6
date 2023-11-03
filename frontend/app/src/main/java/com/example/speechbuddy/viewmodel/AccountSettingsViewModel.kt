package com.example.speechbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.AccountSettingsAlert
import com.example.speechbuddy.ui.models.AccountSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject internal constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountSettingsUiState())
    val uiState: StateFlow<AccountSettingsUiState> = _uiState.asStateFlow()

    fun showAlert(alert: AccountSettingsAlert) {
        _uiState.update { currentState ->
            currentState.copy(
                alert = alert
            )
        }
    }

    fun hideAlert() {
        _uiState.update { currentState ->
            currentState.copy(
                alert = null
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            //repository.logout()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            //repository.deleteAccount()
        }
    }
}