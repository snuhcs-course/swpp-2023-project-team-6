package com.example.speechbuddy.repository

import com.example.speechbuddy.data.remote.AuthTokenRemoteSource
import com.example.speechbuddy.data.remote.models.AuthTokenDtoMapper
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailSendRequest
import com.example.speechbuddy.utils.Status
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {

    private lateinit var authRepository: AuthRepository
    private val authTokenRemoteSource = mockk<AuthTokenRemoteSource>()
    private val authTokenDtoMapper = AuthTokenDtoMapper()

    @Before
    fun setup() {
        authRepository = AuthRepository(authTokenRemoteSource, authTokenDtoMapper)
    }

    @Test
    fun testSignupSuccess() {
        runBlocking {
            val authSignupRequest = AuthSignupRequest(
                email = "testemail@google.com",
                nickname = "testnickname",
                password = "testpassword"
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

//    @Test
//    fun testLoginSuccess(){}

    @Test
    fun testVerifySendSignupSuccess() {
        runBlocking {
            val authVerifyEmailSendRequest = AuthVerifyEmailSendRequest(
                email = "testemail@google.com"
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
}

