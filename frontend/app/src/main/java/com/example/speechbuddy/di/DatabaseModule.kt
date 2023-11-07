package com.example.speechbuddy.di

import android.content.Context
import com.example.speechbuddy.data.local.AuthTokenPrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAuthTokenPrefsManager(@ApplicationContext context: Context): AuthTokenPrefsManager {
        return AuthTokenPrefsManager(context)
    }

}