package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailRequest
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.EmailVerificationError
import com.example.speechbuddy.ui.models.EmailVerificationErrorType
import com.example.speechbuddy.ui.models.EmailVerificationUiState
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.Status
import com.example.speechbuddy.utils.isValidCode
import com.example.speechbuddy.utils.isValidEmail
import com.example.speechbuddy.viewmodel.strategy.NavigationSendCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject internal constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState: StateFlow<EmailVerificationUiState> = _uiState.asStateFlow()

    private val source = mutableStateOf<String?>(null)

    var emailInput by mutableStateOf("")
        private set

    var codeInput by mutableStateOf("")
        private set

    fun setSource(value: String?) {
        source.value = value
    }

    fun setEmail(input: String) {
        emailInput = input
        if (_uiState.value.error?.type == EmailVerificationErrorType.EMAIL) validateEmail()
    }

    fun setCode(input: String) {
        codeInput = input
        if (_uiState.value.error?.type == EmailVerificationErrorType.CODE) validateCode()
    }

    fun changeLoadingState() {
        _uiState.update { currentState ->
            currentState.copy(
                loading = !currentState.loading,
                buttonEnabled = !currentState.buttonEnabled
            )
        }
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

    private fun validateCode() {
        if (isValidCode(codeInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidCode = true,
                    error = null
                )
            }
        }
    }

    fun sendCode() {
        if (emailInput.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = false,
                    error = EmailVerificationError(
                        type = EmailVerificationErrorType.EMAIL,
                        messageId = R.string.no_email
                    )
                )
            }
        } else if (!isValidEmail(emailInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = false,
                    error = EmailVerificationError(
                        type = EmailVerificationErrorType.EMAIL,
                        messageId = R.string.wrong_email
                    )
                )
            }
        } else {
            val navigationSendCode = NavigationSendCode()
            navigationSendCode.setSource(source.value!!)
            navigationSendCode.sendCode(this, repository)
        }
    }

    fun handleSendCodeSuccess() {
        _uiState.update { currentState ->
            currentState.copy(
                isCodeSuccessfullySent = true,
                error = null
            )
        }
    }

    fun handleSendCodeBadRequest(errorMessageId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                isValidEmail = false,
                error = EmailVerificationError(
                    type = EmailVerificationErrorType.EMAIL,
                    messageId = errorMessageId
                )
            )
        }
    }

    fun handleSendCodeNoInternetConnection() {
        _uiState.update { currentState ->
            currentState.copy(
                isValidEmail = false,
                error = EmailVerificationError(
                    type = EmailVerificationErrorType.CONNECTION,
                    messageId = R.string.connection_error
                )
            )
        }
    }

    fun handleSendCodeUnknownError() {
        _uiState.update { currentState ->
            currentState.copy(
                isValidEmail = false,
                error = EmailVerificationError(
                    type = EmailVerificationErrorType.UNKNOWN,
                    messageId = R.string.unknown_error
                )
            )
        }
    }

    fun verifyEmail(navigateCallback: (String) -> Unit) {
        if (codeInput.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidCode = false,
                    error = EmailVerificationError(
                        type = EmailVerificationErrorType.CODE,
                        messageId = R.string.no_code
                    )
                )
            }
        } else if (!isValidCode(codeInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidCode = false,
                    error = EmailVerificationError(
                        type = EmailVerificationErrorType.CODE,
                        messageId = R.string.wrong_code
                    )
                )
            }
        } else {
            if (source.value == "signup") verifyEmailForSignup(navigateCallback)
            if (source.value == "reset_password") verifyEmailForResetPassword(navigateCallback)
            else _uiState.update { currentState ->
                currentState.copy(
                    isValidCode = false,
                    error = EmailVerificationError(
                        type = EmailVerificationErrorType.UNKNOWN,
                        messageId = R.string.unknown_error
                    )
                )
            }
        }
    }

    private fun verifyEmailForSignup(navigateCallback: (String) -> Unit) {
        changeLoadingState()
        viewModelScope.launch {
            repository.verifyEmailForSignup(
                AuthVerifyEmailRequest(
                    email = emailInput,
                    code = codeInput
                )
            ).collect { result ->
                changeLoadingState()
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        navigateCallback("signup/$emailInput")
                    }

                    ResponseCode.BAD_REQUEST.value -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isValidCode = false,
                                error = EmailVerificationError(
                                    type = EmailVerificationErrorType.CODE,
                                    messageId = R.string.wrong_code
                                )
                            )
                        }
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isValidCode = false,
                                error = EmailVerificationError(
                                    type = EmailVerificationErrorType.CONNECTION,
                                    messageId = R.string.connection_error
                                )
                            )
                        }
                    }

                    else -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isValidCode = false,
                                error = EmailVerificationError(
                                    type = EmailVerificationErrorType.UNKNOWN,
                                    messageId = R.string.unknown_error
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun verifyEmailForResetPassword(navigateCallback: (String) -> Unit) {
        changeLoadingState()
        viewModelScope.launch {
            repository.verifyEmailForResetPassword(
                AuthVerifyEmailRequest(
                    email = emailInput,
                    code = codeInput
                )
            ).collect { resource ->
                changeLoadingState()
                if (resource.status == Status.SUCCESS) {
                    val temporaryToken = AuthToken(resource.data?.accessToken, null)
                    sessionManager.setAuthToken(temporaryToken)
                    navigateCallback("reset_password")
                } else if (resource.message?.contains("unknown", ignoreCase = true) == true) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidCode = false,
                            error = EmailVerificationError(
                                type = EmailVerificationErrorType.CONNECTION,
                                messageId = R.string.connection_error
                            )
                        )
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidCode = false,
                            error = EmailVerificationError(
                                type = EmailVerificationErrorType.CODE,
                                messageId = R.string.wrong_code
                            )
                        )
                    }
                }
            }
        }
    }
}