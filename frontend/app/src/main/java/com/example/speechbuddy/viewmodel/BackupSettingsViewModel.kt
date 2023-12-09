package com.example.speechbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.repository.SettingsRepository
import com.example.speechbuddy.ui.models.BackupSettingsAlert
import com.example.speechbuddy.ui.models.BackupSettingsUiState
import com.example.speechbuddy.utils.ResponseCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BackupSettingsViewModel @Inject internal constructor(
    private val repository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BackupSettingsUiState(
            lastBackupDate = getLastBackupDate(),
            isAutoBackupEnabled = getAutoBackup()
        )
    )
    val uiState: StateFlow<BackupSettingsUiState> = _uiState.asStateFlow()

    fun setAutoBackup(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isAutoBackupEnabled = value
            )
        }
        viewModelScope.launch {
            repository.setAutoBackup(value)
        }
    }

    private fun setLastBackupDate(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                lastBackupDate = value
            )
        }
        viewModelScope.launch {
            repository.setLastBackupDate(value)
        }
    }

    private fun getAutoBackup(): Boolean {
        var autoBackup = false
        viewModelScope.launch {
            repository.getAutoBackup().collect {
                autoBackup = it.data ?: false
            }
        }
        return autoBackup
    }

    private fun getLastBackupDate(): String {
        var lastBackupDate = ""
        viewModelScope.launch {
            repository.getLastBackupDate().collect {
                lastBackupDate = it.data ?: ""
            }
        }
        return lastBackupDate
    }

    fun toastDisplayed() {
        _uiState.update { currentState ->
            currentState.copy(
                alert = null,
                loading = false,
                buttonEnabled = true
            )
        }
    }

    private fun changeLoadingState() {
        _uiState.update { currentState ->
            currentState.copy(
                loading = !currentState.loading,
                buttonEnabled = !currentState.buttonEnabled
            )
        }
    }

    private fun displayBackup() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.displayBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        symbolListBackup()
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        handleNoInternetConnection()
                    }
                }
            }
        }
    }

    private fun symbolListBackup() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.symbolListBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        favoriteSymbolBackup()
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        handleNoInternetConnection()
                    }
                }

            }
        }

    }

    private fun favoriteSymbolBackup() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.favoriteSymbolBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        weightTableBackup()
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        handleNoInternetConnection()
                    }
                }
            }
        }
    }

    private fun weightTableBackup() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.weightTableBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        handleSuccess()
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        handleNoInternetConnection()
                    }
                }
            }
        }
    }

    fun backup() {
        changeLoadingState()
        displayBackup()
    }

    private fun handleNoInternetConnection() {
        changeLoadingState()
        _uiState.update { currentState ->
            currentState.copy(
                alert = BackupSettingsAlert.CONNECTION
            )
        }
    }

    private fun handleSuccess() {
        changeLoadingState()
        setLastBackupDate(LocalDate.now().toString())
        _uiState.update { currentState ->
            currentState.copy(
                alert = BackupSettingsAlert.SUCCESS,
            )
        }
    }

}