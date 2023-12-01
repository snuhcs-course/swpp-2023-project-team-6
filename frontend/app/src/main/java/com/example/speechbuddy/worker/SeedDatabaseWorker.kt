package com.example.speechbuddy.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.speechbuddy.data.local.AppDatabase
import com.example.speechbuddy.data.local.models.CategoryEntity
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.WeightRowEntity
import com.example.speechbuddy.utils.Constants.Companion.CATEGORY_DATA_FILENAME
import com.example.speechbuddy.utils.Constants.Companion.SYMBOL_DATA_FILENAME
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getInstance(applicationContext)



            if (!applicationContext.getDatabasePath("speechbuddy-db").exists()) {
                createWeightTable(database)
            }
            else{ // check if the weight table is filled with 0
                val currentDb = database.weightRowDao().getAllWeightRows().first()
                var cnt = 1
                for (currentWeightRowEntity in currentDb) {
                    if (currentWeightRowEntity.weights.all{it == 0}){
                        cnt+=1 // count the rows with 0 weights
                    }
                }
                if (cnt == currentDb.size){ // if weight table is filled with 0
                    createWeightTable(database)
                }
            }
            

            applicationContext.assets.open(SYMBOL_DATA_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val symbolEntityType = object : TypeToken<List<SymbolEntity>>() {}.type
                    val symbolEntityList: List<SymbolEntity> =
                        Gson().fromJson(jsonReader, symbolEntityType)

                    database.symbolDao().upsertAll(symbolEntityList)

                    Result.success()
                }
            }

            applicationContext.assets.open(CATEGORY_DATA_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val categoryEntityType = object : TypeToken<List<CategoryEntity>>() {}.type
                    val categoryEntityList: List<CategoryEntity> =
                        Gson().fromJson(jsonReader, categoryEntityType)

                    database.categoryDao().upsertAll(categoryEntityList)

                    Result.success()
                }
            }
        } catch (ex: Exception) {
            Log.e("SeedDatabaseWorker", "Error seeding database", ex)
            Result.failure()
        }
    }

    private suspend fun createWeightTable(database: AppDatabase){
        val weightRows = mutableListOf<WeightRowEntity>()

        applicationContext.assets.open("weight_table.txt").use { inputStream ->
            val inputList: MutableList<List<Int>> = ArrayList()
            inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    inputList.add(
                        line.split(",").mapNotNull { it.trim().toIntOrNull() })
                }
            }
            var id = 1
            for (weight in inputList) {
                val weightRowEntity = WeightRowEntity(id++, weight)
                weightRows.add(weightRowEntity)
            }
        }

        database.weightRowDao().upsertAll(weightRows)

        Result.success()
    }

}