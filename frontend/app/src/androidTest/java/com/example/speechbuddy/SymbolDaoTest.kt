package com.example.speechbuddy

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.speechbuddy.data.local.AppDatabase
import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.models.SymbolEntity
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SymbolDaoTest {
    private lateinit var symbolDao: SymbolDao
    private lateinit var database: AppDatabase

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        symbolDao = database.symbolDao()
    }

    @After
    fun teardown() {
        database.close()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun should_upsert_symbols_and_get_symbols_when_fun_upsertAll_and_fun_getSymbols_called() {
        runBlocking {

            val symbols = listOf(
                SymbolEntity(
                    id = 1,
                    text = "symbol1",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                ),
                SymbolEntity(
                    id = 2,
                    text = "symbol2",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                ),
                SymbolEntity(
                    id = 3,
                    text = "symbol3",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                )
            )
            symbolDao.upsertAll(symbols)

            val symbolsFlow = symbolDao.getSymbols().first()
            Assert.assertEquals(symbols, symbolsFlow)

        }
    }

    @Test
    fun should_get_symbols_when_fun_getFavoriteSymbols_called() {
        runBlocking {
            val symbols = listOf(

                SymbolEntity(
                    id = 1,
                    text = "symbol1",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                ),
                SymbolEntity(
                    id = 2,
                    text = "favsymbol",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = true,
                    isMine = false
                ),
                SymbolEntity(
                    id = 3,
                    text = "another",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                )
            )
            symbolDao.upsertAll(symbols)

            val queriedCategoriesFlow = symbolDao.getFavoriteSymbols().first()
            Assert.assertEquals(1, queriedCategoriesFlow.size)
            Assert.assertTrue(queriedCategoriesFlow.any {
                it.text.contains(
                    "favsymbol",
                    ignoreCase = true
                )
            })
        }
    }

    @Test
    fun should_get_symbols_when_fun_getSymbolsByQuery_called() {
        runBlocking {
            val symbols = listOf(

                SymbolEntity(
                    id = 1,
                    text = "symbol1",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                ),
                SymbolEntity(
                    id = 2,
                    text = "symbol2",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                ),
                SymbolEntity(
                    id = 3,
                    text = "another",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                )
            )
            symbolDao.upsertAll(symbols)

            val queriedCategoriesFlow = symbolDao.getSymbolsByQuery("symbol").first()
            Assert.assertEquals(2, queriedCategoriesFlow.size)
            Assert.assertTrue(queriedCategoriesFlow.any {
                it.text.contains(
                    "symbol",
                    ignoreCase = true
                )
            })
        }
    }

    @Test
    fun should_get_symbols_when_fun_getFavoriteSymbolsByQuery_called() {
        runBlocking {
            val symbols = listOf(

                SymbolEntity(
                    id = 1,
                    text = "symbol1",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                ),
                SymbolEntity(
                    id = 2,
                    text = "favsymbolwithisfav",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = true,
                    isMine = false
                ),
                SymbolEntity(
                    id = 3,
                    text = "another",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = true,
                    isMine = false
                )
            )
            symbolDao.upsertAll(symbols)

            val queriedCategoriesFlow =
                symbolDao.getFavoriteSymbolsByQuery("favsymbolwithisfav").first()
            Assert.assertEquals(1, queriedCategoriesFlow.size)
            Assert.assertTrue(queriedCategoriesFlow.any {
                it.text.contains(
                    "favsymbolwithisfav",
                    ignoreCase = true
                )
            })
        }
    }

    @Test
    fun should_get_symbols_when_fun_getSymbolsByCategoryId_called() {
        runBlocking {
            val symbols = listOf(

                SymbolEntity(
                    id = 1,
                    text = "symbol1",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                ),
                SymbolEntity(
                    id = 2,
                    text = "symbol2",
                    imageUrl = null,
                    categoryId = 2,
                    isFavorite = true,
                    isMine = false
                ),
                SymbolEntity(
                    id = 3,
                    text = "another",
                    imageUrl = null,
                    categoryId = 3,
                    isFavorite = true,
                    isMine = false
                )
            )
            symbolDao.upsertAll(symbols)

            val queriedCategoriesFlow = symbolDao.getSymbolsByCategoryId(2).first()
            Assert.assertEquals(1, queriedCategoriesFlow.size)
            Assert.assertTrue(queriedCategoriesFlow.any {
                it.text.contains(
                    "symbol2",
                    ignoreCase = true
                )
            })
        }
    }

    //updateSymbol
    @Test
    fun should_update_symbols_when_fun_updateSymbol_called() {
        runBlocking {
            val symbols = listOf(

                SymbolEntity(
                    id = 1,
                    text = "symbol1",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                ),
                SymbolEntity(
                    id = 2,
                    text = "symbol2",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                ),
                SymbolEntity(
                    id = 3,
                    text = "another",
                    imageUrl = null,
                    categoryId = 1,
                    isFavorite = false,
                    isMine = false
                )
            )
            symbolDao.upsertAll(symbols)

            val symbolToUpdate = SymbolEntity(
                id = 2,
                text = "symbolupdated",
                imageUrl = null,
                categoryId = 1,
                isFavorite = false,
                isMine = false
            )

            symbolDao.updateSymbol(symbolToUpdate)
            val queriedCategoriesFlow = symbolDao.getSymbolsByQuery("symbolupdated").first()
            Assert.assertEquals(1, queriedCategoriesFlow.size)
            Assert.assertTrue(queriedCategoriesFlow.any {
                it.text.contains(
                    "symbolupdated",
                    ignoreCase = true
                )
            })
        }
    }

}