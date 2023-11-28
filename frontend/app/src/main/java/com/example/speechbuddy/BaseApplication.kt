package com.example.speechbuddy

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.speechbuddy.worker.SeedDatabaseWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        // create DB at the start of the app
        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
        WorkManager.getInstance(this).enqueue(request)
    }
}