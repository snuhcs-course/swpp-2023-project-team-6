package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthResetPasswordRequest
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.ResetPasswordError
import com.example.speechbuddy.ui.models.ResetPasswordErrorType
import com.example.speechbuddy.ui.models.ResetPasswordUiState
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject internal constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    var passwordInput by mutableStateOf("")
        private set

    var passwordCheckInput by mutableStateOf("")
        private set

    fun setPassword(input: String) {
        passwordInput = input
        if (_uiState.value.error?.type == ResetPasswordErrorType.PASSWORD) validatePassword()
    }

    fun setPasswordCheck(input: String) {
        passwordCheckInput = input
        if (_uiState.value.error?.type == ResetPasswordErrorType.PASSWORD_CHECK) validatePasswordCheck()
    }

    private fun validatePassword() {
        if (isValidPassword(passwordInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = true,
                    error = null
                )
            }
        }
    }

    private fun validatePasswordCheck() {
        if (passwordInput == passwordCheckInput) {
            _uiState.update { currentState ->
                currentState.copy(
                    error = null
                )
            }
        }
    }

    private fun changeLoadingState() {
        _uiState.update { currentState ->
            currentState.copy(
                loading = !currentState.loading,
                buttonEnabled = !currentState.buttonEnabled
            )
        }
    }

    fun resetPassword(onSuccess: () -> Unit) {
        if (passwordInput.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = ResetPasswordError(
                        type = ResetPasswordErrorType.PASSWORD,
                        messageId = R.string.no_password
                    )
                )
            }
        } else if (!isValidPassword(passwordInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = ResetPasswordError(
                        type = ResetPasswordErrorType.PASSWORD,
                        messageId = R.string.password_too_short
                    )
                )
            }
        } else if (passwordInput != passwordCheckInput) {
            _uiState.update { currentState ->
                currentState.copy(
                    error = ResetPasswordError(
                        type = ResetPasswordErrorType.PASSWORD_CHECK,
                        messageId = R.string.wrong_password_check
                    )
                )
            }
        } else {
            changeLoadingState()
            viewModelScope.launch {
                repository.resetPassword(
                    AuthResetPasswordRequest(
                        password = passwordInput
                    )
                ).collect { result ->
                    changeLoadingState()
                    when (result.code()) {
                        ResponseCode.SUCCESS.value -> {
                            sessionManager.nullify()
                            onSuccess()
                        }

                        ResponseCode.BAD_REQUEST.value -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidPassword = false,
                                    error = ResetPasswordError(
                                        type = ResetPasswordErrorType.PASSWORD,
                                        messageId = R.string.unknown_error
                                    )
                                )
                            }
                        }

                        ResponseCode.NO_INTERNET_CONNECTION.value -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidPassword = false,
                                    error = ResetPasswordError(
                                        type = ResetPasswordErrorType.CONNECTION,
                                        messageId = R.string.connection_error
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}