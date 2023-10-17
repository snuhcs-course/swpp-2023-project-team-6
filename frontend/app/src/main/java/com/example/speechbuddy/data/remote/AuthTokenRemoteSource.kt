package com.example.speechbuddy.data.remote

import android.util.Log
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
            Log.d("signupAuthToken", "${result.code()}")
            Log.d("signupAuthToken", result.message())
            Log.d("signupAuthToken", "${result.body()}")
            Log.d("signupAuthToken", "${result.raw()}")
            Log.d("signupAuthToken", "${result.isSuccessful}")
            Log.d("signupAuthToken", "${result.errorBody()}")
            emit(result)
        }

    suspend fun loginAuthToken(authLoginRequest: AuthLoginRequest): Flow<Response<AuthTokenDto>> =
        flow {
            val result = userService.login(authLoginRequest)
            Log.d("loginAuthToken", "${result.code()}")
            Log.d("loginAuthToken", result.message())
            Log.d("loginAuthToken", "${result.body()}")
            Log.d("loginAuthToken", "${result.raw()}")
            Log.d("loginAuthToken", "${result.isSuccessful}")
            Log.d("loginAuthToken", "${result.errorBody()}")
            emit(result)
        }

}