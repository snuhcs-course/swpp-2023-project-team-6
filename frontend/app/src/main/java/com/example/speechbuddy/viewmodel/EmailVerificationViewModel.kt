package com.example.speechbuddy.viewmodel

import android.util.Log
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
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.Status
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

    private fun clearVerifyNumberInput() {
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

    fun verifySend(source:String?) {
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
        } else if(source=="signup") {
            viewModelScope.launch {
                repository.verifySendSignup(
                    AuthVerifyEmailSendRequest(
                        email = emailInput
                    )
                ).collect { result ->
                    when(result.status){
                        Status.SUCCESS-> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isSuccessfulSend = true,
                                    error = null
                                )
                            }
                        }
                        Status.ERROR-> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidEmail = false,
                                    error = EmailVerificationError(
                                        type = EmailVerificationErrorType.EMAIL,
                                        // 일단 임시적인 처리! 백의 응답에 따라 text가 달라지도록 수정해야 함
                                        messageId = R.string.email_already_taken
                                    )
                                )
                            }
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            }
        } else { // if it's for resetting password, 백의 에러메시지를 활용할 수 있도록 하면 코드의 중복을 더 줄일 수 있을듯
            viewModelScope.launch {
                repository.verifySendPW(
                    AuthVerifyEmailSendRequest(
                        email = emailInput
                    )
                ).collect { result ->
                    when(result.status){
                        Status.SUCCESS-> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isSuccessfulSend = true,
                                    error = null
                                )
                            }
                        }
                        Status.ERROR-> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidEmail = false,
                                    error = EmailVerificationError(
                                        type = EmailVerificationErrorType.EMAIL,
                                        // 일단 임시적인 처리! 백의 응답에 따라 text가 달라지도록 수정해야 함
                                        messageId = R.string.no_such_user
                                    )
                                )
                            }
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            }
        }
    }

    fun verifyAccept(source: String?, onNextClick: () -> Unit) {
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
        else if (source=="signup"){
            viewModelScope.launch {
                repository.verifyAcceptSignup(
                    AuthVerifyEmailAcceptRequest(
                        email = emailInput,
                        code = verifyNumberInput
                    )
                ).collect { result ->
                    when(result.status){
                        Status.SUCCESS-> {
                            onNextClick()
                        }
                        Status.ERROR-> {
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
                        Status.LOADING -> {
                        }
                    }
                }
            }
            clearVerifyNumberInput()
        } else {
            viewModelScope.launch {
                repository.verifyAcceptPW(
                    AuthVerifyEmailAcceptRequest(
                        email = emailInput,
                        code = verifyNumberInput
                    )
                ).collect { result ->
                    when(result.status){
                        Status.SUCCESS-> {
                            // access token 저장 후
                            onNextClick()
                        }
                        Status.ERROR-> {
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
                        Status.LOADING -> {
                        }
                    }
                }
            }
            clearVerifyNumberInput()
        }
    }
}

