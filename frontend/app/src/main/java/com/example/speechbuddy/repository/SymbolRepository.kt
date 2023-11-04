package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.models.CategoryMapper
import com.example.speechbuddy.data.local.models.SymbolMapper
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbolRepository @Inject constructor(
    private val symbolDao: SymbolDao,
    private val categoryDao: CategoryDao,
) {
    private val symbolMapper = SymbolMapper()
    private val categoryMapper = CategoryMapper()

    fun getSymbols() = symbolDao.getSymbols().map { symbolEntities ->
        symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
    }

    fun getCategories() = categoryDao.getCategories()

    companion object {
        // For Singleton Instantiation
        @Volatile
        private var instance: SymbolRepository? = null

        fun getInstance(
            symbolDao: SymbolDao,
            categoryDao: CategoryDao
        ) =
            instance ?: synchronized(this) {
                instance ?: SymbolRepository(
                    symbolDao,
                    categoryDao
                ).also { instance = it }
            }
    }
}