package com.example.speechbuddy.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.utils.Constants.Companion.GUEST_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SessionManager {

    private val _cachedToken = MutableLiveData<AuthToken?>(null)
    private val _userId = MutableLiveData<Int?>(null)

    val cachedToken: LiveData<AuthToken?>
        get() = _cachedToken

    val userId: LiveData<Int?>
        get() = _userId

    /**
     * Authorized only when _userId is set to GUEST or refreshToken is not null
     * (because accessToken is temporarily set in case of 'reset password')
     */
    val isAuthorized = MediatorLiveData<Boolean>().apply {
        addSource(cachedToken) { value = checkAuthorization() }
        addSource(userId) { if (_cachedToken.value?.refreshToken == null) value = checkAuthorization() }
    }

    private fun checkAuthorization(): Boolean {
        return _cachedToken.value?.refreshToken != null || _userId.value == GUEST_ID
    }

    fun setAuthToken(value: AuthToken) {
        CoroutineScope(Dispatchers.Main).launch {
            if (_cachedToken.value != value) {
                _cachedToken.value = value
            }
        }
    }

    fun setUserId(value: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            if (_userId.value != value) {
                _userId.value = value
            }
        }
    }

    fun deleteToken() {
        CoroutineScope(Dispatchers.Main).launch {
            _cachedToken.value = null
            _userId.value = null
        }
    }

    fun enterGuestMode() {
        CoroutineScope(Dispatchers.Main).launch {
            _userId.value = GUEST_ID
        }
    }

    fun exitGuestMode() {
        CoroutineScope(Dispatchers.Main).launch {
            _userId.value = null
        }
    }

}