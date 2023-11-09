package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.WeigthTableOperations
import com.example.speechbuddy.data.local.models.CategoryMapper
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Entry
import com.example.speechbuddy.domain.models.Symbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


import com.example.speechbuddy.ui.models.SymbolItem
import kotlinx.coroutines.flow.flow
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.toIntArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toList


@Singleton
class SymbolRepository @Inject constructor(
    private val symbolDao: SymbolDao,
    private val categoryDao: CategoryDao,
    private val weightTableOperations: WeigthTableOperations
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

    private fun getAllSymbols() = symbolDao.getSymbols().map { symbolEntities ->
        symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
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

    /**
     * Symbol object에서 id는 1부터 시작하기 때문에 list manipulation시 주의
     */
    fun provideSuggestion(symbol: Symbol): Flow<List<Symbol>> {
        val file = weightTableOperations.readFromFile("weight_table.txt")
        val allSymbolList = mutableListOf<Symbol>()
        // Launch a coroutine to collect the flow
        CoroutineScope(Dispatchers.IO).launch {
            getAllSymbols().collect { symbolList ->
                allSymbolList.clear()
                allSymbolList.addAll(symbolList)
            }
        }
        val newSymbolList = mutableListOf<Symbol>()
        val listOfSymCntPairs = mutableListOf<Pair<Symbol, Int>>()

        val lines = file.readLines()
        val matrix = mk.ndarray(lines.map { line ->
            line.trim().split(",").map { it.toInt() }.toIntArray()
        }.toTypedArray())

        for (i in 0 until allSymbolList.size) {
            listOfSymCntPairs.add(Pair(allSymbolList[i], matrix[symbol.id - 1][i]))
        }

        val sortedByNumberDescending = listOfSymCntPairs.sortedByDescending { it.second }

        for (i in sortedByNumberDescending) {
            newSymbolList.add(i.first)
        }

        return flow {
            val newSymbols = newSymbolList.toList()
            emit(newSymbols)
        }
    }

    /**
     * Symbol object에서 id는 1부터 시작하기 때문에 list manipulation시 주의
     */
    fun update(symbolList: List<SymbolItem>) {
        val file = weightTableOperations.readFromFile("weight_table.txt")
        val lines = file.readLines()
        val matrix = mk.ndarray(lines.map { line ->
            line.trim().split(",").map { it.toInt() }.toIntArray()
        }.toTypedArray())

        for (i in 0 until symbolList.size - 1) {
            val preSymbol = mk.ndarray(matrix[symbolList[i].symbol.id - 1].toIntArray())
            val aftSymbolId = symbolList[i + 1].symbol.id - 1
            // purposely slpitted into two line
            // preSymbol[aftSymbolId] + 1 result in int
            preSymbol[aftSymbolId] += 1
            val aftSymbol = preSymbol
            val newString = aftSymbol.toList().toString().drop(1).dropLast(1)

            weightTableOperations.replaceFileContent(
                "weight_table.txt",
                symbolList[i].symbol.id - 1, // the matrix index starts from 0 so it should -1
                newString
            )
        }
    }
}