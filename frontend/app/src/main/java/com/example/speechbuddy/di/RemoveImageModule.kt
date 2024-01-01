package com.example.speechbuddy.di

import android.content.Context
import com.example.speechbuddy.data.remote.ImageDownloader
import com.example.speechbuddy.data.remote.ProxyImageDownloader
import com.example.speechbuddy.data.remote.RealImageDownloader
import com.example.speechbuddy.data.remote.RemoveImage
import com.example.speechbuddy.service.BackupService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RemoveImageModule {
    @Singleton
    @Provides
    fun provideRemoveImage(
        @ApplicationContext context: Context,
    ): RemoveImage {
        return RemoveImage(context)
    }
}