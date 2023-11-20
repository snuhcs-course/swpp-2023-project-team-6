package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.SettingsBackupDto
import com.example.speechbuddy.service.BackupService
import com.example.speechbuddy.utils.ResponseHandler
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class SettingsRemoteSource @Inject constructor(
    private val backupService: BackupService,
    private val responseHandler: ResponseHandler
){
    suspend fun getDisplaySettings(authHeader: String): Flow<Response<SettingsBackupDto>> =
        flow {
            try {
                val result = backupService.getDisplaySettings(authHeader)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }
}