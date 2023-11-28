package com.example.speechbuddy.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.createDataStore
import com.example.speechbuddy.data.local.UserIdPrefsManager.PreferencesKeys.USER_ID
import com.example.speechbuddy.utils.Constants.Companion.USER_ID_PREF
import com.example.speechbuddy.utils.Constants.Companion.USER_ID_PREFS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserIdPrefsManager @Inject constructor(context: Context) {

    private val dataStore = context.createDataStore(name = USER_ID_PREFS)

    val preferencesFlow: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[USER_ID] ?: -1
        }

    suspend fun saveUserId(id: Int) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    suspend fun clearUserId() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object PreferencesKeys {
        val USER_ID = intPreferencesKey(USER_ID_PREF)
    }
}