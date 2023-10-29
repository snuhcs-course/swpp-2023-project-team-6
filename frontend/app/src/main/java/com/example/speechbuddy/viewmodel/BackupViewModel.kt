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
class BackupViewModel @Inject internal constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var automaticBackupChecked by mutableStateOf(true)
        private set

    fun backup() {
        viewModelScope.launch {
            //repository.backup()
        }
    }

    fun onAutomaticBackupCheckedChange() {
        automaticBackupChecked = !automaticBackupChecked
    }
}