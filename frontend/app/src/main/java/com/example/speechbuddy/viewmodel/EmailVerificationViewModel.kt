package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.example.speechbuddy.R

class EmailVerificationViewModel : ViewModel() {
    private var email = mutableStateOf("")
    private var verifyNumber = mutableStateOf("")
    private var isInvalidEmail = mutableStateOf(true)
    private var isDuplicateEmail = mutableStateOf(false)
    private var isInvalidVerifyNumber = mutableStateOf(true)

    fun updateEmail(updatedEmail: String) {
        email.value = updatedEmail
    }

    fun updateVerifyNumber(updatedNumber: String) {
        verifyNumber.value = updatedNumber
    }

    fun getEmail(): String {
        isInvalidEmail.value = !".+@.+\\..+".toRegex().matches(email.toString())
        return email.value
    }

    fun getVerifyNumber(): String {
        if (verifyNumber.value == "") {
            isInvalidVerifyNumber.value = false
        }
        return verifyNumber.value
    }

    fun warnEmail(): Boolean {
        if (email.value == "") {
            return false
        }
//        TODO
//        회원가입시에는 isDuplicateEmail.value==TRUE일 때 에러 메시지
//        비밀번호 재설정시에는 FALSE일 때 에러메시지 띄워야
        if (isDuplicateEmail.value || isInvalidEmail.value) {
            return true
        }
        return false
    }

    fun warnVerifyNumber(): Boolean {
        if (isInvalidVerifyNumber.value) {
            return true
        }
        return false
    }

    @Composable
    fun warnEmailMessage(): String {
        if (email.value == "") {
            return ""
        }
        if (isInvalidEmail.value) {
            return stringResource(id = R.string.false_email)
        }
//        TODO
//        회원가입시에는 isDuplicateEmail.value==TRUE일 때 에러 메시지
//        비밀번호 재설정시에는 FALSE일 때 에러메시지 띄워야
        if (isDuplicateEmail.value) {
            return stringResource(id = R.string.email_already_taken)
        }
        return ""
    }

    @Composable
    fun warnVerifyNumberMessage(): String {
        if (verifyNumber.value == "") {
            return ""
        }
        if (isInvalidVerifyNumber.value) {
            return stringResource(id = R.string.false_validation_number)
        }
        return ""
    }
}