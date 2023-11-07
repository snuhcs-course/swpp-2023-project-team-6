package com.example.speechbuddy.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speechbuddy.data.local.AuthTokenPrefsManager
import com.example.speechbuddy.domain.models.AuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val authTokenPrefsManager: AuthTokenPrefsManager
) {

    private val _cachedToken = MutableLiveData<AuthToken?>()

    val cachedToken: LiveData<AuthToken?>
        get() = _cachedToken

    fun login(authToken: AuthToken) {
        setValue(authToken)
    }

    // TODO: 나중에 SettingsScreen에서 이 logout() 호출
    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            setValue(null)
            authTokenPrefsManager.clearAuthToken()
        }
    }

    private fun setValue(value: AuthToken?) {
        CoroutineScope(Dispatchers.Main).launch {
            if (_cachedToken.value != value) {
                _cachedToken.value = value
            }
        }
    }

}