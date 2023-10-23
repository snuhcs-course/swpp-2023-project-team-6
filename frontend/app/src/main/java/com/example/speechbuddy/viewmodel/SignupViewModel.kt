package com.example.speechbuddy.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.LoginError
import com.example.speechbuddy.ui.models.LoginErrorType
import com.example.speechbuddy.ui.models.LoginUiState
import com.example.speechbuddy.ui.models.SignupError
import com.example.speechbuddy.ui.models.SignupErrorType
import com.example.speechbuddy.ui.models.SignupUiState
import com.example.speechbuddy.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject internal constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    var emailInput by mutableStateOf("jonokil743@scubalm.com")
        private set

    var nicknameInput by mutableStateOf("")
        private set

    var passwordInput by mutableStateOf("")
        private set

    var passwordCheckInput by mutableStateOf("")
        private set

    fun setNickname(input: String) {
        nicknameInput = input
    }

    fun setPassword(input: String) {
        passwordInput = input
    }

    fun setPasswordCheck(input: String) {
        passwordCheckInput = input
    }

    fun validateNickname() {
        if (nicknameInput.isNotEmpty()) {
            _uiState.update { currentSate ->
                currentSate.copy(
                    isValidNickname = true,
                    error = null
                )
            }
        }
    }

    fun validatePassword() {
        // Check password length
        if (isValidPassword(passwordInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = true,
                    error = null
                )
            }
        }
        // Compare input with check
        if (passwordInput == passwordCheckInput) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = true,
                    error = null
                )
            }
        }
    }

    fun clearInputs() {
        nicknameInput = ""
        passwordInput = ""
        passwordCheckInput = ""
    }

    fun signUp() {
        if (nicknameInput.isBlank()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidNickname = false,
                    error = SignupError(
                        type = SignupErrorType.NICKNAME,
                        messageId = R.string.nicknaem_length_error
                    )
                )
            }
        } else if (!isValidPassword(passwordInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = SignupError(
                        type = SignupErrorType.PASSWORD,
                        messageId = R.string.false_new_password
                    )
                )
            }
        } else if (passwordInput != passwordCheckInput) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = false,
                    error = SignupError(
                        type = SignupErrorType.PASSWORDCHECK,
                        messageId = R.string.false_new_password_check
                    )
                )
            }
        } else {
            viewModelScope.launch {
                repository.signup(
                    AuthSignupRequest(
                        email = emailInput,
                        nickname = nicknameInput,
                        password = passwordInput
                    )
                ).collect {
                    /*TODO*/
                }
            }
            clearInputs()
        }
    }
}