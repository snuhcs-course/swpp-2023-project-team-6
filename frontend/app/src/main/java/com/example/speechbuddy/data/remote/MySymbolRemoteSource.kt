package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.MySymbolDto
import com.example.speechbuddy.service.SymbolCreationService
import com.example.speechbuddy.utils.ResponseHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject

class MySymbolRemoteSource @Inject constructor(
    private val symbolCreationService: SymbolCreationService,
    private val responseHandler: ResponseHandler
) {
    suspend fun createSymbolBackup(
        header: String,
        symbolText: String,
        categoryId: Int,
        image: MultipartBody.Part
    ): Flow<Response<MySymbolDto>> =
        flow {
            try {
                val symbolTextPart = symbolText.toRequestBody("text/plain".toMediaType())
                val categoryIdPart = categoryId.toString().toRequestBody("text/plain".toMediaType())
                val result =
                    symbolCreationService.createSymbolBackup(
                        header,
                        symbolTextPart,
                        categoryIdPart,
                        image
                    )
                emit(result)
            } catch (e: Exception){
                emit(responseHandler.getConnectionErrorResponse())
            }
        }
}
