package com.example.speechbuddy.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speechbuddy.data.local.SettingsPrefsManager
import com.example.speechbuddy.data.remote.models.SettingsBackupDto
import com.example.speechbuddy.data.remote.requests.AuthRefreshRequest
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.service.BackupService
import com.example.speechbuddy.ui.models.InitialPage
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.ResponseHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsPrefManager: SettingsPrefsManager,
    private val backupService: BackupService,
    private val responseHandler: ResponseHandler,
    private val sessionManager: SessionManager
) {

    private val _darkModeLiveData = MutableLiveData<Boolean?>()
    val darkModeLiveData: LiveData<Boolean?>
        get() = _darkModeLiveData

    suspend fun setDarkMode(value: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            if (_darkModeLiveData.value != value) {
                _darkModeLiveData.value = value
            }
        }
        settingsPrefManager.saveDarkMode(value)
    }

    suspend fun setInitialPage(page: InitialPage) {
        if (page == InitialPage.SYMBOL_SELECTION) {
            settingsPrefManager.saveInitialPage(true)
        } else {
            settingsPrefManager.saveInitialPage(false)
        }
    }

    suspend fun setAutoBackup(value: Boolean) {
        settingsPrefManager.saveAutoBackup(value)
    }

    fun getDarkMode(): Flow<Resource<Boolean>> {
        return settingsPrefManager.settingsPreferencesFlow.map { settingsPreferences ->
            Resource.success(settingsPreferences.darkMode)
        }
    }

    fun getInitialPage(): Flow<Resource<Boolean>> {
        return settingsPrefManager.settingsPreferencesFlow.map { settingsPreferences ->
            Resource.success(settingsPreferences.initialPage)
        }
    }

    fun getAutoBackup(): Flow<Resource<Boolean>> {
        return settingsPrefManager.settingsPreferencesFlow.map { settingsPreferences ->
            Resource.success(settingsPreferences.autoBackup)
        }
    }

    suspend fun displayBackup(settingsBackupDto: SettingsBackupDto): Flow<Response<Void>> =
        flow {
            try {
                val result = backupService.displayBackup(getAuthHeader(), settingsBackupDto)
                emit(result)
                Log.d("repository", result.code().toString())
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    /* TODO: favorite symbol, symbol list, weight table backup */
    /* TODO: setting 받아오기 */

    private fun getAuthHeader(): String {
        val accessToken = sessionManager.cachedToken.value?.accessToken
        return "Bearer $accessToken"
    }

}