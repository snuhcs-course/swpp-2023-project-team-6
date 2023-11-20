package com.example.speechbuddy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.speechbuddy.data.remote.models.SettingsBackupDto
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.repository.SettingsRepository
import com.example.speechbuddy.ui.models.BackupSettingsAlert
import com.example.speechbuddy.ui.models.BackupSettingsUiState
import com.example.speechbuddy.utils.ResponseCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BackupSettingsViewModel @Inject internal constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupSettingsUiState())
    val uiState: StateFlow<BackupSettingsUiState> = _uiState.asStateFlow()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    fun setAutoBackup(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isAutoBackupEnabled = value
            )
        }
        viewModelScope.launch {
            repository.setAutoBackup(value)
        }
        // TODO: Implement automated backup
    }

    fun toastDisplayed() {
        _uiState.update { currentState ->
            currentState.copy(
                alert = null
            )
        }
    }

    private fun getDarkMode(): Int {
        var darkMode: Boolean = false
        viewModelScope.launch {
            repository.getDarkMode().collect {
                darkMode = it.data?: false
            }
        }
        return if (!darkMode) { 0 } else { 1 }
    }

    private fun getInitialPage(): Int {
        var initialPage: Boolean = true
        viewModelScope.launch {
            repository.getInitialPage().collect {
                initialPage = it.data?: true
            }
        }
        return if (initialPage) { 1 } else { 0 }
    }

    private fun displayBackup() {
        viewModelScope.launch {
            repository.displayBackup(
                SettingsBackupDto(
                    getDarkMode(),
                    getInitialPage()
                )
            ).collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {}

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        _loading.value = false
                        _uiState.update { currentState ->
                            currentState.copy (
                                alert = BackupSettingsAlert.CONNECTION
                            )
                        }
                    }
                }
            }
        }
    }

    private fun symbolListBackup() {

        viewModelScope.launch {
            repository.symbolListBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {}

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        _loading.value = false
                        _uiState.update { currentState ->
                            currentState.copy (
                                alert = BackupSettingsAlert.CONNECTION
                            )
                        }
                    }
                }

            }
        }

    }

    private fun favoriteSymbolBackup() {
        viewModelScope.launch {
            repository.favoriteSymbolBackup().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        /* TODO: 백업 완성됐을 때 여기는 지우고 weight table에 복붙 */
                        _loading.value = false
                        _uiState.update { currentState ->
                            currentState.copy (
                                alert = BackupSettingsAlert.SUCCESS,
                                lastBackupDate = LocalDate.now().toString()
                            )
                        }
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        _loading.value = false
                        _uiState.update { currentState ->
                            currentState.copy (
                                alert = BackupSettingsAlert.CONNECTION
                            )
                        }
                    }
                }
            }
        }
    }

    private fun weightTableBackup() {
        viewModelScope.launch {
            //repository.weightTableBackup()
        }
    }

    fun backup() {
        _loading.value = true
        displayBackup()
        symbolListBackup()
        favoriteSymbolBackup()
        //weightTableBackup()
    }


}