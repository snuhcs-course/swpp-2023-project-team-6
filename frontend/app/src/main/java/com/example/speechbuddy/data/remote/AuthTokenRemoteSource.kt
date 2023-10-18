package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.AuthTokenDto
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class AuthTokenRemoteSource @Inject constructor(private val userService: UserService) {

    suspend fun signupAuthToken(authSignupRequest: AuthSignupRequest): Flow<Response<Void>> =
        flow {
            val result = userService.signup(authSignupRequest)
            emit(result)
        }

    suspend fun loginAuthToken(authLoginRequest: AuthLoginRequest): Flow<Response<AuthTokenDto>> =
        flow {
            val result = userService.login(authLoginRequest)
            emit(result)
        }

}