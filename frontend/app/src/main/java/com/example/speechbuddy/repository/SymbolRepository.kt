package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.models.CategoryMapper
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.data.remote.MySymbolRemoteSource
import com.example.speechbuddy.utils.ResponseHandler
import com.example.speechbuddy.data.remote.models.MySymbolDtoMapper
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Entry
import com.example.speechbuddy.domain.models.MySymbol
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.ResponseCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SymbolRepository @Inject constructor(
    private val symbolDao: SymbolDao,
    private val categoryDao: CategoryDao,
    // For remote-related processing
    private val mySymbolRemoteSource: MySymbolRemoteSource,
    private val mySymbolDtoMapper: MySymbolDtoMapper,
    private val responseHandler: ResponseHandler,
    private val sessionManager: SessionManager,
    // For local mapping
    private val symbolMapper: SymbolMapper,
    private val categoryMapper: CategoryMapper
) {


    fun getSymbols(query: String) =
        if (query.isBlank()) getAllSymbols()
        else symbolDao.getSymbolsByQuery(query).map { symbolEntities ->
            symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
        }

    fun getLastSymbol() = symbolDao.getLastSymbol().map { symbolEntities ->
        symbolEntities.first().let { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
    }

    fun getCategories(query: String) =
        if (query.isBlank()) getAllCategories()
        else categoryDao.getCategoriesByQuery(query).map { categoryEntities ->
            categoryEntities.map { categoryEntity -> categoryMapper.mapToDomainModel(categoryEntity) }
        }

    fun getEntries(query: String): Flow<List<Entry>> {
        val symbolsFlow = getSymbols(query)
        val categoriesFlow = getCategories(query)

        return symbolsFlow.combine(categoriesFlow) { symbols, categories ->
            val entries = mutableListOf<Entry>()
            entries.addAll(categories)
            entries.addAll(symbols)
            return@combine entries
        }
    }

    fun getFavoriteSymbols(query: String) =
        if (query.isBlank()) getAllFavoriteSymbols()
        else symbolDao.getFavoriteSymbolsByQuery(query).map { symbolEntities ->
            symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
        }

    private fun getAllSymbols() = symbolDao.getSymbols().map { symbolEntities ->
        symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
    }

    fun getAllCategories() = categoryDao.getCategories().map { categoryEntities ->
        categoryEntities.map { categoryEntity -> categoryMapper.mapToDomainModel(categoryEntity) }
    }

    private fun getAllFavoriteSymbols() = symbolDao.getFavoriteSymbols().map { symbolEntities ->
        symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
    }

    fun getSymbolsByCategory(category: Category) =
        symbolDao.getSymbolsByCategoryId(category.id).map { symbolEntities ->
            symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
        }

    suspend fun updateFavorite(symbol: Symbol, value: Boolean) {
        val symbolEntity = SymbolEntity(
            id = symbol.id,
            text = symbol.text,
            imageUrl = symbol.imageUrl,
            categoryId = symbol.categoryId,
            isFavorite = value,
            isMine = symbol.isMine
        )
        symbolDao.updateSymbol(symbolEntity)
    }

    suspend fun insertSymbol(symbol: Symbol) {
        val symbolEntity = symbolMapper.mapFromDomainModel(symbol)
        symbolDao.insertSymbol(symbolEntity)
    }

    suspend fun createSymbolBackup(
        symbolText: String,
        categoryId: Int,
        image: MultipartBody.Part
    ): Flow<Resource<MySymbol>> {
        return mySymbolRemoteSource.createSymbolBackup(getAuthHeader(), symbolText, categoryId, image)
            .map { response ->
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS.value) {
                    response.body()?.let { mySymbolDto ->
                        mySymbolDto.let {
                            Resource.success(
                                mySymbolDtoMapper.mapToDomainModel(
                                    mySymbolDto
                                )
                            )
                        }
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

    private fun getAuthHeader(): String {
        val accessToken = sessionManager.cachedToken.value?.accessToken
        return "Bearer $accessToken"
    }

    private fun returnUnknownError(): Resource<MySymbol> {
        return Resource.error(
            "Unknown error", null
        )
    }
}
