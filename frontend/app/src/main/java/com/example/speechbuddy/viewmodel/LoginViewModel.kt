package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.repository.SettingsRepository
import com.example.speechbuddy.repository.UserRepository
import com.example.speechbuddy.ui.models.LoginError
import com.example.speechbuddy.ui.models.LoginErrorType
import com.example.speechbuddy.ui.models.LoginUiState
import com.example.speechbuddy.utils.Status
import com.example.speechbuddy.utils.isValidEmail
import com.example.speechbuddy.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject internal constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    var emailInput by mutableStateOf("")
        private set

    var passwordInput by mutableStateOf("")
        private set

    private fun changeLoadingState() {
        _uiState.update { currentState ->
            currentState.copy(
                loading = !currentState.loading,
                buttonEnabled = !currentState.buttonEnabled
            )
        }
    }

    fun setEmail(input: String) {
        emailInput = input
        if (_uiState.value.error?.type == LoginErrorType.EMAIL) validateEmail()
    }

    fun setPassword(input: String) {
        passwordInput = input
        if (_uiState.value.error?.type == LoginErrorType.PASSWORD) validatePassword()
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
        if (emailInput.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = false,
                    error = LoginError(
                        type = LoginErrorType.EMAIL,
                        messageId = R.string.no_email
                    )
                )
            }
        } else if (!isValidEmail(emailInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidEmail = false,
                    error = LoginError(
                        type = LoginErrorType.EMAIL,
                        messageId = R.string.wrong_email
                    )
                )
            }
        } else if (passwordInput.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = LoginError(
                        type = LoginErrorType.PASSWORD,
                        messageId = R.string.no_password
                    )
                )
            }
        } else if (!isValidPassword(passwordInput)) {
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = LoginError(
                        type = LoginErrorType.PASSWORD,
                        messageId = R.string.wrong_password
                    )
                )
            }
        } else {
            changeLoadingState()
            viewModelScope.launch {
                authRepository.login(
                    AuthLoginRequest(
                        email = emailInput,
                        password = passwordInput
                    )
                ).collect { resource ->
                    if (resource.status == Status.SUCCESS) {
                        // AccessToken is already saved in AuthTokenPrefsManager by the authRepository
                        sessionManager.setAuthToken(resource.data!!)

                        runBlocking {
                            val jobs = mutableListOf<Job>()
                            jobs.add(getMyInfoFromRemote(resource.data.accessToken))
                            jobs.add(getMyDisplaySettingsFromRemote(resource.data.accessToken))
                            jobs.add(getSymbolListFromRemote(resource.data.accessToken))
                            jobs.add(getFavoritesListFromRemote(resource.data.accessToken))
                            jobs.add(getWeightTableFromRemote(resource.data.accessToken))

                            jobs.joinAll()

                            changeLoadingState()
                        }
                    } else if (resource.message?.contains("email", ignoreCase = true) == true) {
                        changeLoadingState()
                        _uiState.update { currentState ->
                            currentState.copy(
                                isValidEmail = false,
                                error = LoginError(
                                    type = LoginErrorType.EMAIL,
                                    messageId = R.string.wrong_email
                                )
                            )
                        }
                    } else if (resource.message?.contains("password", ignoreCase = true) == true) {
                        changeLoadingState()
                        _uiState.update { currentState ->
                            currentState.copy(
                                isValidPassword = false,
                                error = LoginError(
                                    type = LoginErrorType.PASSWORD,
                                    messageId = R.string.wrong_password
                                )
                            )
                        }
                    } else if (resource.message?.contains("unknown", ignoreCase = true) == true) {
                        changeLoadingState()
                        _uiState.update { currentState ->
                            currentState.copy(
                                isValidEmail = false,
                                error = LoginError(
                                    type = LoginErrorType.CONNECTION,
                                    messageId = R.string.connection_error
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getMyInfoFromRemote(accessToken: String?): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            userRepository.getMyInfoFromRemote(accessToken).collect { resource ->
                if (resource.status == Status.SUCCESS) {
                    sessionManager.setUserId(resource.data!!.id)
                } else if (resource.message?.contains("unknown", ignoreCase = true) == true) {
                    changeLoadingState()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidEmail = false,
                            error = LoginError(
                                type = LoginErrorType.CONNECTION,
                                messageId = R.string.connection_error
                            )
                        )
                    }
                } else {
                    changeLoadingState()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidPassword = false,
                            error = LoginError(
                                type = LoginErrorType.UNKNOWN,
                                messageId = R.string.unknown_error
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getMyDisplaySettingsFromRemote(accessToken: String?): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.getDisplaySettingsFromRemote(accessToken).collect { resource ->
                if (resource.message?.contains("unknown", ignoreCase = true) == true) {
                    changeLoadingState()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidEmail = false,
                            error = LoginError(
                                type = LoginErrorType.CONNECTION,
                                messageId = R.string.connection_error
                            )
                        )
                    }
                } else {
                    changeLoadingState()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidPassword = false,
                            error = LoginError(
                                type = LoginErrorType.UNKNOWN,
                                messageId = R.string.unknown_error
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getSymbolListFromRemote(accessToken: String?): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.getSymbolListFromRemote(accessToken).collect { resource ->
                if (resource.message?.contains("unknown", ignoreCase = true) == true) {
                    changeLoadingState()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidEmail = false,
                            error = LoginError(
                                type = LoginErrorType.CONNECTION,
                                messageId = R.string.connection_error
                            )
                        )
                    }
                } else {
                    changeLoadingState()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidPassword = false,
                            error = LoginError(
                                type = LoginErrorType.UNKNOWN,
                                messageId = R.string.unknown_error
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getFavoritesListFromRemote(accessToken: String?): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.getFavoritesListFromRemote(accessToken).collect { resource ->
                if (resource.message?.contains("unknown", ignoreCase = true) == true) {
                    changeLoadingState()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidEmail = false,
                            error = LoginError(
                                type = LoginErrorType.CONNECTION,
                                messageId = R.string.connection_error
                            )
                        )
                    }
                } else {
                    changeLoadingState()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidPassword = false,
                            error = LoginError(
                                type = LoginErrorType.UNKNOWN,
                                messageId = R.string.unknown_error
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getWeightTableFromRemote(accessToken: String?): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.getWeightTableFromRemote(accessToken).collect { resource ->
                if (resource.message?.contains("unknown", ignoreCase = true) == true) {
                    changeLoadingState()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidEmail = false,
                            error = LoginError(
                                type = LoginErrorType.CONNECTION,
                                messageId = R.string.connection_error
                            )
                        )
                    }
                } else {
                    changeLoadingState()
                    _uiState.update { currentState ->
                        currentState.copy(
                            isValidPassword = false,
                            error = LoginError(
                                type = LoginErrorType.UNKNOWN,
                                messageId = R.string.unknown_error
                            )
                        )
                    }
                }
            }
        }
    }

    fun enterGuestMode() {
        viewModelScope.launch {
            userRepository.setGuestMode()
            sessionManager.enterGuestMode()
        }
    }

}