package com.example.speechbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechbuddy.repository.AuthRepository
import com.example.speechbuddy.repository.SettingsRepository
import com.example.speechbuddy.repository.SymbolRepository
import com.example.speechbuddy.repository.UserRepository
import com.example.speechbuddy.repository.WeightTableRepository
import com.example.speechbuddy.ui.models.AccountSettingsAlert
import com.example.speechbuddy.ui.models.AccountSettingsUiState
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject internal constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val weightTableRepository: WeightTableRepository,
    private val symbolRepository: SymbolRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountSettingsUiState())
    val uiState: StateFlow<AccountSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getMyInfo().collect { resource ->
                if (resource.status == Status.SUCCESS) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            user = resource.data!!
                        )
                    }
                }
            }
        }
    }

    fun showAlert(alert: AccountSettingsAlert) {
        _uiState.update { currentState ->
            currentState.copy(
                alert = alert,
                buttonEnabled = false
            )
        }
    }

    fun hideAlert() {
        _uiState.update { currentState ->
            currentState.copy(
                alert = null,
                buttonEnabled = true
            )
        }
    }

    fun logout() {
        showAlert(AccountSettingsAlert.LOADING)
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        settingsRepository.resetSettings()
                        weightTableRepository.resetAllWeightRows()
                        symbolRepository.resetSymbolsAndFavorites()
                        userRepository.deleteUserInfo()
                        hideAlert()
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        showAlert(AccountSettingsAlert.CONNECTION)
                    }
                }
            }
        }
    }

    fun withdraw() {
        showAlert(AccountSettingsAlert.LOADING)
        viewModelScope.launch {
            authRepository.withdraw().collect { result ->
                when (result.code()) {
                    ResponseCode.SUCCESS.value -> {
                        settingsRepository.resetSettings()
                        weightTableRepository.resetAllWeightRows()
                        symbolRepository.resetSymbolsAndFavorites()
                        userRepository.deleteUserInfo()
                        hideAlert()
                    }

                    ResponseCode.NO_INTERNET_CONNECTION.value -> {
                        showAlert(AccountSettingsAlert.CONNECTION)
                    }
                }
            }
        }
    }

    fun exitGuestMode() {
        viewModelScope.launch {
            settingsRepository.resetSettings()
            weightTableRepository.resetAllWeightRows()
            symbolRepository.resetSymbolsAndFavorites()
            userRepository.deleteUserInfo()
        }
    }

    private fun displayBackup() {
        viewModelScope.launch {
            settingsRepository.displayBackup().collect { result ->
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
        viewModelScope.launch {
            settingsRepository.symbolListBackup().collect { result ->
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
        viewModelScope.launch {
            settingsRepository.favoriteSymbolBackup().collect { result ->
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
        viewModelScope.launch {
            settingsRepository.weightTableBackup().collect { result ->
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
        _uiState.update { currentState ->
            currentState.copy(
                alert = AccountSettingsAlert.LOADING,
                buttonEnabled = false
            )
        }
        displayBackup()
    }

    private fun handleNoInternetConnection() {
        _uiState.update { currentState ->
            currentState.copy(
                alert = AccountSettingsAlert.CONNECTION,
                buttonEnabled = false
            )
        }
    }

    private fun handleSuccess() {
        viewModelScope.launch {
            settingsRepository.setLastBackupDate(LocalDate.now().toString())
        }
        _uiState.update { currentState ->
            currentState.copy(
                alert = AccountSettingsAlert.BACKUP_SUCCESS,
                buttonEnabled = false
            )
        }
    }

}