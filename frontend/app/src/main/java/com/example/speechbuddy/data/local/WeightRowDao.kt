package com.example.speechbuddy.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.speechbuddy.data.local.models.WeightRowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightRowDao {
    @Query("SELECT * FROM weighttable")
    fun getAllWeightRows(): Flow<List<WeightRowEntity>>

    @Query("SELECT * FROM weighttable WHERE id = :rowId")
    fun getWeightRowById(rowId: Int): Flow<List<WeightRowEntity>>

    @Update
    fun updateWeightRow(weightRowEntity: WeightRowEntity)

    @Upsert
    suspend fun upsertAll(weightRowEntities: List<WeightRowEntity>)

    @Query("DELETE FROM weighttable WHERE id > 500")
    suspend fun deleteMySymbolsWeightRows()

    @Query("UPDATE weighttable SET weights = :resetWeights WHERE id < 501")
    suspend fun resetOriginalSymbolsWeightRows(resetWeights: List<Int>)
}
