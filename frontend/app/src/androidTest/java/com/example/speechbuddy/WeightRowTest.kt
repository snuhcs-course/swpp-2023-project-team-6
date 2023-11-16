package com.example.speechbuddy

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.speechbuddy.data.local.AppDatabase
import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.WeightRowDao
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.WeightRowEntity
import com.example.speechbuddy.domain.models.WeightRow
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
class WeightRowTest {

    private lateinit var weightRowDao: WeightRowDao
    private lateinit var database: AppDatabase

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        weightRowDao = database.weightRowDao()
    }

    @After
    fun teardown() {
        database.close()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun should_upsert_weightRows_and_get_weightRows_when_fun_upsertAll_and_fun_getAllWeightRows_called() {
        runBlocking {

            val weightRows = listOf(
                WeightRowEntity(
                    id = 1,
                    weights = listOf(1,2,3,4,5,6,7,8,9)
                ),
                WeightRowEntity(
                    id = 2,
                    weights = listOf(1,2,3,4,5,6,7,8,9)
                ),
                WeightRowEntity(
                    id = 3,
                    weights = listOf(1,2,3,4,5,6,7,8,9)
                ),
            )
            weightRowDao.upsertAll(weightRows)

            val weightRowsFlow = weightRowDao.getAllWeightRows().first()
            Assert.assertEquals(weightRows, weightRowsFlow)
        }
    }

    @Test
    fun should_get_weightRows_when_fun_getWeightRowById_called() {
        runBlocking {

            val weightRows = listOf(
                WeightRowEntity(
                    id = 1,
                    weights = listOf(1,2,3,4,5,6,7,8,9)
                ),
                WeightRowEntity(
                    id = 2,
                    weights = listOf(1,2,3,4,5,6,7,8,9,10)
                ),
                WeightRowEntity(
                    id = 3,
                    weights = listOf(1,2,3,4,5,6,7,8,9)
                ),
            )

            weightRowDao.upsertAll(weightRows)

            val queriedCategoriesFlow = weightRowDao.getWeightRowById(2).first()
            Assert.assertEquals(1, queriedCategoriesFlow.size)
            Assert.assertTrue(queriedCategoriesFlow.any {
                it.weights.contains(10)
            })
        }
    }

    @Test
    fun should_update_weightRows_when_fun_updateWeightRow_called() {
        runBlocking {

            val weightRows = listOf(
                WeightRowEntity(
                    id = 1,
                    weights = listOf(1,2,3,4,5,6,7,8,9)
                ),
                WeightRowEntity(
                    id = 2,
                    weights = listOf(1,2,3,4,5,6,7,8,9)
                ),
                WeightRowEntity(
                    id = 3,
                    weights = listOf(1,2,3,4,5,6,7,8,9)
                ),
            )

            weightRowDao.upsertAll(weightRows)

            val weightRowToUpdate = WeightRowEntity(
                id = 2,
                weights = listOf(1,2,3,4,5,6,7,8,9,10)
            )

            weightRowDao.updateWeightRow(weightRowToUpdate)
            val queriedCategoriesFlow = weightRowDao.getWeightRowById(2).first()
            Assert.assertEquals(1, queriedCategoriesFlow.size)
            Assert.assertTrue(queriedCategoriesFlow.any {
                it.weights.contains(10)
            })
        }
    }

}