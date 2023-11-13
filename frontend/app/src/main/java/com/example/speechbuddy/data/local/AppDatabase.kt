package com.example.speechbuddy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.speechbuddy.data.local.models.CategoryEntity
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.WeightRowEntity
import com.example.speechbuddy.domain.models.WeightRow
import com.example.speechbuddy.domain.utils.Converters
import com.example.speechbuddy.utils.Constants.Companion.DATABASE_NAME
import com.example.speechbuddy.worker.SeedDatabaseWorker
import java.util.concurrent.TimeUnit

/**
 * Room Database for SpeechBuddy App
 */
@Database(entities = [SymbolEntity::class, CategoryEntity::class, WeightRowEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun symbolDao(): SymbolDao
    abstract fun categoryDao(): CategoryDao
    abstract fun weightRowDao(): WeightRowDao

    companion object {
        // For Singleton Instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                //.fallbackToDestructiveMigration() // activate this line and delete app from device when data migration error occurs
                .addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            //val request = PeriodicWorkRequestBuilder<SeedDatabaseWorker>(1, TimeUnit.MINUTES).build()
                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                            WorkManager.getInstance(context).enqueue(request)
                        }
                    }
                )
                .build()
        }
    }
}