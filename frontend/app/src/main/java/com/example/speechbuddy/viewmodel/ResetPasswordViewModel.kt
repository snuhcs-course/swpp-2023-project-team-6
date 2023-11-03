package com.example.speechbuddy.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthResetPasswordRequest
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.ResetPasswordError
import com.example.speechbuddy.ui.models.ResetPasswordErrorType
import com.example.speechbuddy.ui.models.ResetPasswordUiState
import com.example.speechbuddy.utils.Status
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

    private fun clearInputs() {
        passwordInput = ""
        passwordCheckInput = ""
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

    fun resetPassword(navController: NavHostController) {
        if (!isValidPassword(passwordInput)) { // Check password length
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = ResetPasswordError(
                        type = ResetPasswordErrorType.PASSWORD,
                        messageId = R.string.false_new_password
                    )
                )
            }
        } else if (passwordInput != passwordCheckInput) { // Check password equality
            _uiState.update { currentState ->
                currentState.copy(
                    error = ResetPasswordError(
                        type = ResetPasswordErrorType.PASSWORD_CHECK,
                        messageId = R.string.false_new_password_check
                    )
                )
            }
        } else {
            viewModelScope.launch {
                repository.resetPassword(
                    AuthResetPasswordRequest(
                        password = passwordInput
                    )
                ).collect { result ->
                    /* TODO: 나중에 고쳐야 함 */
                    when (result.code()) {
                        200 -> {
                            navController.navigate("login")
                        }

                        400 -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidPassword = false,
                                    error = ResetPasswordError(
                                        type = ResetPasswordErrorType.PASSWORD_CHECK,
                                        messageId = R.string.reset_password_error
                                    )
                                )
                            }
                        }
                    }
                }
                clearInputs()
            }
        }
    }
}