package com.example.speechbuddy.repository

import android.content.Context
import android.util.Log
import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.WeigthTableOperations
import com.example.speechbuddy.data.local.models.CategoryMapper
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Entry
import com.example.speechbuddy.domain.models.Symbol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbolRepository @Inject constructor(
    private val symbolDao: SymbolDao,
    private val categoryDao: CategoryDao,
    private val weigthTableOperations: WeigthTableOperations
) {
    private val symbolMapper = SymbolMapper()
    private val categoryMapper = CategoryMapper()

    fun getSymbols(query: String) =
        if (query.isBlank()) getAllSymbols()
        else symbolDao.getSymbolsByQuery(query).map { symbolEntities ->
            symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
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

//    private fun getAllSymbols() = symbolDao.getSymbols().map { symbolEntities ->
//        symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
//    }

    private fun getAllSymbols() {
        val a = symbolDao.getSymbols().map { symbolEntities ->
            symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
        }.collect()
    }
    private fun getAllCategories() = categoryDao.getCategories().map { categoryEntities ->
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
    private lateinit var context: Context
    fun sort() {
        val file = weigthTableOperations.readFromFile("weight_table.txt")
        val a = getAllSymbols()
        Log.d("test", a.toString())
        Log.d("test", file.toString())
    }

    private fun update() {

    }
}