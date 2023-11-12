package com.example.speechbuddy.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speechbuddy.data.local.SettingsPrefsManager.PreferencesKeys.AUTO_BACKUP
import com.example.speechbuddy.data.local.SettingsPrefsManager.PreferencesKeys.DARK_MODE
import com.example.speechbuddy.data.local.SettingsPrefsManager.PreferencesKeys.INITIAL_PAGE
import com.example.speechbuddy.domain.models.SettingsPreferences
import com.example.speechbuddy.utils.Constants
import com.example.speechbuddy.utils.Constants.Companion.SETTINGS_PREFS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

class SettingsPrefsManager @Inject constructor(context: Context) {

    private val dataStore = context.createDataStore(name = SETTINGS_PREFS)

    val settingsPreferencesFlow: Flow<SettingsPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            SettingsPreferences(
                autoBackup = preferences[AUTO_BACKUP] ?: true,
                darkMode = preferences[DARK_MODE] ?: false,
                initialPage = preferences[INITIAL_PAGE] ?: true
            )
        }

    suspend fun saveAutoBackup(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_BACKUP] = value
        }
    }

    suspend fun saveDarkMode(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = value
        }
    }

    suspend fun saveInitialPage(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[INITIAL_PAGE] = value
        }
    }

    suspend fun resetSettings() {
        dataStore.edit { preferences ->
            preferences[AUTO_BACKUP] = true
            preferences[DARK_MODE] = false
            preferences[INITIAL_PAGE] = true
        }
    }

    private object PreferencesKeys {
        val AUTO_BACKUP = booleanPreferencesKey(Constants.AUTO_BACKUP_PREF)
        val DARK_MODE = booleanPreferencesKey(Constants.DARK_MODE_PREF)
        val INITIAL_PAGE = booleanPreferencesKey(Constants.INITIAL_PAGE_PREF)
    }
}