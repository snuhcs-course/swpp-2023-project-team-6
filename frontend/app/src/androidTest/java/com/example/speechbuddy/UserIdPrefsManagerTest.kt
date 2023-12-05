package com.example.speechbuddy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.speechbuddy.data.local.UserIdPrefsManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserIdPrefsManagerTest {

    private lateinit var context: Context
    private lateinit var userIdPrefsManager: UserIdPrefsManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        userIdPrefsManager = UserIdPrefsManager(context)
    }

    @After
    fun teardown() {
        runBlocking {
            userIdPrefsManager.clearUserId()
        }
    }

    @Test
    fun should_save_user_id() {
        runBlocking {
            userIdPrefsManager.saveUserId(USER_ID_VALUE)

            val observedUserId = userIdPrefsManager.preferencesFlow.first()
            assertEquals(USER_ID_VALUE, observedUserId)
        }
    }

    @Test
    fun should_clear_user_id() {
        runBlocking {
            userIdPrefsManager.saveUserId(USER_ID_VALUE)

            userIdPrefsManager.clearUserId()
            val observedUserId = userIdPrefsManager.preferencesFlow.first()
            assertEquals(null, observedUserId)
        }
    }

    companion object {
        const val USER_ID_VALUE = 123
    }
}
