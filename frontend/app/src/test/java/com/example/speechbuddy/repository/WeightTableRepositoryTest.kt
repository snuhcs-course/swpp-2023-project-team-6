package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.WeightRowDao
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.WeightRowEntity
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.ui.models.SymbolItem
import io.mockk.coEvery
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

    private val mockWeight = mutableListOf(500)


    @Before
    fun setUp() {
        mockWeightRowDao = mockk(relaxed = true)
        mockSymbolDao = mockk(relaxed = true)

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

        weightTableRepository = WeightTableRepository(mockSymbolDao, mockWeightRowDao)

    }

    @Test
    fun `provideSuggestion calls symbolDao insert`() = runBlocking {
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
    fun `update calls weightRowDao update`() = runBlocking {
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
}