package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.SettingsPrefsManager
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.ui.models.InitialPage
import com.example.speechbuddy.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsPrefManager: SettingsPrefsManager
) {
    suspend fun setDarkMode(value: Boolean) {
        settingsPrefManager.saveDarkMode(value)
    }

    suspend fun setInitialPage(page: InitialPage) {
        if (page == InitialPage.SYMBOL_SELECTION) {
            settingsPrefManager.saveInitialPage(true)
        } else {
            settingsPrefManager.saveInitialPage(false)
        }
    }

    suspend fun setAutoBackup(value: Boolean) {
        settingsPrefManager.saveAutoBackup(value)
    }

    fun getDarkMode(): Flow<Resource<Boolean>> {
        return settingsPrefManager.settingsPreferencesFlow.map { settingsPreferences ->
            Resource.success(settingsPreferences.darkMode)
        }
    }

    fun getInitialPage(): Flow<Resource<Boolean>> {
        return settingsPrefManager.settingsPreferencesFlow.map { settingsPreferences ->
            Resource.success(settingsPreferences.initialPage)
        }
    }

    fun getAutoBackup(): Flow<Resource<Boolean>> {
        return settingsPrefManager.settingsPreferencesFlow.map { settingsPreferences ->
            Resource.success(settingsPreferences.autoBackup)
        }
    }

}