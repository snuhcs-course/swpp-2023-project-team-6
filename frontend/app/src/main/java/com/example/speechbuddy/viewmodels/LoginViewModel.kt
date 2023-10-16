package com.example.speechbuddy.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel() : ViewModel() {
    private var email = mutableStateOf("")
    private var password = mutableStateOf("")
    private var emailError = mutableStateOf(false)
    private var passwordError = mutableStateOf(false)
    fun validateEmail(input: String) {
        var matchIndex = input.indexOf('@', 0)
        setEmail(input)
        if (matchIndex == -1) {
            emailError.value = true
        } else {
            emailError.value = false
        }
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
        if (password.value.length < 8){
            passwordError.value = true
        }else{
            passwordError.value = false
        }
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