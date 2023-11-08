package com.example.speechbuddy.data.remote

import android.util.Log
import com.example.speechbuddy.data.remote.models.AuthTokenDto
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailRequest
import com.example.speechbuddy.di.NotConnectException
import com.example.speechbuddy.service.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject

class AuthTokenRemoteSource @Inject constructor(private val authService: AuthService) {

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
            600,
            "{\"code\": 600, \"message\": \"No Internet Connection\"}".toResponseBody()
        )
    }
}