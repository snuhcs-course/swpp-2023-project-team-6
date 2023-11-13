package com.example.speechbuddy.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.speechbuddy.domain.models.AuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SessionManager {

    private val _cachedToken = MutableLiveData<AuthToken?>()
    private val _isGuestMode = MutableLiveData(false)

    val accessToken: LiveData<String?>
        get() = _cachedToken.map { cachedToken -> cachedToken?.accessToken }

    val refreshToken: LiveData<String?>
        get() = _cachedToken.map { cachedToken -> cachedToken?.refreshToken }

    val isGuestMode: LiveData<Boolean>
        get() = _isGuestMode

    /**
     * Authorized only when _isGuestMode is set to true or refreshToken is not null
     * (because accessToken is temporarily set in case of 'reset password')
     */
    val isAuthorized: LiveData<Boolean>
        get() = _isGuestMode.map { isGuestMode -> isGuestMode || _cachedToken.value?.refreshToken != null }

    fun setAuthToken(value: AuthToken) {
        CoroutineScope(Dispatchers.Main).launch {
            if (_cachedToken.value != value) {
                _cachedToken.value = value
            }
        }
    }

    fun clearAuthToken() {
        CoroutineScope(Dispatchers.Main).launch {
            _cachedToken.value = null
        }
    }

    fun enterGuestMode() {
        CoroutineScope(Dispatchers.Main).launch {
            _isGuestMode.value = true
        }
    }

    fun exitGuestMode() {
        CoroutineScope(Dispatchers.Main).launch {
            _isGuestMode.value = false
        }
    }

}