package com.example.speechbuddy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.speechbuddy.data.local.AuthTokenPrefsManager
import com.example.speechbuddy.domain.models.AuthToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthTokenPrefsManagerTest {

    private lateinit var context: Context
    private lateinit var authTokenPrefsManager: AuthTokenPrefsManager

    private val testAuthToken = AuthToken("testAccessToken", "testRefreshToken")

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        authTokenPrefsManager = AuthTokenPrefsManager(context)
    }

    @After
    fun teardown() {
        runBlocking {
            authTokenPrefsManager.clearAuthToken()
        }
    }

    @Test
    fun should_save_and_retrieve_auth_token() {
        runBlocking {
            authTokenPrefsManager.saveAuthToken(testAuthToken)
            val observedAuthToken = authTokenPrefsManager.preferencesFlow.first()
            assertEquals(testAuthToken, observedAuthToken)
        }
    }

    @Test
    fun should_clear_auth_token() {
        runBlocking {
            authTokenPrefsManager.clearAuthToken()

            val observedAuthToken = authTokenPrefsManager.preferencesFlow.first()
            val expectedAuthToken = AuthToken("", "")
            assertEquals(expectedAuthToken, observedAuthToken)
        }
    }

    @Test
    fun should_reset_auth_token_on_clear() {
        runBlocking {
            val newAuthToken = AuthToken("newAccessToken", "newRefreshToken")
            authTokenPrefsManager.saveAuthToken(newAuthToken)

            authTokenPrefsManager.clearAuthToken()

            val observedAuthToken = authTokenPrefsManager.preferencesFlow.first()
            val expectedAuthToken = AuthToken("", "")
            assertEquals(expectedAuthToken, observedAuthToken)
        }
    }
}
