package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.AuthTokenDto
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailRequest
import com.example.speechbuddy.service.AuthService
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.ResponseHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class AuthTokenRemoteSource @Inject constructor(private val authService: AuthService) {

    private val responseHandler = ResponseHandler()

    suspend fun loginAuthToken(authLoginRequest: AuthLoginRequest): Flow<Response<AuthTokenDto>> =
        flow {
            try {
                val result = authService.login(authLoginRequest)
                emit(result)
            } catch (e: Exception) {
                emit(noInternetResponse())
            }
        }

    suspend fun verifyEmailForResetPasswordAuthToken(authVerifyEmailRequest: AuthVerifyEmailRequest): Flow<Response<AuthTokenDto>> =
        flow {
            try {
                val result = authService.verifyEmailForResetPassword(authVerifyEmailRequest)
                emit(result)
            } catch (e: Exception) {
                emit(noInternetResponse())
            }
        }

    private fun noInternetResponse(): Response<AuthTokenDto> {
        return Response.error(
            ResponseCode.NO_INTERNET_CONNECTION.value,
            responseHandler.getResponseBody(ResponseCode.NO_INTERNET_CONNECTION)
        )
    }

}