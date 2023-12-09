package com.example.speechbuddy.viewmodel.strategy

import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.utils.ResponseHandler
import com.example.speechbuddy.viewmodel.EmailVerificationViewModel

interface SendCodeStrategy {
    fun sendCode(viewModel: EmailVerificationViewModel, repository: AuthRepository, responseHandler: ResponseHandler)
}