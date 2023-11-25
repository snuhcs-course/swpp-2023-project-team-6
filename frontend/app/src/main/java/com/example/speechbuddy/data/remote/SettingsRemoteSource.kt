package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.FavoritesListDto
import com.example.speechbuddy.data.remote.models.SettingsBackupDto
import com.example.speechbuddy.data.remote.models.SymbolListDto
import com.example.speechbuddy.data.remote.requests.BackupWeightTableRequest
import com.example.speechbuddy.service.BackupService
import com.example.speechbuddy.utils.ResponseHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class SettingsRemoteSource @Inject constructor(
    private val backupService: BackupService,
    private val responseHandler: ResponseHandler
) {
    suspend fun getDisplaySettings(authHeader: String): Flow<Response<SettingsBackupDto>> =
        flow {
            try {
                val result = backupService.getDisplaySettings(authHeader)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun getSymbolList(authHeader: String): Flow<Response<SymbolListDto>> =
        flow {
            try {
                val result = backupService.getSymbolList(authHeader)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun getFavoritesList(authHeader: String): Flow<Response<FavoritesListDto>> =
        flow {
            try {
                val result = backupService.getFavoriteSymbolList(authHeader)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

    suspend fun getWeightTable(authHeader: String): Flow<Response<BackupWeightTableRequest>> =
        flow {
            try {
                val result = backupService.getWeightTable(authHeader)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }
}