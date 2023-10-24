package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.LoginError
import com.example.speechbuddy.ui.models.LoginErrorType
import com.example.speechbuddy.ui.models.LoginUiState
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.Status
import com.example.speechbuddy.utils.isValidEmail
import com.example.speechbuddy.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject internal constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    var emailInput by mutableStateOf("")
        private set

    var passwordInput by mutableStateOf("")
        private set

    private var _loginResult = MutableLiveData<Resource<AuthToken>>()
    val loginResult: LiveData<Resource<AuthToken>> = _loginResult

    fun setEmail(input: String) {
        emailInput = input
        if (_uiState.value.error?.type == LoginErrorType.EMAIL) validateEmail()
    }

    fun setPassword(input: String) {
        passwordInput = input
        if (_uiState.value.error?.type == LoginErrorType.PASSWORD) validatePassword()
    }

    private fun clearInputs() {
        emailInput = ""
        passwordInput = ""
    }

    private fun validateEmail() {
        if (isValidEmail(emailInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = true,
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

    fun login() {
        if (!isValidEmail(emailInput) && emailInput.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = false,
                    error = LoginError(
                        type = LoginErrorType.EMAIL,
                        messageId = R.string.false_email
                    )
                )
            }
        } else if (!isValidPassword(passwordInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = LoginError(
                        type = LoginErrorType.PASSWORD,
                        messageId = R.string.false_password
                    )
                )
            }
        } else {
            viewModelScope.launch {
                repository.login(
                    AuthLoginRequest(
                        email = emailInput,
                        password = passwordInput
                    )
                ).collect {
                    if (it.status == Resource(Status.SUCCESS, "", "").status) { // 200
                        _loginResult.postValue(it)
                    } else { // status:error
                        // when password is wrong
                        if (it.message?.contains("password", ignoreCase = true) == true) {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidPassword = false,
                                    error = LoginError(
                                        type = LoginErrorType.PASSWORD,
                                        messageId = R.string.false_password
                                    )
                                )
                            }
                        } else if (it.message?.contains("email", ignoreCase = true) == true
                        ) { // email is wrong
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidEmail = false,
                                    error = LoginError(
                                        type = LoginErrorType.EMAIL,
                                        messageId = R.string.false_email
                                    )
                                )
                            }
                        }
                        _loginResult.postValue(it)
                    }
                }
            }
        }
        //clearInputs()
    }
}