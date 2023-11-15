package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.WeightRowDao
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.data.local.models.WeightRowEntity
import com.example.speechbuddy.data.local.models.WeightRowMapper
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.domain.models.WeightRow
import com.example.speechbuddy.ui.models.SymbolItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeightTableRepository @Inject constructor(
    private val symbolDao: SymbolDao,
    private val weightRowDao: WeightRowDao
) {
    private val symbolMapper = SymbolMapper()
    private val weightRowMapper = WeightRowMapper()
    private val allSymbols = getAllSymbols()


    private fun getAllSymbols() = symbolDao.getSymbols().map { symbolEntities ->
        symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
    }

    private fun getAllWeightRows() =
        weightRowDao.getAllWeightRows().map { weightRowEntities ->
            weightRowEntities.map { weightRowEntity ->
                weightRowMapper.mapToDomainModel(weightRowEntity)
            }
        }

    private fun getWeightRowById(rowId: Int) =
        weightRowDao.getWeightRowById(rowId).map { weightRowEntities ->
            weightRowEntities.map { weightRowEntity ->
                weightRowMapper.mapToDomainModel(weightRowEntity)
            }
        }

    private fun updateWeightRow(weightRow: WeightRow, value: List<Int>) {
        val weightRowEntity = WeightRowEntity(
            id = weightRow.id,
            weights = value
        )
        weightRowDao.updateWeightRow(weightRowEntity) // may change to updateWeightRow function
    }

    fun provideSuggestion(symbol: Symbol): Flow<List<Symbol>> = flow {
        val allSymbolList = allSymbols.first()
        val oneWeightRow = getWeightRowById(symbol.id).first()
        val newSymbolList = mutableListOf<Symbol>()
        val listOfSymCntPairs = mutableListOf<Pair<Symbol, Int>>()
        val weights = oneWeightRow[0].weights

        for (i in 0 until allSymbolList.size) {
            listOfSymCntPairs.add(Pair(allSymbolList[i], weights[i]))
        }

        val sortedByNumberDescending = listOfSymCntPairs.sortedByDescending { it.second }

        for (i in sortedByNumberDescending) {
            newSymbolList.add(i.first)
        }
        emit(newSymbolList)
    }

    fun update(symbolList: List<SymbolItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            val weightRows = mutableListOf<WeightRow>() // weight table
            weightRows.clear()
            weightRows.addAll(fetchWeightRows())

            // loop through every symbol
            for (i in 0 until symbolList.size - 1) {
                val symbol = symbolList[i].symbol
                var dbIndex = 0
                // loop through every weight table
                // get the match index of weight row to symbol.id
                for (j in 0 until weightRows.size - 1) {
                    if (weightRows[j].id == symbol.id) {
                        dbIndex = j
                        break
                    }
                }
                val targetRow = weightRows[dbIndex]
                val weights = targetRow.weights
                val preSymbolWeights = weights.toIntArray()
                val aftSymbolId = symbolList[i + 1].symbol.id
                preSymbolWeights[aftSymbolId - 1] += 1 // weights의 iteration 0이 symbol의 id 1에 대응
                val aftSymbolWeights = preSymbolWeights

                updateWeightRow(targetRow, aftSymbolWeights.toList())
            }
        }
    }

    private suspend fun fetchWeightRows(): List<WeightRow> = withContext(Dispatchers.IO) {
        getAllWeightRows().first().map { it }.toList()
    }
}