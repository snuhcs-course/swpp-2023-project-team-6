package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.models.ErrorResponseMapper
import com.example.speechbuddy.data.remote.requests.AuthSendCodeRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailRequest
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.EmailVerificationError
import com.example.speechbuddy.ui.models.EmailVerificationErrorType
import com.example.speechbuddy.ui.models.EmailVerificationUiState
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.Status
import com.example.speechbuddy.utils.isValidEmail
import com.example.speechbuddy.utils.isValidVerifyCode
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

    private val errorResponseMapper = ErrorResponseMapper()

    var emailInput by mutableStateOf("")
        private set

    var verifyCodeInput by mutableStateOf("")
        private set

    fun setEmail(input: String) {
        emailInput = input
        if (_uiState.value.error?.type == EmailVerificationErrorType.EMAIL) validateEmail()
    }

    fun setVerifyCode(input: String) {
        verifyCodeInput = input
        if (_uiState.value.error?.type == EmailVerificationErrorType.VERIFY_CODE) validateVerifyCode()
    }

    private fun clearVerifyCodeInput() {
        verifyCodeInput = ""
    }

    private fun changeLoading() {
        _uiState.update {
            it.copy(loading = !it.loading)
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

    private fun validateVerifyCode() {
        if (isValidVerifyCode(verifyCodeInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidVerifyCode = true,
                    error = null
                )
            }
        }
    }

    fun sendCode(source: String?) {
        val sendCode =
            if (source == "signup")
                repository::sendCodeForSignup
            else
                repository::sendCodeForResetPassword

        if (!isValidEmail(emailInput)) {
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
            changeLoading()
            viewModelScope.launch {
                sendCode(
                    AuthSendCodeRequest(
                        email = emailInput
                    )
                ).collect { result ->
                    when (result.code()) {
                        ResponseCode.SUCCESS.value -> {
                            changeLoading()
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isSuccessfulSend = true,
                                    error = null
                                )
                            }
                        }


                        ResponseCode.BAD_REQUEST.value -> {
                            changeLoading()
                            val messageId =
                                when (errorResponseMapper.mapToDomainModel(result.errorBody()!!).key) {
                                    "email" -> R.string.wrong_email
                                    "already_taken" -> R.string.email_already_taken
                                    "no_user" -> R.string.unregistered_email
                                    else -> R.string.unknown_error
                                }
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isValidEmail = false,
                                    error = EmailVerificationError(
                                        type = EmailVerificationErrorType.EMAIL,
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
                                    error = EmailVerificationError(
                                        type = EmailVerificationErrorType.CONNECTION,
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

    fun verifyEmail(source: String?, navController: NavHostController) {
        if (!isValidVerifyCode(verifyCodeInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidVerifyCode = false,
                    error = EmailVerificationError(
                        type = EmailVerificationErrorType.VERIFY_CODE,
                        messageId = R.string.wrong_code
                    )
                )
            }
        } else {
            changeLoading()
            if (source == "signup") verifyEmailForSignup(navController)
            else verifyEmailForResetPassword(navController)
        }
        clearVerifyCodeInput()
    }

    private fun verifyEmailForSignup(navController: NavHostController) {
        viewModelScope.launch {
            repository.verifyEmailForSignup(
                AuthVerifyEmailRequest(
                    email = emailInput,
                    code = verifyCodeInput
                )
            ).collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        changeLoading()
                        navController.navigate("signup/$emailInput")
                    }

                    ResponseCode.BAD_REQUEST.value -> {
                        changeLoading()
                        _uiState.update { currentState ->
                            currentState.copy(
                                isValidVerifyCode = false,
                                error = EmailVerificationError(
                                    type = EmailVerificationErrorType.VERIFY_CODE,
                                    messageId = R.string.wrong_code
                                )
                            )
                        }
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        changeLoading()
                        _uiState.update { currentState ->
                            currentState.copy(
                                isValidVerifyCode = false,
                                error = EmailVerificationError(
                                    type = EmailVerificationErrorType.CONNECTION,
                                    messageId = R.string.connection_error
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun verifyEmailForResetPassword(navController: NavHostController) {
        viewModelScope.launch {
            repository.verifyEmailForResetPassword(
                AuthVerifyEmailRequest(
                    email = emailInput,
                    code = verifyCodeInput
                )
            ).collect { resource ->
                if (resource.status == Status.SUCCESS) {
                    changeLoading()
                    val temporaryToken = resource.data?.accessToken
                    sessionManager.setTemporaryToken(temporaryToken)
                    navController.navigate("reset_password")
                } else if (resource.message?.contains("Unknown") == true) {
                    changeLoading()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidVerifyCode = false,
                            error = EmailVerificationError(
                                type = EmailVerificationErrorType.VERIFY_CODE,
                                messageId = R.string.connection_error
                            )
                        )
                    }
                } else {
                    changeLoading()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidVerifyCode = false,
                            error = EmailVerificationError(
                                type = EmailVerificationErrorType.VERIFY_CODE,
                                messageId = R.string.wrong_code
                            )
                        )
                    }
                }
            }
        }
    }
}