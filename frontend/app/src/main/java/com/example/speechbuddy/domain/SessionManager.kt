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
    private val _temporaryToken =
        MutableLiveData<String?>() // Temporary access token used for reset password

    val cachedToken: LiveData<AuthToken?>
        get() = _cachedToken

    val temporaryToken: LiveData<String?>
        get() = _temporaryToken

    fun login(authToken: AuthToken) {
        setValue(authToken)
    }

    fun clearAuthToken() {
        setValue(null)
    }

    private fun setValue(value: AuthToken?) {
        CoroutineScope(Dispatchers.Main).launch {
            if (_cachedToken.value != value) {
                _cachedToken.value = value
            }
        }
    }

    fun setTemporaryToken(token: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            if (_temporaryToken.value != token) {
                _temporaryToken.value = token
            }
        }
    }

}