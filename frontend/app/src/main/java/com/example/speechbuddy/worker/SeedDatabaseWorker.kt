package com.example.speechbuddy.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.speechbuddy.data.local.AppDatabase
import com.example.speechbuddy.data.local.models.Category
import com.example.speechbuddy.data.local.models.Symbol
import com.example.speechbuddy.utils.Constants.Companion.CATEGORY_DATA_FILENAME
import com.example.speechbuddy.utils.Constants.Companion.SYMBOL_DATA_FILENAME
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getInstance(applicationContext)

            applicationContext.assets.open(SYMBOL_DATA_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val symbolType = object : TypeToken<List<Symbol>>() {}.type
                    val symbolList: List<Symbol> = Gson().fromJson(jsonReader, symbolType)

                    database.symbolDao().upsertAll(symbolList)

                    Result.success()
                }
            }
            applicationContext.assets.open(CATEGORY_DATA_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val categoryType = object : TypeToken<List<Category>>() {}.type
                    val categoryList: List<Category> = Gson().fromJson(jsonReader, categoryType)

                    database.categoryDao().upsertAll(categoryList)

                    Result.success()
                }
            }
        } catch (ex: Exception) {
            Log.e("SeedDatabaseWorker", "Error seeding database", ex)
            Result.failure()
        }
    }

}
