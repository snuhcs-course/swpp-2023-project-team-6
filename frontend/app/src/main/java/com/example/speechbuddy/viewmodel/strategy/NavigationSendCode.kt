package com.example.speechbuddy.viewmodel.strategy

import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.viewmodel.EmailVerificationViewModel

class NavigationSendCode {
    private lateinit var sendCodeStrategy: SendCodeStrategy
    private lateinit var source : String

    fun setSource(source: String) {
        this.source = source
        if(source=="signup") {
            sendCodeStrategy = SendCodeForSignupStrategy()
        } else if(source=="reset_password") {
            sendCodeStrategy = SendCodeForResetPasswordStrategy()
        }
    }

    fun sendCode(viewModel: EmailVerificationViewModel, repository: AuthRepository) {
        sendCodeStrategy.sendCode(viewModel, repository)
    }
}