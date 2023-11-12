package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.models.ErrorResponseMapper
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.SignupError
import com.example.speechbuddy.ui.models.SignupErrorType
import com.example.speechbuddy.ui.models.SignupUiState
import com.example.speechbuddy.utils.Constants
import com.example.speechbuddy.utils.ResponseCode
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
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    private val errorResponseMapper = ErrorResponseMapper()

    var nicknameInput by mutableStateOf("")
        private set

    var passwordInput by mutableStateOf("")
        private set

    var passwordCheckInput by mutableStateOf("")
        private set

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

    private fun changeLoading() {
        _uiState.update {
            it.copy(loading = !it.loading)
        }
    }

    fun clearInputs() {
        nicknameInput = ""
        passwordInput = ""
        passwordCheckInput = ""
    }

    fun signup(emailInput: String, navController: NavHostController) {
        if (!isValidNickname(nicknameInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidNickname = false,
                    error = SignupError(
                        type = SignupErrorType.NICKNAME,
                        messageId = R.string.nickname_too_long
                    )
                )
            }
        } else if (!isValidPassword(passwordInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = SignupError(
                        type = SignupErrorType.PASSWORD,
                        messageId = R.string.wrong_password
                    )
                )
            }
        } else if (passwordInput != passwordCheckInput) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = false,
                    error = SignupError(
                        type = SignupErrorType.PASSWORD_CHECK,
                        messageId = R.string.wrong_password_check
                    )
                )
            }
        } else {
            changeLoading()
            viewModelScope.launch {
                repository.signup(
                    AuthSignupRequest(
                        email = emailInput,
                        nickname = nicknameInput,
                        password = passwordInput
                    )
                ).collect { result ->
                    when (result.code()) {
                        ResponseCode.CREATED.value -> {
                            changeLoading()
                            navController.navigate("login")
                        }

                        ResponseCode.BAD_REQUEST.value -> {
                            changeLoading()
                            val messageId =
                                when (errorResponseMapper.mapToDomainModel(result.errorBody()!!).key) {
                                    "email" -> R.string.wrong_email
                                    "already_taken" -> R.string.email_already_taken
                                    else -> R.string.unknown_error
                                }
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidEmail = false,
                                    error = SignupError(
                                        type = SignupErrorType.EMAIL,
                                        messageId = messageId
                                    )
                                )
                            }
                        }

                        ResponseCode.NO_INTERNET_CONNECTION.value -> {
                            changeLoading()
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
            clearInputs()
        }
    }
}