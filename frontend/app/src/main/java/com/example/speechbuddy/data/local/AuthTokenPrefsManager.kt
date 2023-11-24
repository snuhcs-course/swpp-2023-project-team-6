package com.example.speechbuddy.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import com.example.speechbuddy.data.local.AuthTokenPrefsManager.PreferencesKeys.ACCESS
import com.example.speechbuddy.data.local.AuthTokenPrefsManager.PreferencesKeys.REFRESH
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.utils.Constants.Companion.ACCESS_TOKEN_PREF
import com.example.speechbuddy.utils.Constants.Companion.AUTH_TOKEN_PREFS
import com.example.speechbuddy.utils.Constants.Companion.REFRESH_TOKEN_PREF
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AuthTokenPrefsManager @Inject constructor(context: Context) {

    private val dataStore = context.createDataStore(name = AUTH_TOKEN_PREFS)

    val preferencesFlow: Flow<AuthToken> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AuthToken(
                accessToken = preferences[ACCESS] ?: "",
                refreshToken = preferences[REFRESH] ?: ""
            )
        }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS] = token
        }
    }

    suspend fun saveAuthToken(authToken: AuthToken) {
        dataStore.edit { preferences ->
            preferences[ACCESS] = authToken.accessToken ?: ""
            preferences[REFRESH] = authToken.refreshToken ?: ""
        }
    }

    suspend fun clearAuthToken() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object PreferencesKeys {
        val ACCESS = stringPreferencesKey(ACCESS_TOKEN_PREF)
        val REFRESH = stringPreferencesKey(REFRESH_TOKEN_PREF)
    }

}