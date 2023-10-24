package com.example.speechbuddy.repository

import com.example.speechbuddy.data.remote.AuthTokenRemoteSource
import com.example.speechbuddy.data.remote.models.AuthTokenDtoMapper
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailAcceptRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailSendRequest
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authTokenRemoteSource: AuthTokenRemoteSource,
    private val authTokenDtoMapper: AuthTokenDtoMapper
) {

    suspend fun signup(authSignupRequest: AuthSignupRequest): Flow<Resource<Void>> {
        return authTokenRemoteSource.signupAuthToken(authSignupRequest).map { response ->
            if (response.isSuccessful && response.code() == 201) {
                Resource.success(null)
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMessage =
                        JSONObject(responseBody.charStream().readText()).getString("error")
                    Resource.error(
                        errorMessage,
                        null
                    )
                } ?: Resource.error("Unknown Error", null)
            }
        }
    }

    suspend fun login(authLoginRequest: AuthLoginRequest): Flow<Resource<AuthToken>> {
        return authTokenRemoteSource.loginAuthToken(authLoginRequest)
            .map { response ->
                if (response.isSuccessful && response.code() == 200) {
                    response.body()?.let { authTokenDto ->
                        authTokenDto.let {
                            Resource.success(authTokenDtoMapper.mapToDomainModel(authTokenDto))
                        }
                    } ?: returnUnknownError()
                } else {
                    response.errorBody()?.let { responseBody ->
                        val errorMessage =
                            JSONObject(responseBody.charStream().readText()).getString("error")
                        Resource.error(
                            errorMessage,
                            null
                        )
                    } ?: returnUnknownError()
                }
            }
    }

    suspend fun verifySendSignup(authVerifyEmailSendRequest: AuthVerifyEmailSendRequest): Flow<Resource<Void>> {
        return authTokenRemoteSource.verifySendSignupAuthToken(authVerifyEmailSendRequest).map { response ->
            if (response.isSuccessful && response.code() == 200) {
                Resource.success(null)
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorJson = JSONObject(responseBody.charStream().readText())
                    val messageJson = errorJson.getJSONObject("error").getJSONObject("message")
                    val firstKeyOfMessage = messageJson.keys().next().toString()
                    Resource.error(
                        firstKeyOfMessage,
                        null
                    )
                } ?: Resource.error("Unknown Error", null)
            }
        }
    }

    suspend fun verifySendPW(authVerifyEmailSendRequest: AuthVerifyEmailSendRequest): Flow<Resource<Void>> {
        return authTokenRemoteSource.verifySendPWAuthToken(authVerifyEmailSendRequest).map { response ->
            if (response.isSuccessful && response.code() == 200) {
                Resource.success(null)
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorJson = JSONObject(responseBody.charStream().readText())
                    val messageJson = errorJson.getJSONObject("error").getJSONObject("message")
                    val firstKeyOfMessage = messageJson.keys().next().toString()
                    Resource.error(
                        firstKeyOfMessage,
                        null
                    )
                } ?: Resource.error("Unknown Error", null)
            }
        }
    }
    suspend fun verifyAcceptSignup(authVerifyEmailAcceptRequest: AuthVerifyEmailAcceptRequest): Flow<Resource<Void>> {
        return authTokenRemoteSource.verifyAcceptSignupAuthToken(authVerifyEmailAcceptRequest).map { response ->
            if (response.isSuccessful && response.code() == 200) {
                Resource.success(null)
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorJson = JSONObject(responseBody.charStream().readText())
                    val messageJson = errorJson.getJSONObject("error").getJSONObject("message")
                    val firstKeyOfMessage = messageJson.keys().next().toString()
                    Resource.error(
                        firstKeyOfMessage,
                        null
                    )
                } ?: Resource.error("Unknown Error", null)
            }
        }
    }

    suspend fun verifyAcceptPW(authVerifyEmailAcceptRequest: AuthVerifyEmailAcceptRequest): Flow<Resource<AuthToken>> {
        return authTokenRemoteSource.verifyAcceptPWAuthToken(authVerifyEmailAcceptRequest)
            .map { response ->
                if (response.isSuccessful && response.code() == 200) {
                    response.body()?.let { authTokenDto ->
                        authTokenDto.let {
                            Resource.success(authTokenDtoMapper.mapToDomainModel(authTokenDto))
                        }
                    } ?: returnUnknownError()
                } else {
                    response.errorBody()?.let { responseBody ->
                        val errorJson = JSONObject(responseBody.charStream().readText())
                        val messageJson = errorJson.getJSONObject("error").getJSONObject("message")
                        val firstKeyOfMessage = messageJson.keys().next().toString()
                        Resource.error(
                            firstKeyOfMessage,
                            null
                        )
                    } ?: returnUnknownError()
                }
            }

    }

    private fun returnUnknownError(): Resource<AuthToken> {
        return Resource.error(
            "Unknown error",
            null
        )
    }

}