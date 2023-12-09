package com.example.speechbuddy.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.utils.Constants.Companion.GUEST_ID
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SessionManagerTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var authTokenObserver: Observer<AuthToken?>
    private lateinit var userIdObserver: Observer<Int?>
    private lateinit var isLoginObserver: Observer<Boolean?>

    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)

        authTokenObserver = mockk(relaxed = true)
        userIdObserver = mockk(relaxed = true)
        isLoginObserver = mockk(relaxed = true)

        sessionManager = SessionManager()
        sessionManager.cachedToken.observeForever(authTokenObserver)
        sessionManager.userId.observeForever(userIdObserver)
        sessionManager.isLogin.observeForever(isLoginObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
        clearAllMocks()
    }

    @Test
    fun `setAuthToken should update cachedToken LiveData`() = testCoroutineDispatcher.runBlockingTest {
        val testAuthToken = AuthToken("testAccessToken", "testRefreshToken")

        sessionManager.setAuthToken(testAuthToken)

        verify { authTokenObserver.onChanged(testAuthToken) }
    }

    @Test
    fun `setUserId should update userId LiveData`() = testCoroutineDispatcher.runBlockingTest {
        val testUserId = 123

        sessionManager.setUserId(testUserId)

        verify { userIdObserver.onChanged(testUserId) }
    }

    @Test
    fun `deleteToken should set cachedToken and userId to null`() = testCoroutineDispatcher.runBlockingTest {
        sessionManager.deleteToken()

        verify { authTokenObserver.onChanged(null) }
        verify { userIdObserver.onChanged(null) }
    }

    @Test
    fun `enterGuestMode should set userId to GUEST_ID`() = testCoroutineDispatcher.runBlockingTest {
        sessionManager.enterGuestMode()

        verify { userIdObserver.onChanged(GUEST_ID) }
    }

    @Test
    fun `exitGuestMode should set userId to null`() = testCoroutineDispatcher.runBlockingTest {
        sessionManager.exitGuestMode()

        verify { userIdObserver.onChanged(null) }
    }

    @Test
    fun `setIsLogin should update isLogin LiveData`() = testCoroutineDispatcher.runBlockingTest {
        sessionManager.setIsLogin(true)

        verify { isLoginObserver.onChanged(true) }
    }

    @Test
    fun `isAuthorized should reflect correct authorization status`() =
        testCoroutineDispatcher.runBlockingTest {
            val testAuthToken = AuthToken("testAccessToken", "testRefreshToken")

            sessionManager.setUserId(456)
            sessionManager.setAuthToken(testAuthToken)

            verify { userIdObserver.onChanged(any()) }
            verify { authTokenObserver.onChanged(any()) }

            val isAuthorizedObserver = slot<Boolean>()
            every { isLoginObserver.onChanged(capture(isAuthorizedObserver)) } just Runs

            sessionManager.isAuthorized.observeForever(isLoginObserver)

            assertEquals(true, isAuthorizedObserver.captured)
        }
}
