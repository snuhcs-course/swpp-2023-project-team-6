package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.AuthTokenPrefsManager
import com.example.speechbuddy.data.local.SettingsPrefsManager
import com.example.speechbuddy.data.local.UserIdPrefsManager
import com.example.speechbuddy.data.remote.AuthTokenRemoteSource
import com.example.speechbuddy.data.remote.models.AccessTokenDtoMapper
import com.example.speechbuddy.data.remote.models.AuthTokenDtoMapper
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthRefreshRequest
import com.example.speechbuddy.data.remote.requests.AuthResetPasswordRequest
import com.example.speechbuddy.data.remote.requests.AuthSendCodeRequest
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailRequest
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.AccessToken
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.service.AuthService
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.ResponseHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val userIdPrefsManager: UserIdPrefsManager,
    private val authTokenPrefsManager: AuthTokenPrefsManager,
    private val settingsPrefsManager: SettingsPrefsManager,
    private val authTokenRemoteSource: AuthTokenRemoteSource,
    private val authTokenDtoMapper: AuthTokenDtoMapper,
    private val accessTokenDtoMapper: AccessTokenDtoMapper,
    private val responseHandler: ResponseHandler,
    private val sessionManager: SessionManager
) {

    suspend fun signup(authSignupRequest: AuthSignupRequest): Flow<Response<Void>> =
        flow {
            try {
                val result = authService.signup(authSignupRequest)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun login(authLoginRequest: AuthLoginRequest): Flow<Resource<AuthToken>> {
        return authTokenRemoteSource.loginAuthToken(authLoginRequest).map { response ->
            if (response.isSuccessful && response.code() == ResponseCode.SUCCESS.value) {
                response.body()?.let { authTokenDto ->
                    authTokenDto.let {
                        val authToken = authTokenDtoMapper.mapToDomainModel(authTokenDto)
                        authTokenPrefsManager.saveAuthToken(authToken)
                        Resource.success(authToken)
                    }
                } ?: returnUnknownError()
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMsgKey = responseHandler.parseErrorResponse(responseBody).key
                    Resource.error(
                        errorMsgKey, null
                    )
                } ?: returnUnknownError()
            }
        }
    }

    suspend fun sendCodeForSignup(authSendCodeRequest: AuthSendCodeRequest): Flow<Response<Void>> =
        flow {
            try {
                val result = authService.sendCodeForSignup(authSendCodeRequest)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun sendCodeForResetPassword(authSendCodeRequest: AuthSendCodeRequest): Flow<Response<Void>> =
        flow {
            try {
                val result = authService.sendCodeForResetPassword(authSendCodeRequest)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun verifyEmailForSignup(authVerifyEmailRequest: AuthVerifyEmailRequest): Flow<Response<Void>> =
        flow {
            try {
                val result = authService.verifyEmailForSignup(authVerifyEmailRequest)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun verifyEmailForResetPassword(authVerifyEmailRequest: AuthVerifyEmailRequest): Flow<Resource<AccessToken>> {
        return authTokenRemoteSource.verifyEmailForResetPasswordAuthToken(
            authVerifyEmailRequest
        ).map { response ->
            if (response.isSuccessful && response.code() == ResponseCode.SUCCESS.value) {
                response.body()?.let { accessTokenDto ->
                    accessTokenDto.let {
                        Resource.success(
                            accessTokenDtoMapper.mapToDomainModel(
                                accessTokenDto
                            )
                        )
                    }
                } ?: returnUnknownError()
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMsgKey = responseHandler.parseErrorResponse(responseBody).key
                    Resource.error(
                        errorMsgKey, null
                    )
                } ?: returnUnknownError()
            }
        }
    }

    suspend fun resetPassword(authResetPasswordRequest: AuthResetPasswordRequest): Flow<Response<Void>> =
        flow {
            try {
                val result =
                    authService.resetPassword(getAuthHeader(), authResetPasswordRequest)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun logout(): Flow<Response<Void>> =
        flow {
            try {
                val refreshToken = sessionManager.cachedToken.value!!.refreshToken!!
                val result =
                    authService.logout(getAuthHeader(), AuthRefreshRequest(refreshToken))
                CoroutineScope(Dispatchers.IO).launch {
                    userIdPrefsManager.clearUserId()
                    authTokenPrefsManager.clearAuthToken()
                    settingsPrefsManager.resetSettings()
                }
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun withdraw(): Flow<Response<Void>> =
        flow {
            try {
                val refreshToken = sessionManager.cachedToken.value!!.refreshToken!!
                val result =
                    authService.withdraw(getAuthHeader(), AuthRefreshRequest(refreshToken))
                CoroutineScope(Dispatchers.IO).launch {
                    userIdPrefsManager.clearUserId()
                    authTokenPrefsManager.clearAuthToken()
                    settingsPrefsManager.resetSettings()
                }
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    fun checkPreviousUser(): Flow<Resource<Pair<Int, AuthToken>>> {
        return userIdPrefsManager.preferencesFlow.combine(
            authTokenPrefsManager.preferencesFlow
        ) { userId, authToken ->
            Pair(userId, authToken)
        }.map { pair ->
            if (pair.first != -1 && !pair.second.accessToken.isNullOrEmpty() && !pair.second.refreshToken.isNullOrEmpty())
                Resource.success(pair)
            else
                Resource.error("Couldn't find previous user", null)
        }
    }

    private fun getAuthHeader(): String {
        val accessToken = sessionManager.cachedToken.value?.accessToken
        return "Bearer $accessToken"
    }

    private fun <T> returnUnknownError(): Resource<T> {
        return Resource.error(
            "Unknown error", null
        )
    }

}