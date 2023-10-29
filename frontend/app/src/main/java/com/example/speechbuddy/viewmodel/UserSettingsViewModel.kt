package com.example.speechbuddy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingsScreenViewModel @Inject internal constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var showLogoutDialog by mutableStateOf(false)
        private set

    var showWithdrawalDialog by mutableStateOf(false)
        private set

    var showSecondWithdrawalDialog by mutableStateOf(false)
        private set

    fun onShowLogoutDialog() {
        showLogoutDialog = true
    }

    fun onHideLogoutDialog() {
        showLogoutDialog = false
    }

    fun onShowWithdrawalDialog() {
        showWithdrawalDialog = true
    }

    fun onHideWithdrawalDialog() {
        showWithdrawalDialog = false
    }

    fun onShowSecondWithdrawalDialog() {
        showSecondWithdrawalDialog = true
    }

    fun onHideSecondWithdrawalDialog() {
        showSecondWithdrawalDialog = false
    }

    fun logout() {
        viewModelScope.launch {
            //repository.logout()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            //repository.deleteAccount()
        }
    }
}