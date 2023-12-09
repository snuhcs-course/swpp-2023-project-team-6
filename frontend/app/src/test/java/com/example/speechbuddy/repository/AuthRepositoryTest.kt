package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.AuthTokenPrefsManager
import com.example.speechbuddy.data.local.SettingsPrefsManager
import com.example.speechbuddy.data.local.UserIdPrefsManager
import com.example.speechbuddy.data.remote.AuthTokenRemoteSource
import com.example.speechbuddy.data.remote.models.AccessTokenDto
import com.example.speechbuddy.data.remote.models.AccessTokenDtoMapper
import com.example.speechbuddy.data.remote.models.AuthTokenDto
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
import com.example.speechbuddy.utils.ResponseHandler
import com.example.speechbuddy.utils.Status
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
/*
class AuthRepositoryTest {

    private lateinit var authRepository: AuthRepository

    @MockK
    private val authService = mockk<AuthService>()
    private val userIdPrefsManager = mockk<UserIdPrefsManager>()
    private val authTokenPrefsManager = mockk<AuthTokenPrefsManager>()
    private val settingsPrefsManager = mockk<SettingsPrefsManager>()
    private val authTokenRemoteSource = mockk<AuthTokenRemoteSource>()
    private val authTokenDtoMapper = AuthTokenDtoMapper()
    private val accessTokenDtoMapper = AccessTokenDtoMapper()
    private val responseHandler = ResponseHandler()
    private val sessionManager = mockk<SessionManager>()

    private val mockEmail = "test@example.com"
    private val mockPassword = "password123"
    private val mockNickname = "TestUser"
    private val mockCode = "123456"
    private val mockAccessToken = "testAccessToken"
    private val mockRefreshToken = "testRefreshToken"
    private val mockAuthHeader = "Bearer $mockAccessToken"

    private val mockErrorJson = """
    {
        "error": {
            "code": 000,
            "message": {
                "key of message": [
                    "error description"
                ]
            }
        }
    }
    """
    private val mockErrorResponseBody =
        mockErrorJson.toResponseBody("application/json".toMediaType())

    @Before
    fun setup() {
        authRepository = AuthRepository(
            authService,
            userIdPrefsManager,
            authTokenPrefsManager,
            authTokenRemoteSource,
            authTokenDtoMapper,
            accessTokenDtoMapper,
            responseHandler,
            sessionManager
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should return successful response when signup request is valid`() {
        runBlocking {
            val authSignupRequest = AuthSignupRequest(
                email = mockEmail,
                password = mockPassword,
                nickname = mockNickname
            )
            // 성공 케이스의 Response<Void>를 정의
            val successResponse = Response.success<Void>(201, null)

            coEvery { authService.signup(authSignupRequest) } returns successResponse

            val result = authRepository.signup(authSignupRequest)
            // 아래 resource는 Response<Void> 타입
            result.collect { resource ->
                assert(resource.isSuccessful)
                assert(resource.code() == 201)
                assert(resource.body() == null)
            }
        }
    }

    @Test
    fun `should return error response when signup request is invalid`() {
        runBlocking {
            val authSignupRequest = AuthSignupRequest(
                email = mockEmail,
                password = "invalid",
                nickname = mockNickname
            )
            // 에러 케이스의 Response<Void>를 정의
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { authService.signup(authSignupRequest) } returns errorResponse

            val result = authRepository.signup(authSignupRequest)
            result.collect { resource ->
                assert(!resource.isSuccessful)
                assert(resource.code() == 400)
            }
        }
    }

    @Test
    fun `should return SUCCESS Resource when login request is valid`() {
        runBlocking {
            val authLoginRequest = AuthLoginRequest(
                email = mockEmail,
                password = mockPassword
            )
            val authTokenDto = AuthTokenDto(
                accessToken = mockAccessToken,
                refreshToken = mockRefreshToken
            )
            val authToken = AuthToken(
                accessToken = mockAccessToken,
                refreshToken = mockRefreshToken
            )
            val expectedAuthToken = AuthToken(
                accessToken = mockAccessToken,
                refreshToken = mockRefreshToken
            )
            // 성공 케이스의 Response<AuthTokenDto>를 정의
            val successResponse = Response.success<AuthTokenDto>(200, authTokenDto)

            coEvery { authTokenRemoteSource.loginAuthToken(authLoginRequest) } returns flowOf(
                successResponse
            )

            coEvery { authTokenPrefsManager.saveAuthToken(authToken) } returns Unit
            coEvery { sessionManager.setIsLogin(true) } returns Unit

            val result = authRepository.login(authLoginRequest)

            // 아래 resource는 Resource<AuthToken> 타입
            result.collect { resource ->
                assert(resource.status == Status.SUCCESS)
                assert(resource.data == expectedAuthToken)
                assert(resource.message == null)
            }
        }
    }

    @Test
    fun `should return ERROR Resource when login request is invalid`() {
        runBlocking {
            val authLoginRequest = AuthLoginRequest(
                email = mockEmail,
                password = "invalid"
            )
            val errorResponse = Response.error<AuthTokenDto>(400, mockErrorResponseBody)

            coEvery { authTokenRemoteSource.loginAuthToken(authLoginRequest) } returns flowOf(
                errorResponse
            )

            val result = authRepository.login(authLoginRequest)
            result.collect { resource ->
                assert(resource.status == Status.ERROR)
                assert(resource.data == null)
                assert(resource.message == "key of message")
            }
        }
    }

    @Test
    fun `should return successful response when sendCodeForSignup request is valid`() {
        runBlocking {
            val authSendCodeRequest = AuthSendCodeRequest(
                email = mockEmail
            )
            val successResponse = Response.success<Void>(200, null)

            coEvery { authService.sendCodeForSignup(authSendCodeRequest) } returns successResponse

            val result = authRepository.sendCodeForSignup(authSendCodeRequest)
            // 아래 resource는 Response<Void> 타입
            result.collect { resource ->
                assert(resource.isSuccessful)
                assert(resource.code() == 200)
                assert(resource.body() == null)
            }
        }
    }

    @Test
    fun `should return error response when sendCodeForSignup request is invalid`() {
        runBlocking {
            val authSendCodeRequest = AuthSendCodeRequest(
                email = "invalid@example.com"
            )
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { authService.sendCodeForSignup(authSendCodeRequest) } returns errorResponse

            val result = authRepository.sendCodeForSignup(authSendCodeRequest)
            result.collect { resource ->
                assert(!resource.isSuccessful)
                assert(resource.code() == 400)
            }
        }
    }

    @Test
    fun `should return successful response when sendCodeForResetPassword request is valid`() {
        runBlocking {
            val authSendCodeRequest = AuthSendCodeRequest(
                email = mockEmail
            )
            val successResponse = Response.success<Void>(200, null)

            coEvery { authService.sendCodeForResetPassword(authSendCodeRequest) } returns successResponse

            val result = authRepository.sendCodeForResetPassword(authSendCodeRequest)
            // 아래 resource는 Response<Void> 타입
            result.collect { resource ->
                assert(resource.isSuccessful)
                assert(resource.code() == 200)
                assert(resource.body() == null)
            }
        }
    }

    @Test
    fun `should return error response when sendCodeForResetPassword request is invalid`() {
        runBlocking {
            val authSendCodeRequest = AuthSendCodeRequest(
                email = "invalid@example.com"
            )
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { authService.sendCodeForResetPassword(authSendCodeRequest) } returns errorResponse

            val result = authRepository.sendCodeForResetPassword(authSendCodeRequest)
            result.collect { resource ->
                assert(!resource.isSuccessful)
                assert(resource.code() == 400)
            }
        }
    }

    @Test
    fun `should return successful response when verifyEmailForSignup request is valid`() {
        runBlocking {
            val authVerifyEmailRequest = AuthVerifyEmailRequest(
                email = mockEmail,
                code = mockCode
            )
            val successResponse = Response.success<Void>(200, null)

            coEvery { authService.verifyEmailForSignup(authVerifyEmailRequest) } returns successResponse

            val result = authRepository.verifyEmailForSignup(authVerifyEmailRequest)
            // 아래 resource는 Response<Void> 타입
            result.collect { resource ->
                assert(resource.isSuccessful)
                assert(resource.code() == 200)
                assert(resource.body() == null)
            }
        }
    }

    @Test
    fun `should return error response when verifyEmailForSignup request is invalid`() {
        runBlocking {
            val authVerifyEmailRequest = AuthVerifyEmailRequest(
                email = mockEmail,
                code = "invalid"
            )
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { authService.verifyEmailForSignup(authVerifyEmailRequest) } returns errorResponse

            val result = authRepository.verifyEmailForSignup(authVerifyEmailRequest)
            result.collect { resource ->
                assert(!resource.isSuccessful)
                assert(resource.code() == 400)
            }
        }
    }

    @Test
    fun `should return SUCCESS Resource when verifyEmailForResetPassword request is valid`() {
        runBlocking {
            val authVerifyEmailRequest = AuthVerifyEmailRequest(
                email = mockEmail,
                code = mockCode
            )
            val accessTokenDto = AccessTokenDto(
                mockAccessToken
            )
            val successResponse = Response.success<AccessTokenDto>(200, accessTokenDto)

            coEvery {
                authTokenRemoteSource.verifyEmailForResetPasswordAuthToken(
                    authVerifyEmailRequest
                )
            } returns flowOf(successResponse)

            val result = authRepository.verifyEmailForResetPassword(authVerifyEmailRequest)

            // 아래 resource는 Resource<AuthToken> 타입
            result.collect { resource ->
                assert(resource.status == Status.SUCCESS)
                assert(resource.data == AccessToken(mockAccessToken))
                assert(resource.message == null)
            }
        }
    }

    @Test
    fun `should return ERROR Resource when verifyEmailForResetPassword request is invalid`() {
        runBlocking {
            val authVerifyEmailRequest = AuthVerifyEmailRequest(
                email = mockEmail,
                code = "invalid"
            )
            val errorResponse = Response.error<AccessTokenDto>(400, mockErrorResponseBody)

            coEvery {
                authTokenRemoteSource.verifyEmailForResetPasswordAuthToken(
                    authVerifyEmailRequest
                )
            } returns flowOf(errorResponse)

            val result = authRepository.verifyEmailForResetPassword(authVerifyEmailRequest)
            result.collect { resource ->
                assert(resource.status == Status.ERROR)
                assert(resource.data == null)
                assert(resource.message == "key of message")
            }
        }
    }

    @Test
    fun `should return successful response when resetPassword request is valid`() {
        runBlocking {
            val authResetPasswordRequest = AuthResetPasswordRequest(
                password = "newPassword"
            )
            val successResponse = Response.success<Void>(200, null)

            coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken

            coEvery { authService.resetPassword(mockAuthHeader, authResetPasswordRequest) } returns successResponse

            val result = authRepository.resetPassword(authResetPasswordRequest)
            // 아래 resource는 Response<Void> 타입
            result.collect { resource ->
                assert(resource.isSuccessful)
                assert(resource.code() == 200)
                assert(resource.body() == null)
            }
        }
    }

    @Test
    fun `should return error response when resetPassword request is invalid`() {
        runBlocking {
            val authResetPasswordRequest = AuthResetPasswordRequest(
                password = "invalid"
            )
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken

            coEvery { authService.resetPassword(mockAuthHeader, authResetPasswordRequest) } returns errorResponse

            val result = authRepository.resetPassword(authResetPasswordRequest)
            result.collect { resource ->
                assert(!resource.isSuccessful)
                assert(resource.code() == 400)
            }
        }
    }

    private fun mockEmptyAuthToken(): Flow<AuthToken> {
        return flow {
            emit(AuthToken("", ""))
        }
    }
    @Test
    fun `should return successful response when logout request is valid`() {
        runBlocking {
            val authRefreshRequest = AuthRefreshRequest(
                refresh = mockRefreshToken
            )
            val successResponse = Response.success<Void>(200, null)

            coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
            coEvery { sessionManager.cachedToken.value?.refreshToken } returns mockRefreshToken
            coEvery { authService.logout(mockAuthHeader, authRefreshRequest) } returns successResponse
            coEvery { authTokenPrefsManager.preferencesFlow } returns mockEmptyAuthToken()
            coEvery { userIdPrefsManager.clearUserId() } returns Unit
            coEvery { settingsPrefsManager.resetSettings() } returns Unit

            val result = authRepository.logout()
            result.collect { resource ->
                assert(resource.isSuccessful)
                assert(resource.code() == 200)
                assert(resource.body() == null)
            }

            val authTokenFlow = authTokenPrefsManager.preferencesFlow
            authTokenFlow.collect { authToken ->
                authToken.accessToken?.let { assert(it.isEmpty()) }
                authToken.refreshToken?.let { assert(it.isEmpty()) }
            }
        }
    }

    @Test
    fun `should return error response when logout request is invalid`() {
        runBlocking {
            val authRefreshRequest = AuthRefreshRequest(
                refresh = ""
            )
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
            coEvery { sessionManager.cachedToken.value?.refreshToken } returns ""
            coEvery { authService.logout(mockAuthHeader, authRefreshRequest) } returns errorResponse

            val result = authRepository.logout()
            result.collect { resource ->
                assert(!resource.isSuccessful)
                assert(resource.code() == 400)
            }
        }
    }

    @Test
    fun `should return successful response when withdraw request is valid`() {
        runBlocking {
            val authRefreshRequest = AuthRefreshRequest(
                refresh = mockRefreshToken
            )
            val successResponse = Response.success<Void>(200, null)

            coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
            coEvery { sessionManager.cachedToken.value?.refreshToken } returns mockRefreshToken
            coEvery { authService.withdraw(mockAuthHeader, authRefreshRequest) } returns successResponse
            coEvery { authTokenPrefsManager.preferencesFlow } returns mockEmptyAuthToken()
            coEvery { userIdPrefsManager.clearUserId() } returns Unit
            coEvery { settingsPrefsManager.resetSettings() } returns Unit

            val result = authRepository.withdraw()
            result.collect { resource ->
                assert(resource.isSuccessful)
                assert(resource.code() == 200)
                assert(resource.body() == null)
            }

            val authTokenFlow = authTokenPrefsManager.preferencesFlow
            authTokenFlow.collect { authToken ->
                authToken.accessToken?.let { assert(it.isEmpty()) }
                authToken.refreshToken?.let { assert(it.isEmpty()) }
            }
        }
    }

    @Test
    fun `should return error response when withdraw request is invalid`() {
        runBlocking {
            val authRefreshRequest = AuthRefreshRequest(
                refresh = ""
            )
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { sessionManager.cachedToken.value?.accessToken } returns mockAccessToken
            coEvery { sessionManager.cachedToken.value?.refreshToken } returns ""
            coEvery { authService.withdraw(mockAuthHeader, authRefreshRequest) } returns errorResponse

            val result = authRepository.withdraw()
            result.collect { resource ->
                assert(!resource.isSuccessful)
                assert(resource.code() == 400)
            }
        }
    }


}

 */

