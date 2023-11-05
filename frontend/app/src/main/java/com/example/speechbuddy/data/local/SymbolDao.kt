package com.example.speechbuddy.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.speechbuddy.data.local.models.SymbolEntity
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for symbols.
 */
@Dao
interface SymbolDao {
    @Query("SELECT * FROM symbols")
    fun getSymbols(): Flow<List<SymbolEntity>>

    @Query("SELECT * FROM symbols WHERE categoryId = :categoryId")
    fun getSymbolsByCategoryId(categoryId: Int): Flow<List<SymbolEntity>>

    @Upsert
    suspend fun upsertAll(symbolEntities: List<SymbolEntity>)
}