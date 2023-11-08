package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.SymbolIdDto
import com.example.speechbuddy.service.SymbolCreationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class SymbolIdRemoteSource @Inject constructor(private val symbolCreationService: SymbolCreationService) {

    suspend fun createSymbolBackup(symbolText: String, categoryId: Int, image: MultipartBody.Part): Flow<Response<SymbolIdDto>> =
        flow {
            val result = symbolCreationService.createSymbolBackup(symbolText, categoryId, image)
            emit(result)
        }
}