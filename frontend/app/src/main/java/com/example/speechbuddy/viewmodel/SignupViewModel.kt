package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.SignupError
import com.example.speechbuddy.ui.models.SignupErrorType
import com.example.speechbuddy.ui.models.SignupUiState
import com.example.speechbuddy.utils.Constants
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.ResponseHandler
import com.example.speechbuddy.utils.isValidNickname
import com.example.speechbuddy.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject internal constructor(
    private val repository: AuthRepository,
    private val responseHandler: ResponseHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    val email = mutableStateOf<String?>(null)

    var nicknameInput by mutableStateOf("")
        private set

    var passwordInput by mutableStateOf("")
        private set

    var passwordCheckInput by mutableStateOf("")
        private set

    fun setEmail(value: String?) {
        email.value = value
    }

    fun setNickname(input: String) {
        nicknameInput = input
        if (_uiState.value.error?.type == SignupErrorType.NICKNAME) validateNickname()
    }

    fun setPassword(input: String) {
        passwordInput = input
        if (_uiState.value.error?.type == SignupErrorType.PASSWORD) validatePassword()
    }

    fun setPasswordCheck(input: String) {
        passwordCheckInput = input
        if (_uiState.value.error?.type == SignupErrorType.PASSWORD_CHECK) validatePasswordCheck()
    }

    private fun validateNickname() {
        if (nicknameInput.isNotEmpty() && nicknameInput.length <= Constants.MAXIMUM_NICKNAME_LENGTH) {
            _uiState.update { currentSate ->
                currentSate.copy(
                    isValidNickname = true,
                    error = null
                )
            }
        }
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
                    isValidEmail = true,
                    error = null
                )
            }
        }
    }

    fun signup(onSuccess: () -> Unit) {
        if (email.value == null) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = false,
                    error = SignupError(
                        type = SignupErrorType.UNKNOWN,
                        messageId = R.string.unknown_error
                    )
                )
            }
        } else if (nicknameInput.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidNickname = false,
                    error = SignupError(
                        type = SignupErrorType.NICKNAME,
                        messageId = R.string.no_nickname
                    )
                )
            }
        } else if (!isValidNickname(nicknameInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidNickname = false,
                    error = SignupError(
                        type = SignupErrorType.NICKNAME,
                        messageId = R.string.nickname_too_long
                    )
                )
            }
        } else if (passwordInput.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = SignupError(
                        type = SignupErrorType.PASSWORD,
                        messageId = R.string.no_password
                    )
                )
            }
        } else if (!isValidPassword(passwordInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = SignupError(
                        type = SignupErrorType.PASSWORD,
                        messageId = R.string.password_too_short
                    )
                )
            }
        } else if (passwordInput != passwordCheckInput) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = SignupError(
                        type = SignupErrorType.PASSWORD_CHECK,
                        messageId = R.string.wrong_password_check
                    )
                )
            }
        } else {
            viewModelScope.launch {
                repository.signup(
                    AuthSignupRequest(
                        email = email.value!!,
                        nickname = nicknameInput,
                        password = passwordInput
                    )
                ).collect { result ->
                    when (result.code()) {
                        ResponseCode.CREATED.value -> {
                            onSuccess()
                        }

                        ResponseCode.BAD_REQUEST.value -> {
                            val errorMessageId =
                                when (responseHandler.parseErrorResponse(result.errorBody()!!).key) {
                                    "email" -> R.string.wrong_email
                                    "already_taken" -> R.string.email_already_taken
                                    else -> R.string.unknown_error
                                }
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidEmail = false,
                                    error = SignupError(
                                        type = SignupErrorType.EMAIL,
                                        messageId = errorMessageId
                                    )
                                )
                            }
                        }

                        ResponseCode.NO_INTERNET_CONNECTION.value -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidEmail = false,
                                    error = SignupError(
                                        type = SignupErrorType.CONNECTION,
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