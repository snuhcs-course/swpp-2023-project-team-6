package com.example.speechbuddy.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speechbuddy.data.local.SettingsPrefsManager
import com.example.speechbuddy.ui.models.InitialPage
import com.example.speechbuddy.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsPrefManager: SettingsPrefsManager
) {

    private val _darkModeLiveData = MutableLiveData<Boolean?>()
    val darkModeLiveData: LiveData<Boolean?>
        get() = _darkModeLiveData

    suspend fun setDarkMode(value: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            if (_darkModeLiveData.value != value) {
                _darkModeLiveData.value = value
            }
        }

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