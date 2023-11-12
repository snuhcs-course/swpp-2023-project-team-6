package com.example.speechbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.AccountSettingsAlert
import com.example.speechbuddy.ui.models.AccountSettingsUiState
import com.example.speechbuddy.utils.ResponseCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject internal constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
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
            repository.logout().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        /* TODO: 디바이스에 저장돼 있는 유저 정보 초기화(토큰 말고) */
                        sessionManager.clearAuthToken()
                    }
                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        showAlert(AccountSettingsAlert.CONNECTION)
                    }
                }
            }
        }
    }

    fun withdraw() {
        viewModelScope.launch {
            repository.withdraw().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        /* TODO: 디바이스에 저장돼 있는 유저 정보 초기화(토큰 말고) */
                        sessionManager.clearAuthToken()
                    }
                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        showAlert(AccountSettingsAlert.CONNECTION)
                    }
                }
            }
        }
    }

}