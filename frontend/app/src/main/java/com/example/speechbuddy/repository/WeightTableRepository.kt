package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.WeightRowDao
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.data.local.models.WeightRowEntity
import com.example.speechbuddy.data.local.models.WeightRowMapper
import com.example.speechbuddy.data.remote.requests.BackupWeightTableRequest
import com.example.speechbuddy.data.remote.requests.WeightTableEntity
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.domain.models.WeightRow
import com.example.speechbuddy.domain.utils.Converters
import com.example.speechbuddy.ui.models.SymbolItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeightTableRepository @Inject constructor(
    private val symbolDao: SymbolDao,
    private val weightRowDao: WeightRowDao,
    private val converters: Converters
) {
    private val symbolMapper = SymbolMapper()
    private val weightRowMapper = WeightRowMapper()
    private var allSymbols = getAllSymbols()


    private fun getAllSymbols() = symbolDao.getSymbols().map { symbolEntities ->
        symbolEntities.map { symbolEntity -> symbolMapper.mapToDomainModel(symbolEntity) }
    }

    private fun getAllWeightRows() =
        weightRowDao.getAllWeightRows().map { weightRowEntities ->
            weightRowEntities.map { weightRowEntity ->
                weightRowMapper.mapToDomainModel(weightRowEntity)
            }
        }

    suspend fun deleteAllWeightRows() {
        weightRowDao.deleteAllWeightRows()
    }

    suspend fun replaceWeightTable(weightRowList: List<WeightRow>) {

        val weightRowEntityList = mutableListOf<WeightRowEntity>()
        for (weightRow in weightRowList) {
            weightRowEntityList.add(weightRowMapper.mapFromDomainModel(weightRow))
        }
        weightRowDao.upsertAll(weightRowEntityList)
    }

    suspend fun getBackupWeightTableRequest(): BackupWeightTableRequest {
        val weightRowList = getAllWeightRows().firstOrNull() ?: emptyList()
        val weightTableEntities = weightRowList.map { weightRow ->
            WeightTableEntity(
                id = weightRow.id,
                weight = converters.fromList(weightRow.weights)
            )
        }
        return BackupWeightTableRequest(weight_table = weightTableEntities)
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

    fun updateWeightTableForNewSymbol(symbol: Symbol) {
        CoroutineScope(Dispatchers.IO).launch {
            val weightRows = mutableListOf<WeightRow>()
            weightRows.clear()
            weightRows.addAll(fetchWeightRows())

            // update existing weightRows
            for (weightRow in weightRows) {
                val newWeights = mutableListOf<Int>()
                newWeights.addAll(weightRow.weights)
                newWeights.add(0)
                updateWeightRow(weightRow, newWeights)
            }

            // insert new weightRow for new symbol
            val newWeightRowEntity = WeightRowEntity(
                id = symbol.id,
                weights = List(weightRows.size + 1) { 0 }
            )
            val newWeightRowEntities = mutableListOf<WeightRowEntity>()
            newWeightRowEntities.add(newWeightRowEntity)
            weightRowDao.upsertAll(newWeightRowEntities)

            allSymbols = getAllSymbols()

        }
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
                val symbol2 = symbolList[i + 1].symbol
                var dbIndex = 0
                var dbIndex2 = 0
                // loop through every weight table
                // get the match index of weight row to symbol.id
                for (j in 0 until weightRows.size) {
                    // weightRow들 중 그 id가 주어진 symbol의 id인 것을 고른다. 즉 주어진 앞 symbol의 weightRow를 찾는다.
                    if (weightRows[j].id == symbol.id) {
                        dbIndex = j
                        break
                    }
                }
                // 앞 심볼의 weightrow에서 업데이트되어야 할 값이 weights의 몇 번째에 있는지 확인
                for (j in 0 until weightRows.size) {
                    if (weightRows[j].id == symbol2.id) {
                        dbIndex2 = j
                    }
                }
                val targetRow = weightRows[dbIndex] // 위에서 찾은 weightRow
                val weights = targetRow.weights
                val preSymbolWeights = weights.toIntArray() // 앞 symbol의 weights

                preSymbolWeights[dbIndex2] += 1

                val aftSymbolWeights = preSymbolWeights

                updateWeightRow(targetRow, aftSymbolWeights.toList())
            }
        }
    }

    private suspend fun fetchWeightRows(): List<WeightRow> = withContext(Dispatchers.IO) {
        getAllWeightRows().first().map { it }.toList()
    }
}