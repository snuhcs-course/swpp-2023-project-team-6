package com.example.speechbuddy.repository

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

    suspend fun sendCodeForSignup(authVerifyEmailSendRequest: AuthSendCodeRequest): Flow<Response<Void>> =
        flow {
            val result = authService.sendCodeForSignup(authVerifyEmailSendRequest)
            emit(result)
        }

    suspend fun sendCodeForResetPassword(authVerifyEmailSendRequest: AuthSendCodeRequest): Flow<Response<Void>> =
        flow {
            val result = authService.sendCodeForResetPassword(authVerifyEmailSendRequest)
            emit(result)
        }

    suspend fun verifyEmailForSignup(authVerifyEmailAcceptRequest: AuthVerifyEmailRequest): Flow<Response<Void>> =
        flow {
            val result = authService.verifyEmailForSignup(authVerifyEmailAcceptRequest)
            emit(result)
        }

    suspend fun verifyEmailForResetPassword(authVerifyEmailAcceptRequest: AuthVerifyEmailRequest): Flow<Resource<AuthToken>> {
        return authTokenRemoteSource.verifyEmailForResetPasswordAuthToken(
            authVerifyEmailAcceptRequest
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