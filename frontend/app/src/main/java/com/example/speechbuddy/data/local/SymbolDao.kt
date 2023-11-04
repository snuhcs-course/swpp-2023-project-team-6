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
    @Query("SELECT * FROM symbols ORDER BY id")
    fun getSymbols(): Flow<List<SymbolEntity>>

    @Upsert
    suspend fun upsertAll(symbolEntities: List<SymbolEntity>)
}