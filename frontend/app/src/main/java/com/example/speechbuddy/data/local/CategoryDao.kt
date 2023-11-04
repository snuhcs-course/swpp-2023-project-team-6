package com.example.speechbuddy.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.speechbuddy.data.local.models.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for categories.
 */
@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY id")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsertAll(categories: List<CategoryEntity>)
}