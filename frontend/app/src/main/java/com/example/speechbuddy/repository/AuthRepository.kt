package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.AuthTokenPrefsManager
import com.example.speechbuddy.data.remote.AuthTokenRemoteSource
import com.example.speechbuddy.data.remote.models.AuthTokenDtoMapper
import com.example.speechbuddy.data.remote.models.ErrorResponseMapper
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthResetPasswordRequest
import com.example.speechbuddy.data.remote.requests.AuthSendCodeRequest
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailRequest
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.service.AuthService
import com.example.speechbuddy.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val authTokenPrefsManager: AuthTokenPrefsManager,
    private val authTokenRemoteSource: AuthTokenRemoteSource,
    private val authTokenDtoMapper: AuthTokenDtoMapper,
    private val errorResponseMapper: ErrorResponseMapper,
) {

    suspend fun signup(authSignupRequest: AuthSignupRequest): Flow<Response<Void>> = flow {
        val result = authService.signup(authSignupRequest)
        emit(result)
    }

    suspend fun login(authLoginRequest: AuthLoginRequest): Flow<Resource<AuthToken>> {
        return authTokenRemoteSource.loginAuthToken(authLoginRequest).map { response ->
            if (response.isSuccessful && response.code() == 200) {
                response.body()?.let { authTokenDto ->
                    authTokenDto.let {
                        Resource.success(
                            authTokenDtoMapper.mapToDomainModel(
                                authTokenDto
                            )
                        )
                    }
                } ?: returnUnknownError()
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMsgKey = errorResponseMapper.mapToDomainModel(responseBody).key
                    Resource.error(
                        errorMsgKey, null
                    )
                } ?: returnUnknownError()
            }
        }
    }

    suspend fun sendCodeForSignup(authSendCodeRequest: AuthSendCodeRequest): Flow<Response<Void>> =
        flow {
            val result = authService.sendCodeForSignup(authSendCodeRequest)
            emit(result)
        }

    suspend fun sendCodeForResetPassword(authSendCodeRequest: AuthSendCodeRequest): Flow<Response<Void>> =
        flow {
            val result = authService.sendCodeForResetPassword(authSendCodeRequest)
            emit(result)
        }

    suspend fun verifyEmailForSignup(authVerifyEmailRequest: AuthVerifyEmailRequest): Flow<Response<Void>> =
        flow {
            val result = authService.verifyEmailForSignup(authVerifyEmailRequest)
            emit(result)
        }

    suspend fun verifyEmailForResetPassword(authVerifyEmailRequest: AuthVerifyEmailRequest): Flow<Resource<AuthToken>> {
        return authTokenRemoteSource.verifyEmailForResetPasswordAuthToken(
            authVerifyEmailRequest
        ).map { response ->
            if (response.isSuccessful && response.code() == 200) {
                response.body()?.let { authTokenDto ->
                    authTokenDto.let {
                        Resource.success(
                            authTokenDtoMapper.mapToDomainModel(
                                authTokenDto
                            )
                        )
                    }
                } ?: returnUnknownError()
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMsgKey = errorResponseMapper.mapToDomainModel(responseBody).key
                    Resource.error(
                        errorMsgKey, null
                    )
                } ?: returnUnknownError()
            }
        }
    }

    suspend fun resetPassword(authResetPasswordRequest: AuthResetPasswordRequest): Flow<Response<Void>> =
        flow {
            val result = authService.resetPassword(authResetPasswordRequest)
            emit(result)
        }

    private fun returnUnknownError(): Resource<AuthToken> {
        return Resource.error(
            "Unknown error", null
        )
    }

}