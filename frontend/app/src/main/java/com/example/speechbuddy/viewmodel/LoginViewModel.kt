package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private var email = mutableStateOf("")
    private var password = mutableStateOf("")
    private var emailError = mutableStateOf(false)
    private var passwordError = mutableStateOf(false)
    fun validateEmail(input: String) {
        val matchIndex = input.indexOf('@', 0)
        setEmail(input)
        emailError.value = (matchIndex == -1)
    }

    fun setEmail(input: String) {
        email.value = input
    }

    fun getEmail(): String {
        return email.value
    }

    fun getEmailError(): Boolean {
        return emailError.value
    }

    fun validatePassword(input: String) {
        setPassword(input)
        passwordError.value = (password.value.length < 8)
    }

    fun setPassword(input: String) {
        password.value = input
    }

    fun getPassword(): String {
        return password.value
    }

    fun getPasswordError(): Boolean {
        return passwordError.value
    }
}