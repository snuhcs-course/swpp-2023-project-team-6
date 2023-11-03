package com.example.speechbuddy.repository

import com.example.speechbuddy.data.remote.AuthTokenRemoteSource
import com.example.speechbuddy.data.remote.models.AuthTokenDto
import com.example.speechbuddy.data.remote.models.AuthTokenDtoMapper
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthResetPasswordRequest
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailAcceptRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailSendRequest
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.utils.Status
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {

    private lateinit var authRepository: AuthRepository

    @MockK
    private val authTokenRemoteSource = mockk<AuthTokenRemoteSource>()
    private val authTokenDtoMapper = AuthTokenDtoMapper()

    private val mockEmail = "test@example.com"
    private val mockPassword = "password123"
    private val mockNickname = "TestUser"
    private val mockCode = "123456"
    private val mockAccessToken = "testAccessToken"
    private val mockRefreshToken = "testRefreshToken"

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
        authRepository = AuthRepository(authTokenRemoteSource, authTokenDtoMapper)
    }

    @Test
    fun `should return SUCCESS Resource when signup request is valid`() {
        runBlocking {
            val authSignupRequest = AuthSignupRequest(
                email = mockEmail,
                password = mockPassword,
                nickname = mockNickname
            )
            // 성공 케이스의 Response<Void>를 정의
            val successResponse = Response.success<Void>(201, null)

            coEvery { authTokenRemoteSource.signupAuthToken(authSignupRequest) } returns flowOf(successResponse)

            val result = authRepository.signup(authSignupRequest)
            // 아래 resource는 Resource<Void> 타입
            result.collect { resource ->
                assert(resource.status == Status.SUCCESS)
                assert(resource.data == null)
                assert(resource.message == null)
            }
        }
    }

    @Test
    fun `should return ERROR Resource when signup request is invalid`() {
        runBlocking{
            val authSignupRequest = AuthSignupRequest(
                email = mockEmail,
                password = "invalid",
                nickname = mockNickname
            )
            // 에러 케이스의 Response<Void>를 정의
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { authTokenRemoteSource.signupAuthToken(authSignupRequest) } returns flowOf(errorResponse)

            val result = authRepository.signup(authSignupRequest)
            result.collect { resource ->
                assert(resource.status == Status.ERROR)
                assert(resource.data == null)
                assert(resource.message == "key of message")
            }
        }
    }

    @Test
    fun `should return SUCCESS Resource when login request is valid`(){
        runBlocking {
            val authLoginRequest = AuthLoginRequest(
                email = mockEmail,
                password = mockPassword
            )
            val authTokenDto = AuthTokenDto(
                accessToken = mockAccessToken,
                refreshToken = mockRefreshToken
            )
            val expectedAuthToken = AuthToken(
                accessToken = mockAccessToken,
                refreshToken = mockRefreshToken
            )
            // 성공 케이스의 Response<AuthTokenDto>를 정의
            val successResponse = Response.success<AuthTokenDto>(200, authTokenDto)

            coEvery { authTokenRemoteSource.loginAuthToken(authLoginRequest) } returns flowOf(successResponse)

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
        runBlocking{
            val authLoginRequest = AuthLoginRequest(
                email = mockEmail,
                password = "invalid"
            )
            val errorResponse = Response.error<AuthTokenDto>(400, mockErrorResponseBody)

            coEvery { authTokenRemoteSource.loginAuthToken(authLoginRequest) } returns flowOf(errorResponse)

            val result = authRepository.login(authLoginRequest)
            result.collect { resource ->
                assert(resource.status == Status.ERROR)
                assert(resource.data == null)
                assert(resource.message == "key of message")
            }
        }
    }

    @Test
    fun `should return SUCCESS Resource when verifySendSignup request is valid`() {
        runBlocking {
            val authVerifyEmailSendRequest = AuthVerifyEmailSendRequest(
                email = mockEmail
            )
            val successResponse = Response.success<Void>(200, null)

            coEvery { authTokenRemoteSource.verifySendSignupAuthToken(authVerifyEmailSendRequest) } returns flowOf(successResponse)

            val result = authRepository.verifySendSignup(authVerifyEmailSendRequest)
            // 아래 resource는 Resource<Void> 타입
            result.collect { resource ->
                assert(resource.status == Status.SUCCESS)
                assert(resource.data == null)
                assert(resource.message == null)
            }
        }
    }

    @Test
    fun `should return ERROR Resource when verifySendSignup request is invalid`() {
        runBlocking{
            val authVerifyEmailSendRequest = AuthVerifyEmailSendRequest(
                email = "invalid@example.com"
            )
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { authTokenRemoteSource.verifySendSignupAuthToken(authVerifyEmailSendRequest) } returns flowOf(errorResponse)

            val result = authRepository.verifySendSignup(authVerifyEmailSendRequest)
            result.collect { resource ->
                assert(resource.status == Status.ERROR)
                assert(resource.data == null)
                assert(resource.message == "key of message")
            }
        }
    }

    @Test
    fun `should return SUCCESS Resource when verifySendPW request is valid`() {
        runBlocking {
            val authVerifyEmailSendRequest = AuthVerifyEmailSendRequest(
                email = mockEmail
            )
            val successResponse = Response.success<Void>(200, null)

            coEvery { authTokenRemoteSource.verifySendPWAuthToken(authVerifyEmailSendRequest) } returns flowOf(successResponse)

            val result = authRepository.verifySendPW(authVerifyEmailSendRequest)
            // 아래 resource는 Resource<Void> 타입
            result.collect { resource ->
                assert(resource.status == Status.SUCCESS)
                assert(resource.data == null)
                assert(resource.message == null)
            }
        }
    }

    @Test
    fun `should return ERROR Resource when verifySendPW request is invalid`() {
        runBlocking{
            val authVerifyEmailSendRequest = AuthVerifyEmailSendRequest(
                email = "invalid@example.com"
            )
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { authTokenRemoteSource.verifySendPWAuthToken(authVerifyEmailSendRequest) } returns flowOf(errorResponse)

            val result = authRepository.verifySendPW(authVerifyEmailSendRequest)
            result.collect { resource ->
                assert(resource.status == Status.ERROR)
                assert(resource.data == null)
                assert(resource.message == "key of message")
            }
        }
    }

    @Test
    fun `should return SUCCESS Resource when verifyAcceptSignup request is valid`() {
        runBlocking {
            val authVerifyEmailAcceptRequest = AuthVerifyEmailAcceptRequest(
                email = mockEmail,
                code = mockCode
            )
            val successResponse = Response.success<Void>(200, null)

            coEvery { authTokenRemoteSource.verifyAcceptSignupAuthToken(authVerifyEmailAcceptRequest) } returns flowOf(successResponse)

            val result = authRepository.verifyAcceptSignup(authVerifyEmailAcceptRequest)
            // 아래 resource는 Resource<Void> 타입
            result.collect { resource ->
                assert(resource.status == Status.SUCCESS)
                assert(resource.data == null)
                assert(resource.message == null)
            }
        }
    }

    @Test
    fun `should return ERROR Resource when verifyAcceptSignup request is invalid`() {
        runBlocking{
            val authVerifyEmailAcceptRequest = AuthVerifyEmailAcceptRequest(
                email = mockEmail,
                code = "invalid"
            )
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { authTokenRemoteSource.verifyAcceptSignupAuthToken(authVerifyEmailAcceptRequest) } returns flowOf(errorResponse)

            val result = authRepository.verifyAcceptSignup(authVerifyEmailAcceptRequest)
            result.collect { resource ->
                assert(resource.status == Status.ERROR)
                assert(resource.data == null)
                assert(resource.message == "key of message")
            }
        }
    }

    @Test
    fun `should return SUCCESS Resource when verifyAcceptPW request is valid`(){
        runBlocking {
            val authVerifyEmailAcceptRequest = AuthVerifyEmailAcceptRequest(
                email = mockEmail,
                code = mockCode
            )
            val authTokenDto = AuthTokenDto(
                accessToken = mockAccessToken,
                refreshToken = null
            )
            val expectedAuthToken = AuthToken(
                accessToken = mockAccessToken,
                refreshToken = null
            )
            val successResponse = Response.success<AuthTokenDto>(200, authTokenDto)

            coEvery { authTokenRemoteSource.verifyAcceptPWAuthToken(authVerifyEmailAcceptRequest) } returns flowOf(successResponse)

            val result = authRepository.verifyAcceptPW(authVerifyEmailAcceptRequest)

            // 아래 resource는 Resource<AuthToken> 타입
            result.collect { resource ->
                assert(resource.status == Status.SUCCESS)
                assert(resource.data == expectedAuthToken)
                assert(resource.message == null)
            }
        }
    }

    @Test
    fun `should return ERROR Resource when verifyAcceptPW request is invalid`() {
        runBlocking{
            val authVerifyEmailAcceptRequest = AuthVerifyEmailAcceptRequest(
                email = mockEmail,
                code = "invalid"
            )
            val errorResponse = Response.error<AuthTokenDto>(400, mockErrorResponseBody)

            coEvery { authTokenRemoteSource.verifyAcceptPWAuthToken(authVerifyEmailAcceptRequest) } returns flowOf(errorResponse)

            val result = authRepository.verifyAcceptPW(authVerifyEmailAcceptRequest)
            result.collect { resource ->
                assert(resource.status == Status.ERROR)
                assert(resource.data == null)
                assert(resource.message == "key of message")
            }
        }
    }

    @Test
    fun `should return SUCCESS Resource when resetPassword request is valid`() {
        runBlocking {
            val authResetPasswordRequest = AuthResetPasswordRequest(
                password = "newPassword"
            )
            val successResponse = Response.success<Void>(200, null)

            coEvery { authTokenRemoteSource.resetPasswordAuthToken(authResetPasswordRequest) } returns flowOf(successResponse)

            val result = authRepository.resetPassword(authResetPasswordRequest)
            // 아래 resource는 Resource<Void> 타입
            result.collect { resource ->
                assert(resource.status == Status.SUCCESS)
                assert(resource.data == null)
                assert(resource.message == null)
            }
        }
    }

    @Test
    fun `should return ERROR Resource when resetPassword request is invalid`() {
        runBlocking{
            val authResetPasswordRequest = AuthResetPasswordRequest(
                password = "invalid"
            )
            val errorResponse = Response.error<Void>(400, mockErrorResponseBody)

            coEvery { authTokenRemoteSource.resetPasswordAuthToken(authResetPasswordRequest) } returns flowOf(errorResponse)

            val result = authRepository.resetPassword(authResetPasswordRequest)
            result.collect { resource ->
                assert(resource.status == Status.ERROR)
                assert(resource.data == null)
                assert(resource.message == "key of message")
            }
        }
    }

}