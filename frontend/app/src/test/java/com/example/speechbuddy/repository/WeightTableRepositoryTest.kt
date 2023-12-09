package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.WeightRowDao
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.WeightRowEntity
import com.example.speechbuddy.data.remote.requests.BackupWeightTableRequest
import com.example.speechbuddy.data.remote.requests.WeightTableEntity
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.domain.models.WeightRow
import com.example.speechbuddy.domain.utils.Converters
import com.example.speechbuddy.ui.models.SymbolItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class WeightTableRepositoryTest {
    private lateinit var mockSymbolDao: SymbolDao
    private lateinit var mockWeightRowDao: WeightRowDao
    private lateinit var converters: Converters

    private lateinit var weightTableRepository: WeightTableRepository

    private val mockSymbol1 = Symbol(
        id = 1,
        text = "symbol1",
        imageUrl = null,
        categoryId = 1,
        isFavorite = false,
        isMine = false
    )
    private val mockSymbol2 = Symbol(
        id = 2,
        text = "symbol2",
        imageUrl = null,
        categoryId = 1,
        isFavorite = false,
        isMine = false
    )
    private val mockSymbol3 = Symbol(
        id = 3,
        text = "symbol3",
        imageUrl = null,
        categoryId = 1,
        isFavorite = false,
        isMine = false
    )

    @Before
    fun setUp() {
        mockWeightRowDao = mockk(relaxed = true)
        mockSymbolDao = mockk(relaxed = true)
        converters = Converters()

        // for `provideSuggestion calls symbolDao insert`()
        val symbolEntities = mutableListOf<SymbolEntity>()
        // create symbolEntities
        for (i in 1..500) {
            val tmpObj = SymbolEntity(
                id = 1,
                text = "symbol$i",
                imageUrl = null,
                categoryId = i,
                isFavorite = false,
                isMine = false
            )
            symbolEntities.add(tmpObj)
        }

        coEvery { mockSymbolDao.getSymbols() } returns flowOf(
            symbolEntities.toList()
        )

        weightTableRepository = WeightTableRepository(mockSymbolDao, mockWeightRowDao, converters)

    }

    @Test
    fun `should return BackupWeightTableRequest when getBackupWeightTableRequest is called`() = runBlocking {
        val weightRowEntities = mutableListOf<WeightRowEntity>()
        for (i in 1..500) {
            val tmpObj = WeightRowEntity(
                id = i,
                weights = List(500) { 0 }
            )
            weightRowEntities.add(tmpObj)
        }
        val weightTableEntities = weightRowEntities.map { weightRow ->
            WeightTableEntity(
                id = weightRow.id,
                weight = converters.fromList(weightRow.weights)
            )
        }
        val backupWeightTableRequest = BackupWeightTableRequest(weightTableEntities)

        coEvery { mockWeightRowDao.getAllWeightRows() } returns flowOf(weightRowEntities)
        val result = weightTableRepository.getBackupWeightTableRequest()
        assertEquals(backupWeightTableRequest, result)
    }

    @Test
    fun `should return sorted symbols when symbol is selected`() = runBlocking {
        val weightRowEntities = listOf(WeightRowEntity(id = 1, weights = (1..500).toList()))
        val expectedResult = mutableListOf<Symbol>()

        // create expectedResult
        for (i in 500 downTo 1) {
            val tmpObj = Symbol(
                id = 1,
                text = "symbol$i",
                imageUrl = null,
                categoryId = i,
                isFavorite = false,
                isMine = false
            )
            expectedResult.add(tmpObj)
        }

        coEvery { mockWeightRowDao.getWeightRowById(mockSymbol1.id) } returns flowOf(
            weightRowEntities
        )

        // Act
        val result = weightTableRepository.provideSuggestion(mockSymbol1).first()

        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun `should update weight table when symbol list is inputted`() = runBlocking {
        val weightRowEntities = mutableListOf<WeightRowEntity>()
        val symbolItems = listOf(
            SymbolItem(1, mockSymbol1),
            SymbolItem(2, mockSymbol2),
            SymbolItem(3, mockSymbol3)
        )

        // create weightRowEntities
        for (i in 1..500) {
            val tmpObj = WeightRowEntity(
                id = i,
                weights = List(500) { 0 }
            )
            weightRowEntities.add(tmpObj)
        }

        coEvery { mockWeightRowDao.getAllWeightRows() } returns flowOf(weightRowEntities)

        // Act
        weightTableRepository.update(symbolItems)

        // Assert
        // change weightRowEntities to be expected result
        weightRowEntities[0].weights[1]+1
        weightRowEntities[1].weights[2]+1
        val result = mockWeightRowDao.getAllWeightRows().first()
        assertEquals(weightRowEntities, result)
    }

    @Test
    fun `should update weight table for new symbol when updateWeightTableForNewSymbol is called`() = runBlocking {
        val symbol = Symbol(
            id = 501,
            text = "symbol1",
            imageUrl = null,
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )

        val weightRows = mutableListOf<WeightRow>()
        for (i in 1..500) {
            val tmpObj = WeightRow(
                id = i,
                weights = List(500) { 0 }
            )
            weightRows.add(tmpObj)
        }

        val weightRowEntities = mutableListOf<WeightRowEntity>()
        for (i in 1..500) {
            val tmpObj = WeightRowEntity(
                id = i,
                weights = List(500) { 0 }
            )
            weightRowEntities.add(tmpObj)
        }
        val newWeightRowEntity = WeightRowEntity(
            id = symbol.id,
            weights = List(weightRows.size + 1) { 0 }
        )

        coEvery { mockWeightRowDao.getAllWeightRows() } returns flowOf(weightRowEntities)
        coEvery { mockWeightRowDao.upsertAll(listOf(newWeightRowEntity)) } returns Unit

        val result = weightTableRepository.updateWeightTableForNewSymbol(symbol)

        coVerify { mockWeightRowDao.getAllWeightRows() }
        assertEquals(Unit, result)
    }

    @Test
    fun `should reset all weight rows when resetAllWeightRows is called`() = runBlocking {
        coEvery { mockWeightRowDao.deleteMySymbolsWeightRows() } returns Unit
        coEvery { mockWeightRowDao.resetOriginalSymbolsWeightRows(List(500){0}) } returns Unit
        val result = weightTableRepository.resetAllWeightRows()

        assertEquals(Unit, result)
    }

    @Test
    fun `should upsert all weight rows when replaceWeightTable is called`() = runBlocking {
        val weightRows = listOf(WeightRow(id = 1, weights = (1..500).toList()))
        val weightRowEntities = listOf(WeightRowEntity(id = 1, weights = (1..500).toList()))

        coEvery { mockWeightRowDao.upsertAll(weightRowEntities) } returns Unit

        val result = weightTableRepository.replaceWeightTable(weightRows)

        coVerify { mockWeightRowDao.upsertAll(weightRowEntities) }
        assertEquals(Unit, result)
    }
}
