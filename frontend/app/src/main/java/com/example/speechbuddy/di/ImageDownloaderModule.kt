package com.example.speechbuddy.di

import android.content.Context
import com.example.speechbuddy.data.remote.ImageDownloader
import com.example.speechbuddy.data.remote.ProxyImageDownloader
import com.example.speechbuddy.data.remote.RealImageDownloader
import com.example.speechbuddy.service.BackupService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ImageDownloaderModule {
    @Singleton
    @Provides
    fun provideRealImageDownloader(
        @ApplicationContext context: Context,
        backupService: BackupService
    ): RealImageDownloader {
        return RealImageDownloader(backupService, context)
    }

    @Singleton
    @Provides
    fun provideProxyImageDownloader(
        @ApplicationContext context: Context,
        realImageDownloader: RealImageDownloader
    ): ProxyImageDownloader {
        return ProxyImageDownloader(realImageDownloader, context)
    }

    @Singleton
    @Provides
    fun provideImageDownloader(
        @ApplicationContext context: Context, realImageDownloader: RealImageDownloader
    ): ImageDownloader {
        return ProxyImageDownloader(realImageDownloader, context)
    }
}