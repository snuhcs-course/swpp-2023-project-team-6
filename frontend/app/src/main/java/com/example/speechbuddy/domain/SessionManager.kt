package com.example.speechbuddy.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speechbuddy.data.local.AuthTokenPrefsManager
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.repository.AuthRepository
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
    private val _temporaryToken = MutableLiveData<AuthToken?>()

    val cachedToken: LiveData<AuthToken?>
        get() = _cachedToken

    val temporaryToken: LiveData<AuthToken?>
        get() = _temporaryToken

    fun login(authToken: AuthToken) {
        setValue(authToken)
    }

    // TODO: 나중에 SettingsScreen에서 이 logout() 호출
    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            setValue(null)
        }
    }

    private fun setValue(value: AuthToken?) {
        CoroutineScope(Dispatchers.Main).launch {
            if (_cachedToken.value != value) {
                _cachedToken.value = value
            }
        }
    }

    fun setTemporaryToken(authToken: AuthToken?) {
        _temporaryToken.value = authToken
    }

}