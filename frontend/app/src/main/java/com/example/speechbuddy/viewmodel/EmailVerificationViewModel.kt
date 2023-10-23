package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailAcceptRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailSendRequest
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.EmailVerificationError
import com.example.speechbuddy.ui.models.EmailVerificationErrorType
import com.example.speechbuddy.ui.models.EmailVerificationUiState
import com.example.speechbuddy.utils.isValidEmail
import com.example.speechbuddy.utils.isValidVerifyNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject internal constructor(
    private val repository: AuthRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState: StateFlow<EmailVerificationUiState> = _uiState.asStateFlow()

    var emailInput by mutableStateOf("")
        private set

    var verifyNumberInput by mutableStateOf("")
        private set

    fun setEmail(input: String) {
        emailInput = input
        if (_uiState.value.error?.type == EmailVerificationErrorType.EMAIL) validateEmail()
    }

    fun setVerifyNumber(input: String) {
        verifyNumberInput = input
        if (_uiState.value.error?.type == EmailVerificationErrorType.VERIFY_NUMBER) validateVerifyNumber()
    }

    private fun clearInputs() {
        emailInput = ""
        verifyNumberInput = ""
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

    private fun validateVerifyNumber() {
        if (isValidVerifyNumber(verifyNumberInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidVerifyNumber = true,
                    error = null
                )
            }
        }
    }

    fun verifySendSignup() {
        if (!isValidEmail(emailInput)){
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = false,
                    error = EmailVerificationError(
                        type = EmailVerificationErrorType.EMAIL,
                        messageId = R.string.false_email
                    )
                )
            }
        } else {
            viewModelScope.launch {
                repository.verifySendSignup(
                    AuthVerifyEmailSendRequest(
                        email = emailInput
                    )
                ).collect {
                    /*TODO*/
                }
            }
        }
    }

    fun verifySendPW() {
        if (!isValidEmail(emailInput)){
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = false,
                    error = EmailVerificationError(
                        type = EmailVerificationErrorType.EMAIL,
                        messageId = R.string.false_email
                    )
                )
            }
        } else {
            viewModelScope.launch {
                repository.verifySendPW(
                    AuthVerifyEmailSendRequest(
                        email = emailInput
                    )
                ).collect {
                    /*TODO*/
                }
            }
        }
    }

    fun verifyAcceptSignup() {
        if (!isValidVerifyNumber(verifyNumberInput)){
            _uiState.update { currentState ->
                currentState.copy(
                    isValidVerifyNumber = false,
                    error = EmailVerificationError(
                        type = EmailVerificationErrorType.VERIFY_NUMBER,
                        messageId = R.string.false_validation_number
                    )
                )
            }
        }
        else {
            viewModelScope.launch {
                repository.verifyAcceptSignup(
                    AuthVerifyEmailAcceptRequest(
                        email = emailInput,
                        code = verifyNumberInput
                    )
                ).collect {
                    /*TODO*/
                }
            }
            clearInputs()
        }
    }

    fun verifyAcceptPW() {
        if (!isValidVerifyNumber(verifyNumberInput)){
            _uiState.update { currentState ->
                currentState.copy(
                    isValidVerifyNumber = false,
                    error = EmailVerificationError(
                        type = EmailVerificationErrorType.VERIFY_NUMBER,
                        messageId = R.string.false_validation_number
                    )
                )
            }
        }
        else {
            viewModelScope.launch {
                repository.verifyAcceptPW(
                    AuthVerifyEmailAcceptRequest(
                        email = emailInput,
                        code = verifyNumberInput
                    )
                ).collect {
                    /*TODO*/
                }
            }
            clearInputs()
        }
    }
}

