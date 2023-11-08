package com.example.speechbuddy.repository

import com.example.speechbuddy.data.remote.SymbolIdRemoteSource
import com.example.speechbuddy.data.remote.models.ErrorResponseMapper
import com.example.speechbuddy.data.remote.models.SymbolIdDtoMapper
import com.example.speechbuddy.domain.models.SymbolId
import com.example.speechbuddy.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbolCreationRepository @Inject constructor(
    private val symbolIdRemoteSource: SymbolIdRemoteSource,
    private val symbolIdDtoMapper: SymbolIdDtoMapper,
    private val errorResponseMapper: ErrorResponseMapper,
) {
    suspend fun createSymbolBackup(symbolText: String, categoryId: Int, image: MultipartBody.Part): Flow<Resource<SymbolId>> {
        return symbolIdRemoteSource.createSymbolBackup(symbolText, categoryId, image).map { response ->
            if (response.isSuccessful && response.code() == 200) {
                response.body()?.let { symbolIdDto ->
                    symbolIdDto.let {
                        Resource.success(
                            symbolIdDtoMapper.mapToDomainModel(
                                symbolIdDto
                            )
                        )
                    }
                } ?: returnUnknownError()
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMsgKey = errorResponseMapper.mapToDomainModel(responseBody).key
                    Resource.error(
                        errorMsgKey, null
                    )
                } ?: returnUnknownError()
            }
        }
    }

    private fun returnUnknownError(): Resource<SymbolId> {
        return Resource.error(
            "Unknown error", null
        )
    }
}

