package com.example.speechbuddy.repository

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
import com.example.speechbuddy.ui.models.SymbolItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.linalg.norm
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plusAssign
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import org.jetbrains.kotlinx.multik.ndarray.operations.toIntArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toList
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.asJavaRandom

@Singleton
class SymbolRepository @Inject constructor(
    private val symbolDao: SymbolDao,
    private val categoryDao: CategoryDao,
    private val weigthTableOperations: WeigthTableOperations
) {
    private val symbolMapper = SymbolMapper()
    private val categoryMapper = CategoryMapper()

    // Initialize Multik
//    private val mk = Multik.engine

    fun getSymbols(query: String = "") =
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

    fun provideSuggestion(symbol: Symbol): Flow<List<Symbol>> {
        val file = weigthTableOperations.readFromFile("weight_table.txt")
        val allSymbolList = mutableListOf<Symbol>()
        // Launch a coroutine to collect the flow
        CoroutineScope(Dispatchers.IO).launch {
            getAllSymbols().collect { symbolList ->
                allSymbolList.clear()
                allSymbolList.addAll(symbolList)
            }
        }
        var newSymbolList = mutableListOf<Symbol>()
        val listOfSymCntPairs = mutableListOf<Pair<Symbol, Int>>()

        val lines = file.readLines()
        val matrix = mk.ndarray(lines.map { line ->
            line.trim().split(",").map { it.toInt() }.toIntArray()
        }.toTypedArray())

        /*
        // compute cosine similarity between input symbol and other symbols
        // add to listOfSymbolCosinePairs
        for (i in 0 until allSymbolList.size - 1) {
            // skip input symbol
            if (symbol.id != allSymbolList[i].id) {
                val symbolMainMatrix: D1Array<Double> =
                    mk.ndarray(matrix[symbol.id].toDoubleArray())
                val symbolCompareMatrix: D1Array<Double> =
                    mk.ndarray(matrix[allSymbolList[i].id].toDoubleArray())

                /*
                //temp code reset 0 to random val
//                if (symbolCompareMatrix.all{ it == 0.000000000000000000e+00 }) {
//                    Log.d("test", "emty matrix")
//                    val temp = generateRandomVector(200).toList().toString()
//                    temp.toString()
//
//                    weigthTableOperations.replaceFileContent(
//                        "weight_table.txt",
//                        "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0",
//                        generateRandomVector(200).toList().toString().dropLast(1).drop(1)
//                    )
//                }
*/
                val cosineVal = cosineSimilarity(symbolMainMatrix, symbolCompareMatrix)
                listOfSymbolCosinePairs.add(
                    Pair(allSymbolList[i], cosineVal)
                )
            }
        }
        */

        for (i in 1 until allSymbolList.size) {
            listOfSymCntPairs.add(Pair(allSymbolList[i], matrix[symbol.id][i]))
        }

        val sortedByNumberDescending = listOfSymCntPairs.sortedByDescending { it.second }

        for (i in sortedByNumberDescending) {
            newSymbolList.add(i.first)
        }
        Log.d("test", "suggested symbols" + newSymbolList[0].text)
        Log.d("test", "suggested symbols" + newSymbolList[1].text)
        Log.d("test", "suggested symbols" + newSymbolList[2].text)


        return flow {
            val newSymbols = newSymbolList.toList()
            emit(newSymbols)
        }
    }

    fun update(symbolList: List<SymbolItem>) {
        val file = weigthTableOperations.readFromFile("weight_table.txt")
        val lines = file.readLines()
        val matrix = mk.ndarray(lines.map { line ->
            line.trim().split(",").map { it.toInt() }.toIntArray()
        }.toTypedArray())
        var result: D1Array<Int>

        for (i in 0 until symbolList.size - 1) {
            val preSymbol = mk.ndarray(matrix[symbolList[i].id].toIntArray())
            val oldString = preSymbol.toList().toString().drop(1).dropLast(1)
            val aftSymbolId = symbolList[i + 1].symbol.id
            // purposely slpitted into two line
            // preSymbol[aftSymbolId] + 1 result in int
            preSymbol[aftSymbolId] += 1
            val aftSymbol = preSymbol
            val newString = aftSymbol.toList().toString().drop(1).dropLast(1)

            weigthTableOperations.replaceFileContent("weight_table.txt", oldString, newString)



            Log.d("test", "preSymbol: " + preSymbol.toString())
            Log.d("test", "aftSymbolId: " + aftSymbolId.toString())
            Log.d("test", "preSymbol after add: " + preSymbol.toString())
        }
    }


    private fun cosineSimilarity(
        vectorMain: D1Array<Double>,
        vectorCompare: D1Array<Double>
    ): Double {
        // Compute dot product
        val dotProduct = mk.linalg.dot(vectorMain, vectorCompare)

        // Compute magnitudes (norms) of the vectors
        val normMain = mk.linalg.norm(vectorMain.reshape(1, vectorMain.size))
        val normCompare = mk.linalg.norm(vectorCompare.reshape(1, vectorCompare.size))

        // Compute cosine similarity
        var result = dotProduct / (normMain * normCompare)

        // when
        if (result.isNaN()) {
            result = 0.0
        }
        return result
    }

    private fun computeGradient(
        vectorMain: D1Array<Double>,
        vectorCompare: D1Array<Double>
    ): NDArray<Double, D1> {
        // Compute dot product
        val dotProduct = mk.linalg.dot(vectorMain, vectorCompare)

        // Compute magnitudes (norms) of the vectors
        val normMain = mk.linalg.norm(vectorMain.reshape(1, vectorMain.size))
        val normCompare = mk.linalg.norm(vectorCompare.reshape(1, vectorCompare.size))

        return (vectorMain / normMain) - (vectorCompare * dotProduct) / (normCompare.pow(3.0))
    }


    fun generateRandomVector(
        dimensions: Int,
        mean: Double = 0.0,
        stdDev: Double = 0.1
    ): DoubleArray {
        val randomVector = DoubleArray(dimensions) {
            Random.asJavaRandom().nextGaussian() * stdDev + mean
        }
        return normalizeVector(randomVector)
    }

    fun normalizeVector(vector: DoubleArray): DoubleArray {
        val norm = sqrt(vector.map { it * it }.sum())
        return vector.map { it / norm }.toDoubleArray()
    }


    private fun increaseCosineSimilarity(
        vectorMain: D1Array<Double>,
        vectorCompare: D1Array<Double>,
        learningRate: Double,
        threshold: Double
    ): D1Array<Double> {
        var similarity = cosineSimilarity(vectorMain, vectorCompare)
        while (similarity < threshold) {
            val gradient = computeGradient(vectorMain, vectorCompare)
            vectorCompare += learningRate * gradient
            similarity = cosineSimilarity(vectorMain, vectorCompare)
        }
        return vectorCompare
    }

    private fun addNoise() {

    }
}