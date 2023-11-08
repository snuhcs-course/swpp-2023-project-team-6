package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.SymbolIdDto
import com.example.speechbuddy.service.SymbolCreationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject

class SymbolIdRemoteSource @Inject constructor(private val symbolCreationService: SymbolCreationService) {

    suspend fun createSymbolBackup(symbolText: String, categoryId: Int, image: MultipartBody.Part): Flow<Response<SymbolIdDto>> =
        flow {
            val symbolTextPart = symbolText.toRequestBody("text/plain".toMediaType())
            val categoryIdPart = categoryId.toString().toRequestBody("text/plain".toMediaType())

            val result = symbolCreationService.createSymbolBackup(symbolTextPart, categoryIdPart, image)
            emit(result)
        }
}