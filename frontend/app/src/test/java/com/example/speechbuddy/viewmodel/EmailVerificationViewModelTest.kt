package com.example.speechbuddy.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.speechbuddy.R
import com.example.speechbuddy.data.remote.requests.AuthSendCodeRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailRequest
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.AccessToken
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.ui.models.EmailVerificationErrorType
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.ResponseHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class EmailVerificationViewModelTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val responseHandler: ResponseHandler = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private lateinit var viewModel: EmailVerificationViewModel

    private val validEmail = "test@test.com"
    private val invalidEmail = "test"
    private val emptyEmail = ""
    private val wrongEmail = "wrongemail@test.com"
    private val signup = "signup"
    private val resetPassword = "reset_password"
    private val sourceList = arrayListOf(signup, resetPassword)

    private val validCode = "123456" //length == 6
    private val wrongCode = "123450" //length == 6, but assumed as wrong code
    private val inValidCode = "1" //length != 6
    private val emptyCode = ""

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = EmailVerificationViewModel(
            authRepository, responseHandler, sessionManager
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `should return no error when email is empty before send button click`() {
        viewModel.setEmail(emptyEmail)

        assertEquals(emptyEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
    }

    @Test
    fun `should return no error when email is valid before send button click`() {
        viewModel.setEmail(validEmail)

        assertEquals(validEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
    }

    @Test
    fun `should return no error when email is invalid before send button click`() {
        viewModel.setEmail(invalidEmail)

        assertEquals(invalidEmail, viewModel.emailInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidEmail, false)
    }

    @Test
    fun `should change error state when empty email is changed to valid email`() {
        sourceList.forEach {
            viewModel.setSource(it)
            viewModel.setEmail(emptyEmail)

            viewModel.sendCode()

            assertEquals(emptyEmail, viewModel.emailInput)
            assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.EMAIL)
            assertEquals(viewModel.uiState.value.error?.messageId, R.string.no_email)
            assertEquals(viewModel.uiState.value.isValidEmail, false)

            viewModel.setEmail(validEmail)

            assertEquals(validEmail, viewModel.emailInput)
            assertEquals(viewModel.uiState.value.error?.type, null)
            assertEquals(viewModel.uiState.value.isValidEmail, true)
        }
    }

    @Test
    fun `should remain same when empty email is changed to invalid email`() {
        sourceList.forEach {
            viewModel.setSource(it)
            viewModel.setEmail(emptyEmail)

            viewModel.sendCode()

            assertEquals(emptyEmail, viewModel.emailInput)
            assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.EMAIL)
            assertEquals(viewModel.uiState.value.error?.messageId, R.string.no_email)
            assertEquals(viewModel.uiState.value.isValidEmail, false)

            viewModel.setEmail(invalidEmail)

            assertEquals(invalidEmail, viewModel.emailInput)
            assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.EMAIL)
            assertEquals(viewModel.uiState.value.error?.messageId, R.string.no_email)
            assertEquals(viewModel.uiState.value.isValidEmail, false)
        }
    }

    @Test
    fun `should remain same when invalid email is changed to empty email`() {
        sourceList.forEach {
            viewModel.setSource(it)
            viewModel.setEmail(invalidEmail)

            viewModel.sendCode()

            assertEquals(invalidEmail, viewModel.emailInput)
            assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.EMAIL)
            assertEquals(viewModel.uiState.value.error?.messageId, R.string.wrong_email)
            assertEquals(viewModel.uiState.value.isValidEmail, false)

            viewModel.setEmail(emptyEmail)

            assertEquals(emptyEmail, viewModel.emailInput)
            assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.EMAIL)
            assertEquals(viewModel.uiState.value.error?.messageId, R.string.wrong_email)
            assertEquals(viewModel.uiState.value.isValidEmail, false)
        }
    }

    @Test
    fun `should change error state when invalid email is changed to valid email`() {
        sourceList.forEach {
            viewModel.setSource(it)
            viewModel.setEmail(invalidEmail)

            viewModel.sendCode()

            assertEquals(invalidEmail, viewModel.emailInput)
            assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.EMAIL)
            assertEquals(viewModel.uiState.value.error?.messageId, R.string.wrong_email)
            assertEquals(viewModel.uiState.value.isValidEmail, false)

            viewModel.setEmail(validEmail)

            assertEquals(validEmail, viewModel.emailInput)
            assertEquals(viewModel.uiState.value.error?.type, null)
            assertEquals(viewModel.uiState.value.isValidEmail, true)
        }
    }

    @Test
    fun `should return no error when code is empty before verification`() {
        viewModel.setCode(emptyCode)

        assertEquals(emptyCode, viewModel.codeInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidCode, false)
    }

    @Test
    fun `should return no error when code is invalid before verification`() {
        viewModel.setCode(inValidCode)

        assertEquals(inValidCode, viewModel.codeInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidCode, false)
    }

    @Test
    fun `should return no error when code is valid before verification`() {
        viewModel.setCode(validCode)

        assertEquals(validCode, viewModel.codeInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidCode, false)
    }

    @Test
    fun `should change error state when invalid code is changed to valid code`() {
        val navigateCallback: (String) -> Unit = mockk(relaxed = true)

        viewModel.setCode(inValidCode)
        viewModel.verifyEmail(navigateCallback)

        assertEquals(inValidCode, viewModel.codeInput)
        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.CODE)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.wrong_code)
        assertEquals(viewModel.uiState.value.isValidCode, false)

        viewModel.setCode(validCode)

        assertEquals(validCode, viewModel.codeInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidCode, true)
    }

    @Test
    fun `should change error state when empty code is changed to valid code`() {
        val navigateCallback: (String) -> Unit = mockk(relaxed = true)

        viewModel.setCode(emptyCode)
        viewModel.verifyEmail(navigateCallback)

        assertEquals(emptyCode, viewModel.codeInput)
        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.CODE)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.no_code)
        assertEquals(viewModel.uiState.value.isValidCode, false)

        viewModel.setCode(validCode)

        assertEquals(validCode, viewModel.codeInput)
        assertEquals(viewModel.uiState.value.error?.type, null)
        assertEquals(viewModel.uiState.value.isValidCode, true)
    }

    @Test
    fun `should success code send when called with valid email`() {
        val validSendCodeRequest = AuthSendCodeRequest(validEmail)
        val successResponse: Response<Void> = Response.success(null)

        coEvery { authRepository.sendCodeForSignup(validSendCodeRequest) } returns flowOf(
            successResponse
        )
        coEvery { authRepository.sendCodeForResetPassword(validSendCodeRequest) } returns flowOf(
            successResponse
        )

        viewModel.setSource("signup")
        viewModel.setEmail(validEmail)
        viewModel.sendCode()
        coVerify { authRepository.sendCodeForSignup(validSendCodeRequest) }
        assertEquals(viewModel.uiState.value.error, null)
        assertEquals(viewModel.uiState.value.isCodeSuccessfullySent, true)

        viewModel.setSource("reset_password")
        viewModel.setEmail(validEmail)
        viewModel.sendCode()
        coVerify { authRepository.sendCodeForResetPassword(validSendCodeRequest) }
        assertEquals(viewModel.uiState.value.error, null)
        assertEquals(viewModel.uiState.value.isCodeSuccessfullySent, true)
    }

    @Test
    fun `should success verification when called with valid code`() {
        val navigateCallback: (String) -> Unit = mockk(relaxed = true)
        val successResponse: Response<Void> = Response.success(null)
        val accessToken = AccessToken("access")

        val validEmailVerificationRequest = AuthVerifyEmailRequest(
            validEmail, validCode
        )
        coEvery { authRepository.verifyEmailForSignup(validEmailVerificationRequest) } returns flowOf(
            successResponse
        )
        coEvery { authRepository.verifyEmailForResetPassword(validEmailVerificationRequest) } returns flowOf(
            Resource.success(accessToken)
        )

        viewModel.setSource("signup")
        viewModel.setEmail(validEmail)
        viewModel.setCode(validCode)

        viewModel.verifyEmail(navigateCallback)

        coVerify { authRepository.verifyEmailForSignup(validEmailVerificationRequest) }

        coVerify { navigateCallback("signup/$validEmail") }

        viewModel.setSource("reset_password")
        viewModel.setEmail(validEmail)
        viewModel.setCode(validCode)

        viewModel.verifyEmail(navigateCallback)

        coVerify { authRepository.verifyEmailForResetPassword(validEmailVerificationRequest) }

        coVerify { navigateCallback("reset_password") }
    }

    @Test
    fun `should fail code send when source is invalid`() {
        viewModel.setSource("invalid source")
        viewModel.setEmail(validEmail)
        viewModel.sendCode()

        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.UNKNOWN)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.unknown_error)
        assertEquals(viewModel.uiState.value.isCodeSuccessfullySent, false)
    }

    @Test
    fun `should fail code send when bad request occurs`() {
        val sendCodeRequest = AuthSendCodeRequest(wrongEmail)
        val errorResponseBody = "Error message or JSON here".toResponseBody(null)
        val errorResponse: Response<Void> = Response.error(400, errorResponseBody)

        coEvery { authRepository.sendCodeForSignup(sendCodeRequest) } returns flowOf(
            errorResponse
        )
        coEvery { authRepository.sendCodeForResetPassword(sendCodeRequest) } returns flowOf(
            errorResponse
        )
        coEvery { responseHandler.parseErrorResponse(any()).key } returns "email"
        viewModel.setSource("signup")
        viewModel.setEmail(wrongEmail)
        viewModel.sendCode()

        coVerify { authRepository.sendCodeForSignup(sendCodeRequest) }

        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.EMAIL)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.wrong_email)
        assertEquals(viewModel.uiState.value.isCodeSuccessfullySent, false)

        viewModel.setSource("reset_password")
        viewModel.setEmail(wrongEmail)
        viewModel.sendCode()

        coVerify { authRepository.sendCodeForResetPassword(sendCodeRequest) }

        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.EMAIL)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.wrong_email)
        assertEquals(viewModel.uiState.value.isCodeSuccessfullySent, false)
    }

    @Test
    fun `should fail code send when internet connection fails`() {
        val validSendCodeRequest = AuthSendCodeRequest(validEmail)
        val errorResponseBody = "Error message or JSON here".toResponseBody(null)
        val errorResponse: Response<Void> = Response.error(600, errorResponseBody)

        coEvery { authRepository.sendCodeForSignup(validSendCodeRequest) } returns flowOf(
            errorResponse
        )
        coEvery { authRepository.sendCodeForResetPassword(validSendCodeRequest) } returns flowOf(
            errorResponse
        )
        viewModel.setSource("signup")
        viewModel.setEmail(validEmail)
        viewModel.sendCode()

        coVerify { authRepository.sendCodeForSignup(validSendCodeRequest) }

        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.CONNECTION)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.connection_error)
        assertEquals(viewModel.uiState.value.isCodeSuccessfullySent, false)

        viewModel.setSource("reset_password")
        viewModel.setEmail(validEmail)
        viewModel.sendCode()

        coVerify { authRepository.sendCodeForResetPassword(validSendCodeRequest) }

        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.CONNECTION)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.connection_error)
        assertEquals(viewModel.uiState.value.isCodeSuccessfullySent, false)
    }

    @Test
    fun `should fail code send when unknown error occurs`() {
        val validSendCodeRequest = AuthSendCodeRequest(validEmail)
        val errorResponseBody = "Error message or JSON here".toResponseBody(null)
        val errorResponse: Response<Void> = Response.error(404, errorResponseBody)

        coEvery { authRepository.sendCodeForSignup(validSendCodeRequest) } returns flowOf(
            errorResponse
        )
        coEvery { authRepository.sendCodeForResetPassword(validSendCodeRequest) } returns flowOf(
            errorResponse
        )
        viewModel.setSource("signup")
        viewModel.setEmail(validEmail)
        viewModel.sendCode()

        coVerify { authRepository.sendCodeForSignup(validSendCodeRequest) }

        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.UNKNOWN)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.unknown_error)
        assertEquals(viewModel.uiState.value.isCodeSuccessfullySent, false)

        viewModel.setSource("reset_password")
        viewModel.setEmail(validEmail)
        viewModel.sendCode()

        coVerify { authRepository.sendCodeForResetPassword(validSendCodeRequest) }

        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.UNKNOWN)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.unknown_error)
        assertEquals(viewModel.uiState.value.isCodeSuccessfullySent, false)
    }

    @Test
    fun `should fail verification when source is invalid`() {
        val navigateCallback: (String) -> Unit = mockk(relaxed = true)

        viewModel.setSource("invalid source")
        viewModel.setEmail(validEmail)
        viewModel.setCode(validCode)

        viewModel.verifyEmail(navigateCallback)

        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.UNKNOWN)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.unknown_error)
        assertEquals(viewModel.uiState.value.isValidCode, false)
    }

    @Test
    fun `should fail verification when internet connection fails`() {
        val navigateCallback: (String) -> Unit = mockk(relaxed = true)
        val accessToken = AccessToken("access")

        val validEmailVerificationRequest = AuthVerifyEmailRequest(
            validEmail, validCode
        )
        val errorResponseBody = "Error message or JSON here".toResponseBody(null)
        val errorResponse: Response<Void> = Response.error(600, errorResponseBody)
        coEvery { authRepository.verifyEmailForSignup(validEmailVerificationRequest) } returns flowOf(
            errorResponse
        )
        coEvery { authRepository.verifyEmailForResetPassword(validEmailVerificationRequest) } returns flowOf(
            Resource.error("unknown", accessToken)
        )
        viewModel.setSource("signup")
        viewModel.setEmail(validEmail)
        viewModel.setCode(validCode)

        viewModel.verifyEmail(navigateCallback)

        coVerify { authRepository.verifyEmailForSignup(validEmailVerificationRequest) }

        assertEquals(viewModel.uiState.value.isValidCode, false)
        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.CONNECTION)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.connection_error)

        viewModel.setSource("reset_password")
        viewModel.setEmail(validEmail)
        viewModel.setCode(validCode)

        viewModel.verifyEmail(navigateCallback)

        coVerify { authRepository.verifyEmailForResetPassword(validEmailVerificationRequest) }

        assertEquals(viewModel.uiState.value.isValidCode, false)
        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.CONNECTION)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.connection_error)
    }

    @Test
    fun `should fail verification when code is wrong`() {
        val navigateCallback: (String) -> Unit = mockk(relaxed = true)
        val accessToken = AccessToken("access")

        val emailVerificationRequest = AuthVerifyEmailRequest(
            validEmail, wrongCode
        )
        val errorResponseBody = "Error message or JSON here".toResponseBody(null)
        val errorResponse: Response<Void> = Response.error(400, errorResponseBody)
        coEvery { authRepository.verifyEmailForSignup(emailVerificationRequest) } returns flowOf(
            errorResponse
        )
        coEvery { authRepository.verifyEmailForResetPassword(emailVerificationRequest) } returns flowOf(
            Resource.error("", accessToken)
        )
        viewModel.setSource("signup")
        viewModel.setEmail(validEmail)
        viewModel.setCode(wrongCode)

        viewModel.verifyEmail(navigateCallback)

        coVerify { authRepository.verifyEmailForSignup(emailVerificationRequest) }

        assertEquals(viewModel.uiState.value.isValidCode, false)
        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.CODE)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.wrong_code)

        viewModel.setSource("reset_password")
        viewModel.setEmail(validEmail)
        viewModel.setCode(wrongCode)

        viewModel.verifyEmail(navigateCallback)

        coVerify { authRepository.verifyEmailForResetPassword(emailVerificationRequest) }

        assertEquals(viewModel.uiState.value.isValidCode, false)
        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.CODE)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.wrong_code)
    }

    // Signup
    @Test
    fun `should fail verification when unknown error occurs`() {
        val navigateCallback: (String) -> Unit = mockk(relaxed = true)
        val validEmailVerificationRequest = AuthVerifyEmailRequest(
            validEmail, validCode
        )
        val errorResponseBody = "Error message or JSON here".toResponseBody(null)
        val errorResponse: Response<Void> = Response.error(404, errorResponseBody)
        coEvery { authRepository.verifyEmailForSignup(validEmailVerificationRequest) } returns flowOf(
            errorResponse
        )
        viewModel.setSource("signup")
        viewModel.setEmail(validEmail)
        viewModel.setCode(validCode)

        viewModel.verifyEmail(navigateCallback)

        coVerify { authRepository.verifyEmailForSignup(validEmailVerificationRequest) }

        assertEquals(viewModel.uiState.value.isValidCode, false)
        assertEquals(viewModel.uiState.value.error?.type, EmailVerificationErrorType.UNKNOWN)
        assertEquals(viewModel.uiState.value.error?.messageId, R.string.unknown_error)
    }
}