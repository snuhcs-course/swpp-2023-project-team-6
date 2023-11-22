package com.example.speechbuddy.di

import com.example.speechbuddy.domain.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SessionModule {

    @Singleton
    @Provides
    fun provideSessionManager(): SessionManager {
        return SessionManager()
    }

}