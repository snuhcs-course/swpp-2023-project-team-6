package com.example.speechbuddy.repository

import com.example.speechbuddy.data.remote.SymbolIdRemoteSource
import com.example.speechbuddy.data.remote.models.ErrorResponseMapper
import com.example.speechbuddy.data.remote.models.MySymbolDtoMapper
import com.example.speechbuddy.domain.models.MySymbol
import com.example.speechbuddy.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbolCreationRepository @Inject constructor(
    private val symbolIdRemoteSource: SymbolIdRemoteSource,
    private val mySymbolDtoMapper: MySymbolDtoMapper,
    private val errorResponseMapper: ErrorResponseMapper,
) {
    suspend fun createSymbolBackup(symbolText: String, categoryId: Int, image: MultipartBody.Part): Flow<Resource<MySymbol>> {
        return symbolIdRemoteSource.createSymbolBackup(symbolText, categoryId, image).map { response ->
            if (response.isSuccessful && response.code() == 200) {
                response.body()?.let { MySymbolDto ->
                    MySymbolDto.let {
                        Resource.success(
                            mySymbolDtoMapper.mapToDomainModel(
                                MySymbolDto
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

    private fun returnUnknownError(): Resource<MySymbol> {
        return Resource.error(
            "Unknown error", null
        )
    }
}

