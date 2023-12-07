package com.example.speechbuddy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.speechbuddy.data.local.SettingsPrefsManager
import com.example.speechbuddy.domain.models.SettingsPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsPrefsManagerTest {

    private lateinit var context: Context
    private lateinit var settingsPrefsManager: SettingsPrefsManager

    private val testSettings = SettingsPreferences(
        autoBackup = false,
        darkMode = true,
        initialPage = false,
        lastBackupDate = "2023-01-01"
    )

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        settingsPrefsManager = SettingsPrefsManager(context)

        runBlocking {
            settingsPrefsManager.saveAutoBackup(testSettings.autoBackup)
            settingsPrefsManager.saveDarkMode(testSettings.darkMode)
            settingsPrefsManager.saveInitialPage(testSettings.initialPage)
            settingsPrefsManager.saveLastBackupDate(testSettings.lastBackupDate)
        }
    }

    @After
    fun teardown() {
        runBlocking {
            settingsPrefsManager.resetSettings()
        }
    }

    @Test
    fun should_save_and_retrieve_settings() {
        runBlocking {
            val observedSettings = settingsPrefsManager.settingsPreferencesFlow.first()
            assertEquals(testSettings, observedSettings)
        }
    }

    @Test
    fun should_save_auto_backup_setting() {
        runBlocking {
            val testAutoBackupValue = true
            settingsPrefsManager.saveAutoBackup(testAutoBackupValue)

            val observedSettings = settingsPrefsManager.settingsPreferencesFlow.first()

            val expectedSettings = SettingsPreferences(
                autoBackup = testAutoBackupValue,
                darkMode = true,
                initialPage = false,
                lastBackupDate = "2023-01-01"
            )
            assertEquals(expectedSettings, observedSettings)
        }
    }

    @Test
    fun should_save_dark_mode_setting() {
        runBlocking {
            val testDarkModeValue = false
            settingsPrefsManager.saveDarkMode(testDarkModeValue)

            val observedSettings = settingsPrefsManager.settingsPreferencesFlow.first()

            val expectedSettings = SettingsPreferences(
                autoBackup = false,
                darkMode = testDarkModeValue,
                initialPage = false,
                lastBackupDate = "2023-01-01"
            )
            assertEquals(expectedSettings, observedSettings)
        }
    }

    @Test
    fun should_save_initial_page_setting() {
        runBlocking {
            val testInitialPageValue = true
            settingsPrefsManager.saveInitialPage(testInitialPageValue)

            val observedSettings = settingsPrefsManager.settingsPreferencesFlow.first()

            val expectedSettings = SettingsPreferences(
                autoBackup = false,
                darkMode = true,
                initialPage = testInitialPageValue,
                lastBackupDate = "2023-01-01"
            )
            assertEquals(expectedSettings, observedSettings)
        }
    }

    @Test
    fun should_save_last_backup_date_setting() {
        runBlocking {
            val testLastBackupDateValue = "2023-02-15"
            settingsPrefsManager.saveLastBackupDate(testLastBackupDateValue)

            val observedSettings = settingsPrefsManager.settingsPreferencesFlow.first()

            val expectedSettings = SettingsPreferences(
                autoBackup = false,
                darkMode = true,
                initialPage = false,
                lastBackupDate = testLastBackupDateValue
            )
            assertEquals(expectedSettings, observedSettings)
        }
    }

    @Test
    fun should_reset_settings() {
        runBlocking {
            // Set some non-default values first
            settingsPrefsManager.saveAutoBackup(false)
            settingsPrefsManager.saveDarkMode(true)
            settingsPrefsManager.saveInitialPage(false)
            settingsPrefsManager.saveLastBackupDate("2023-01-01")

            settingsPrefsManager.resetSettings()

            val observedSettings = settingsPrefsManager.settingsPreferencesFlow.first()
            val defaultSettings = SettingsPreferences(
                autoBackup = true,
                darkMode = false,
                initialPage = true,
                lastBackupDate = ""
            )
            assertEquals(defaultSettings, observedSettings)
        }
    }
}
