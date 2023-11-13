package com.example.speechbuddy.repository

import android.util.Log
import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.WeightRowDao
import com.example.speechbuddy.data.local.WeigthTableOperations
import com.example.speechbuddy.data.local.models.CategoryMapper
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.data.local.models.WeightRowEntity
import com.example.speechbuddy.data.local.models.WeightRowMapper
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Entry
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.domain.models.WeightRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


import com.example.speechbuddy.ui.models.SymbolItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.sql.RowId


@Singleton
class SymbolRepository @Inject constructor(
    private val symbolDao: SymbolDao,
    private val categoryDao: CategoryDao,
    private val weightTableOperations: WeigthTableOperations,
    private val weightRowDao: WeightRowDao
) {
    private val symbolMapper = SymbolMapper()
    private val categoryMapper = CategoryMapper()
    private val weightRowMapper = WeightRowMapper()



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

    // Below for weight table logic
    private fun getAllWeightRows() =
        weightRowDao.getAllWeightRows().map { weightRowEntities ->
            weightRowEntities.map { weightRowEntity -> weightRowMapper.mapToDomainModel(weightRowEntity) }
        }

    private fun getWeightRowById(rowId: Int) =
        weightRowDao.getWeightRowById(rowId).map { weightRowEntities ->
            weightRowEntities.map { weightRowEntity -> weightRowMapper.mapToDomainModel(weightRowEntity) }
        }

    private fun updateWeightRow(weightRow: WeightRow, value: List<Int>) {
        val weightRowEntity = WeightRowEntity(
            id = weightRow.id,
            weights = value
        )
        weightRowDao.updateWeightRow(weightRowEntity) // may change to updateWeightRow function
    }

    suspend fun provideSuggestion3(symbol: Symbol): Flow<List<Symbol>>{
        val allSymbolList = mutableListOf<Symbol>()
//        CoroutineScope(Dispatchers.IO).launch {
//            getAllSymbols().collect { symbolList ->
//                allSymbolList.clear()
//                allSymbolList.addAll(symbolList)
//            }
//        }
        CoroutineScope(Dispatchers.IO).async {
            getAllSymbols().collect{ symbolList ->
                allSymbolList.clear()
                allSymbolList.addAll(symbolList)
            }
        }

//        val allWeightRowList = mutableListOf<WeightRow>()
//        CoroutineScope(Dispatchers.IO).launch {
//            getAllWeightRows().collect{ weightRowList ->
//                Log.d("weightTable00", weightRowList.toString())
//                allWeightRowList.clear()
//                allWeightRowList.addAll(weightRowList)
//            }
//        }
//        Log.d("weightTable0", allWeightRowList.toString())

        Log.d("weightTable1", symbol.id.toString())

        val oneWeightRow = mutableListOf<WeightRow>()
//        CoroutineScope(Dispatchers.IO).launch {
//            getWeightRowById(symbol.id).collect{ weightRowList ->
//                oneWeightRow.clear()
//                oneWeightRow.addAll(weightRowList)
//            }
//        }
        CoroutineScope(Dispatchers.IO).async {
            getWeightRowById(symbol.id).collect{ weightRowList ->
                oneWeightRow.clear()
                oneWeightRow.addAll(weightRowList)
            }
        }

        Log.d("weightTable2", oneWeightRow.toString())
        val weights = oneWeightRow[0].weights
        Log.d("weightTable3", weights.toString())
        val newSymbolList = mutableListOf<Symbol>()
        val listOfSymCntPairs = mutableListOf<Pair<Symbol, Int>>()

        for(i in 0 until allSymbolList.size){
            // 1 ~ 500 symbol id, 0 ~ 499 symbolList, 0 ~ 499 weight in weightRow
            listOfSymCntPairs.add(Pair(allSymbolList[i], weights[symbol.id - 1]))
        }

        val sortedByNumDesc = listOfSymCntPairs.sortedByDescending { it.second }

        for(i in sortedByNumDesc){
            newSymbolList.add(i.first)
        }

        return flow{
            val newSymbols = newSymbolList.toList()
            emit(newSymbols)
        }
    }

    fun provideSuggestion2(symbol: Symbol): Flow<List<Symbol>> = flow {
        val allSymbolList = getAllSymbols().first() // Blocking call to collect the result

        Log.d("weightTable1", symbol.id.toString())

        val oneWeightRow = getWeightRowById(symbol.id).first() // Blocking call to collect the result
        Log.d("weightTable2", oneWeightRow.toString())

        val weights = oneWeightRow[0].weights
        Log.d("weightTable3", weights.toString())

        val newSymbolList = mutableListOf<Symbol>()
        val listOfSymCntPairs = mutableListOf<Pair<Symbol, Int>>()

        for (i in 0 until allSymbolList.size) {
            // 1 ~ 500 symbol id, 0 ~ 499 symbolList, 0 ~ 499 weight in weightRow
            listOfSymCntPairs.add(Pair(allSymbolList[i], weights[symbol.id - 1]))
        }

        val sortedByNumDesc = listOfSymCntPairs.sortedByDescending { it.second }

        for (i in sortedByNumDesc) {
            newSymbolList.add(i.first)
        }

        emit(newSymbolList.toList())
    }


    suspend fun update15(symbolList: List<SymbolItem>){
        Log.d("weightTable", "up1")
        for(i in 0 until symbolList.size - 1){
            val symbol = symbolList[i]
            Log.d("weightTable", "up2")
            val oneWeightRowList = getWeightRowById(symbol.id).first()
//            val oneWeightRowList = mutableListOf<WeightRow>()
//            CoroutineScope(Dispatchers.IO).launch {
//                getWeightRowById(symbol.id).collect{ weightRowList ->
//                    oneWeightRowList.clear()
//                    oneWeightRowList.addAll(weightRowList)
//                }
//            }
            Log.d("weightTable", oneWeightRowList.toString())
            val oneWeightRow = oneWeightRowList[0]
            val weights = oneWeightRow.weights

            val preSymbol = weights.toIntArray()
            val aftSymbolId = symbolList[i+1].symbol.id - 1 // weights의 iteration 0이 symbol의 id 1에 대응

            preSymbol[aftSymbolId] += 1
            val aftSymbol = preSymbol

            updateWeightRow(oneWeightRow, aftSymbol.toList())
        }
    }

    fun update4(symbolList: List<SymbolItem>) {
        Log.d("weightTable-up", "entered1")
        val scope = CoroutineScope(Dispatchers.IO)

        for (i in 0 until symbolList.size - 1) {
            Log.d("weightTable-up", "entered2")
            scope.launch {
                Log.d("weightTable-up", "entered3")
                val symbol = symbolList[i]
                val oneWeightRowList = mutableListOf<WeightRow>()
                Log.d("weightTable-up", "entered3.5")
                getWeightRowById(symbol.id).collect { weightRowList ->
                    oneWeightRowList.clear()
                    oneWeightRowList.addAll(weightRowList)
                }
                Log.d("weightTable-up", "entered4")
                val oneWeightRow = oneWeightRowList[0]
                val weights = oneWeightRow.weights
                Log.d("weightTable-up", "entered5")
                val preSymbol = weights.toIntArray()
                val aftSymbolId = symbolList[i + 1].symbol.id - 1
                Log.d("weightTable-up", "entered6")
                preSymbol[aftSymbolId] += 1
                val aftSymbol = preSymbol
                Log.d("weightTable-up", aftSymbol.toString())

                updateWeightRow(oneWeightRow, aftSymbol.toList())
            }
        }

        // Ensure all coroutines are complete before exiting the function
        runBlocking {
            scope.coroutineContext.cancelChildren()
        }
    }

    fun update5(symbolList: List<SymbolItem>) {
        for (i in 0 until symbolList.size - 1) {
            runBlocking(Dispatchers.IO) {
                val symbol = symbolList[i]
                val oneWeightRowList = mutableListOf<WeightRow>()

                getWeightRowById(symbol.id).collect { weightRowList ->
                    oneWeightRowList.clear()
                    oneWeightRowList.addAll(weightRowList)
                }

                val oneWeightRow = oneWeightRowList[0]
                val weights = oneWeightRow.weights

                val preSymbol = weights.toIntArray()
                val aftSymbolId = symbolList[i + 1].symbol.id - 1

                preSymbol[aftSymbolId] += 1
                val aftSymbol = preSymbol

                updateWeightRow(oneWeightRow, aftSymbol.toList())
            }
        }
    }

    fun update6(symbolList: List<SymbolItem>) {
        for (i in 0 until symbolList.size - 1) {
            GlobalScope.launch(Dispatchers.Main) { // or Dispatchers.IO if you're updating UI
                val symbol = symbolList[i]
                val oneWeightRowList = mutableListOf<WeightRow>()
                Log.d("weightTable-up", "entered1")

                withContext(Dispatchers.IO) {
                    getWeightRowById(symbol.id).collect { weightRowList ->
                        oneWeightRowList.clear()
                        oneWeightRowList.addAll(weightRowList)
                    }
                }
                Log.d("weightTable-up", "entered2")

                val oneWeightRow = oneWeightRowList[0]
                val weights = oneWeightRow.weights
                Log.d("weightTable-up", "entered3")

                val preSymbol = weights.toIntArray()
                val aftSymbolId = symbolList[i + 1].symbol.id - 1
                Log.d("weightTable-up", "entered4")

                preSymbol[aftSymbolId] += 1
                val aftSymbol = preSymbol
                Log.d("weightTable-up", "entered5")

                updateWeightRow(oneWeightRow, aftSymbol.toList())
            }
        }
    }

    fun update12(symbolList: List<SymbolItem>) {
        for (i in 0 until symbolList.size - 1) {
            val symbol = symbolList[i]
            val oneWeightRowList = runBlocking { getWeightRowById(symbol.id).first() }
            Log.d("weightTable", oneWeightRowList.toString())
            val oneWeightRow = oneWeightRowList[0]
            val weights = oneWeightRow.weights

            val preSymbol = weights.toIntArray()
            val aftSymbolId = symbolList[i + 1].symbol.id - 1

            preSymbol[aftSymbolId] += 1
            val aftSymbol = preSymbol

            updateWeightRow(oneWeightRow, aftSymbol.toList())
        }
    }

    fun update13(symbolList: List<SymbolItem>){
        runBlocking {
            for (i in 0 until symbolList.size - 1) {
                val symbol = symbolList[i]
                val oneWeightRowList = getWeightRowById(symbol.id).first()
                Log.d("weightTable", oneWeightRowList.toString())
                val oneWeightRow = oneWeightRowList[0]
                val weights = oneWeightRow.weights

                val preSymbol = weights.toIntArray()
                val aftSymbolId = symbolList[i + 1].symbol.id - 1

                preSymbol[aftSymbolId] += 1
                val aftSymbol = preSymbol

                updateWeightRow(oneWeightRow, aftSymbol.toList())
            }
        }
    }

    fun update16(symbolList: List<SymbolItem>) {
        Log.d("weightTable", "up1")

        runBlocking {
            for (i in 0 until symbolList.size - 1) {
                val symbol = symbolList[i]
                Log.d("weightTable", "up2")

                val oneWeightRowList = mutableListOf<WeightRow>()

                // Use async to perform the asynchronous operation
                val deferred = CoroutineScope(Dispatchers.IO).async {
                    getWeightRowById(symbol.id).collect { weightRowList ->
                        oneWeightRowList.clear()
                        oneWeightRowList.addAll(weightRowList)
                    }
                }
                Log.d("weightTable", "up3")

                // Wait for the async operation to complete
                deferred.await()

                Log.d("weightTable", oneWeightRowList.toString())

                val oneWeightRow = oneWeightRowList[0]
                val weights = oneWeightRow.weights

                val preSymbol = weights.toIntArray()
                val aftSymbolId = symbolList[i + 1].symbol.id

                preSymbol[aftSymbolId - 1] += 1
                val aftSymbol = preSymbol

                updateWeightRow(oneWeightRow, aftSymbol.toList())
            }
        }
    }

    fun update22(symbolList: List<SymbolItem>) {
        Log.d("weightTable", "up1")

        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until symbolList.size - 1) {
                val symbol = symbolList[i]
                Log.d("weightTable", "up2")

                val oneWeightRowList = withContext(Dispatchers.IO) {
                    getWeightRowById(symbol.id).toList()
                }

                Log.d("weightTable", oneWeightRowList.toString())

                val oneWeightRowL = oneWeightRowList.firstOrNull()

                Log.d("weightTable", oneWeightRowL.toString())
                if (oneWeightRowL != null) {
                    val oneWeightRow = oneWeightRowL[0]
                    val weights = oneWeightRow.weights

                    val preSymbol = weights.toIntArray()
                    val aftSymbolId = symbolList[i + 1].symbol.id - 1

                    preSymbol[aftSymbolId] += 1
                    val aftSymbol = preSymbol

                    withContext(Dispatchers.IO) {
                        updateWeightRow(oneWeightRow, aftSymbol.toList())
                    }
                }
            }
        }
    }

    fun update2(symbolList: List<SymbolItem>){
        val weightRows = mutableListOf<WeightRow>()

        CoroutineScope(Dispatchers.IO).launch {
            getAllWeightRows().collect{ weightRowList ->
                weightRows.clear()
                weightRows.addAll(weightRowList)
            }
            delay(5000)
        }
//        val job = GlobalScope.launch {
//            getAllWeightRows().collect{ weightRowList ->
//                weightRows.clear()
//                weightRows.addAll(weightRowList)
//            }
//        }
//        runBlocking { job.join() }

        Log.d("weightTable", weightRows.toString())

        for (i in 0 until symbolList.size - 1){
            val symbol = symbolList[i]
            Log.d("weightTable", "up2")
            var dbIndex = 0
            for( j in 0 until weightRows.size - 1){
                if(weightRows[j].id == symbol.id) dbIndex = j
            }
            val targetRow = weightRows[dbIndex] // outofIndex
            val weights = targetRow.weights
            val preSymbolWeights = weights.toIntArray()
            val aftSymbolId = symbolList[i+1].symbol.id
            preSymbolWeights[aftSymbolId - 1] += 1 // weights의 iteration 0이 symbol의 id 1에 대응
            val aftSymbolWeights = preSymbolWeights

            updateWeightRow(targetRow, aftSymbolWeights.toList())
        }
    }


    /**
     * Symbol object에서 id는 1부터 시작하기 때문에 list manipulation시 주의
     */
//    fun provideSuggestion(symbol: Symbol): Flow<List<Symbol>> {
//        val file = weightTableOperations.readFromFile("weight_table.txt")
//        val allSymbolList = mutableListOf<Symbol>()
//        // Launch a coroutine to collect the flow
//        CoroutineScope(Dispatchers.IO).launch {
//            getAllSymbols().collect { symbolList ->
//                allSymbolList.clear()
//                allSymbolList.addAll(symbolList)
//            }
//        }
//        val newSymbolList = mutableListOf<Symbol>()
//        val listOfSymCntPairs = mutableListOf<Pair<Symbol, Int>>()
//
//        val lines = file.readLines()
//        val matrix = mk.ndarray(lines.map { line ->
//            line.trim().split(",").map { it.toInt() }.toIntArray()
//        }.toTypedArray())
//
//        for (i in 0 until allSymbolList.size) {
//            listOfSymCntPairs.add(Pair(allSymbolList[i], matrix[symbol.id - 1][i]))
//        }
//
//        val sortedByNumberDescending = listOfSymCntPairs.sortedByDescending { it.second }
//
//        for (i in sortedByNumberDescending) {
//            newSymbolList.add(i.first)
//        }
//
//        return flow {
//            val newSymbols = newSymbolList.toList()
//            emit(newSymbols)
//        }
//    }
//
//    /**
//     * Symbol object에서 id는 1부터 시작하기 때문에 list manipulation시 주의
//     */
//    fun update(symbolList: List<SymbolItem>) {
//        val file = weightTableOperations.readFromFile("weight_table.txt")
//        val lines = file.readLines()
//        val matrix = mk.ndarray(lines.map { line ->
//            line.trim().split(",").map { it.toInt() }.toIntArray()
//        }.toTypedArray())
//
//        for (i in 0 until symbolList.size - 1) {
//            val preSymbol = mk.ndarray(matrix[symbolList[i].symbol.id - 1].toIntArray())
//            val aftSymbolId = symbolList[i + 1].symbol.id - 1
//            // purposely slpitted into two line
//            // preSymbol[aftSymbolId] + 1 result in int
//            preSymbol[aftSymbolId] += 1
//            val aftSymbol = preSymbol
//            val newString = aftSymbol.toList().toString().drop(1).dropLast(1)
//
//            weightTableOperations.replaceFileContent(
//                "weight_table.txt",
//                symbolList[i].symbol.id - 1, // the matrix index starts from 0 so it should -1
//                newString
//            )
//        }
//    }
}