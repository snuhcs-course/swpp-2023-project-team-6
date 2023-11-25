package com.example.speechbuddy.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speechbuddy.data.local.SettingsPrefsManager
import com.example.speechbuddy.data.remote.SettingsRemoteSource
import com.example.speechbuddy.data.remote.models.SettingsBackupDto
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.domain.models.WeightRow
import com.example.speechbuddy.domain.utils.Converters
import com.example.speechbuddy.service.BackupService
import com.example.speechbuddy.ui.models.InitialPage
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.ResponseHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    private val sessionManager: SessionManager,
    private val symbolRepository: SymbolRepository,
    private val weightTableRepository: WeightTableRepository,
    private val settingsRemoteSource: SettingsRemoteSource,
    private val converters: Converters,
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

    suspend fun setLastBackupDate(value: String) {
        settingsPrefManager.saveLastBackupDate(value)
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

    fun getLastBackupDate(): Flow<Resource<String>> {
        return settingsPrefManager.settingsPreferencesFlow.map { settingsPreferences ->
            Resource.success(settingsPreferences.lastBackupDate)
        }
    }

    suspend fun displayBackup(): Flow<Response<Void>> =
        flow {
            try {
                var darkMode: Int? = 0
                var initialPage: Int? = 1
                getDarkMode().first().data?.let {
                    darkMode = if (it) 1 else 0
                }
                getInitialPage().first().data?.let {
                    initialPage = if (it) 1 else 0
                    Log.d("test", "initialPage: $initialPage")
                }
                val result = backupService.displayBackup(
                    getAuthHeader(),
                    SettingsBackupDto(darkMode, initialPage)
                )
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun symbolListBackup(): Flow<Response<Void>> =
        flow {
            try {
                if (symbolRepository.getUserSymbolsIdString().isEmpty()) {
                    val result = backupService.symbolListBackup(
                        header = getAuthHeader()
                    )
                    emit(result)
                } else {
                    val result = backupService.symbolListBackup(
                        symbolRepository.getUserSymbolsIdString(),
                        getAuthHeader()
                    )
                    emit(result)
                }
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun favoriteSymbolBackup(): Flow<Response<Void>> =
        flow {
            try {
                if (symbolRepository.getFavoriteSymbolsIdString().isEmpty()) {
                    val result = backupService.favoriteSymbolBackup(
                        header = getAuthHeader()
                    )
                    emit(result)
                } else {
                    val result = backupService.favoriteSymbolBackup(
                        symbolRepository.getFavoriteSymbolsIdString(),
                        getAuthHeader()
                    )
                    emit(result)
                }
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun weightTableBackup(): Flow<Response<Void>> =
        flow {
            try {
                val result = backupService.weightTableBackup(
                    getAuthHeader(),
                    weightTableRepository.getBackupWeightTableRequest()
                )
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    private fun getAuthHeader(): String {
        val accessToken = sessionManager.cachedToken.value?.accessToken
        return "Bearer $accessToken"
    }

    suspend fun getDisplaySettingsFromRemote(accessToken: String?): Flow<Resource<Void>> {
        return settingsRemoteSource.getDisplaySettings("Bearer $accessToken").map { response ->
            if (response.isSuccessful && response.code() == ResponseCode.SUCCESS.value) {
                response.body()?.let { settingsDto ->
                    val displayMode = settingsDto.displayMode
                    val defaultMenu = settingsDto.defaultMenu
                    if (displayMode == 0) {
                        setDarkMode(false)
                        settingsPrefManager.saveDarkMode(false)
                    } else {
                        setDarkMode(true)
                        settingsPrefManager.saveDarkMode(true)
                    }
                    if (defaultMenu == 0) {
                        setInitialPage(InitialPage.SYMBOL_SELECTION)
                    } else {
                        setInitialPage(InitialPage.TEXT_TO_SPEECH)
                    }
                    Resource.success(null)
                } ?: returnUnknownError()
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMsgKey = responseHandler.parseErrorResponse(responseBody).key
                    Resource.error(
                        errorMsgKey, null
                    )
                } ?: returnUnknownError()
            }
        }
    }

    suspend fun getSymbolListFromRemote(accessToken: String?): Flow<Resource<Void>> {
        return settingsRemoteSource.getSymbolList("Bearer $accessToken").map { response ->
            if (response.isSuccessful && response.code() == ResponseCode.SUCCESS.value) {
                response.body()?.let { symbolListDto ->
                    for (symbolDto in symbolListDto.mySymbols) {
                        symbolRepository.insertSymbol(
                            Symbol(
                                id = symbolDto.id,
                                text = symbolDto.text,
                                imageUrl = symbolDto.image,
                                categoryId = symbolDto.category,
                                isFavorite = false,
                                isMine = true
                            )
                        )
                    }

                    Resource.success(null)
                } ?: returnUnknownError()
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMsgKey = responseHandler.parseErrorResponse(responseBody).key
                    Resource.error(
                        errorMsgKey, null
                    )
                } ?: returnUnknownError()
            }
        }
    }

    suspend fun getFavoritesListFromRemote(accessToken: String?): Flow<Resource<Void>> {
        return settingsRemoteSource.getFavoritesList("Bearer $accessToken").map { response ->
            if (response.isSuccessful && response.code() == ResponseCode.SUCCESS.value) {
                response.body()?.let { favoritesListDto ->
                    for (symbolIdDto in favoritesListDto.results) {
                        symbolRepository.getSymbolsById(symbolIdDto.id).collect { symbol ->
                            symbolRepository.updateFavorite(symbol, true)
                        }
                    }
                    Resource.success(null)
                } ?: returnUnknownError()
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMsgKey = responseHandler.parseErrorResponse(responseBody).key
                    Resource.error(
                        errorMsgKey, null
                    )
                } ?: returnUnknownError()
            }
        }
    }

    suspend fun getWeightTableFromRemote(accessToken: String?): Flow<Resource<Void>> {
        return settingsRemoteSource.getWeightTable("Bearer $accessToken").map { response ->
            if (response.isSuccessful && response.code() == ResponseCode.SUCCESS.value) {
                response.body()?.let { weightTableListEntity ->
                    val weightRow = mutableListOf<WeightRow>()
                    for (weightTableEntity in weightTableListEntity.weight_table) {
                        weightRow.add(
                            WeightRow(
                                weightTableEntity.id,
                                converters.fromString(weightTableEntity.weight)
                            )
                        )
                    }
                    weightTableRepository.replaceWeightTable(weightRow.toList())
                    Resource.success(null)
                } ?: returnUnknownError()
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMsgKey = responseHandler.parseErrorResponse(responseBody).key
                    Resource.error(
                        errorMsgKey, null
                    )
                } ?: returnUnknownError()
            }
        }
    }


    private fun <T> returnUnknownError(): Resource<T> {
        return Resource.error(
            "Unknown error", null
        )
    }
}