package com.example.speechbuddy.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.SignupError
import com.example.speechbuddy.ui.models.SignupErrorType
import com.example.speechbuddy.ui.models.SignupUiState
import com.example.speechbuddy.utils.Resource
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
class SignupViewModel @Inject internal constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    var emailInput by mutableStateOf("")
        private set

    var nicknameInput by mutableStateOf("")
        private set

    var passwordInput by mutableStateOf("")
        private set

    var passwordCheckInput by mutableStateOf("")
        private set

    private val _signupResult = MutableLiveData<Map<String, Any?>>()
    val signupResult: LiveData<Map<String, Any?>> = _signupResult

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
        if (_uiState.value.error?.type == SignupErrorType.PASSWORDCHECK) validatePasswordCheck()
    }

    private fun validateNickname() {
        if (nicknameInput.isNotEmpty()) {
            _uiState.update { currentSate ->
                currentSate.copy(
                    isValidNickname = true,
                    error = null
                )
            }
        }
    }

    // Check password length
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

    // Check password equality
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

    fun clearInputs() {
        nicknameInput = ""
        passwordInput = ""
        passwordCheckInput = ""
    }

    fun signUp(){
        var result : Map<String, Any?> = mapOf()

        if (nicknameInput.isBlank()) { // Check nickname
            _uiState.update { currentState ->
                currentState.copy(
                    isValidNickname = false,
                    error = SignupError(
                        type = SignupErrorType.NICKNAME,
                        messageId = R.string.nicknaem_length_error
                    )
                )
            }
        } else if (!isValidPassword(passwordInput)) { // Check password length
            _uiState.update { currentState ->
                currentState.copy(
                    isValidPassword = false,
                    error = SignupError(
                        type = SignupErrorType.PASSWORD,
                        messageId = R.string.false_new_password
                    )
                )
            }
        } else if (passwordInput != passwordCheckInput) { // Check password equality
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
                    if(it.status == Resource(Status.SUCCESS, "", "").status){ // 200
                        result = mapOf("status" to it.status, "data" to it.data, "msg" to it.message)
                        _signupResult.postValue(result)
                    }else{ // status:error
                        // get message from response
                        val regex = "\\[(.*?)\\]".toRegex()
                        val matchResult = it.message?.let { msg -> regex.find(msg) }  // Search for the pattern in the input string
                        val message = matchResult?.groups?.get(1)?.value  // Extract the value between the brackets
                        message?.replace("\"","")
                        result = mapOf("status" to it.status, "data" to it.data, "msg" to message)
                        _signupResult.postValue(result)
                    }
                }
            }
            clearInputs()
        }
    }
}