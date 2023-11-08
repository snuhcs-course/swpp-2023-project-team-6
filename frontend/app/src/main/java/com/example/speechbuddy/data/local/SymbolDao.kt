package com.example.speechbuddy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT * FROM symbols WHERE isFavorite = 1")
    fun getFavoriteSymbols(): Flow<List<SymbolEntity>>

    @Query("SELECT * FROM symbols WHERE text LIKE  '%' || :query || '%'")
    fun getSymbolsByQuery(query: String): Flow<List<SymbolEntity>>

    @Query("SELECT * FROM symbols WHERE isFavorite = 1 AND text LIKE '%' || :query || '%'")
    fun getFavoriteSymbolsByQuery(query: String): Flow<List<SymbolEntity>>

    @Query("SELECT * FROM symbols WHERE categoryId = :categoryId")
    fun getSymbolsByCategoryId(categoryId: Int): Flow<List<SymbolEntity>>

    @Update
    suspend fun updateSymbol(symbolEntity: SymbolEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSymbol(symbolEntity: SymbolEntity)

    @Upsert
    suspend fun upsertAll(symbolEntities: List<SymbolEntity>)
}