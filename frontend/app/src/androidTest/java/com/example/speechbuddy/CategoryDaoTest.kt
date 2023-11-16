package com.example.speechbuddy

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.speechbuddy.data.local.AppDatabase
import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.models.CategoryEntity
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var categoryDao: CategoryDao
    private lateinit var database: AppDatabase

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        categoryDao = database.categoryDao()
    }

    @After
    fun teardown() {
        database.close()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun should_upsert_categories_when_fun_upsertAll_called() {
        runBlocking {
            val categories = listOf(
                CategoryEntity(1, "Category 1"),
                CategoryEntity(2, "Category 2"),
                CategoryEntity(3, "Category 3"),
                CategoryEntity(4, "Category 4"),
                CategoryEntity(5, "Category 5")
            )

            categoryDao.upsertAll(categories)

            val categoriesFlow = categoryDao.getCategories().first()
            assertEquals(categories, categoriesFlow)
        }
    }

    @Test
    fun should_get_categories_when_fun_getCategories_called() {
        runBlocking {
            val categories = listOf(
                CategoryEntity(1, "Category 1"),
                CategoryEntity(2, "Category 2"),
                CategoryEntity(3, "Category 3")
            )

            categoryDao.upsertAll(categories)

            val categoriesFlow = categoryDao.getCategories().first()
            assertEquals(categories, categoriesFlow)
        }
    }

    @Test
    fun should_get_categories_when_fun_getCategoriesByQuery_called() {
        runBlocking {
            val categories = listOf(
                CategoryEntity(1, "Category 1"),
                CategoryEntity(2, "Category 2"),
                CategoryEntity(3, "Another")
            )

            categoryDao.upsertAll(categories)

            val queriedCategoriesFlow = categoryDao.getCategoriesByQuery("Category").first()
            assertEquals(2, queriedCategoriesFlow.size)
            assertTrue(queriedCategoriesFlow.any {
                it.text.contains(
                    "Category",
                    ignoreCase = true
                )
            })
        }
    }
}