package com.example.speechbuddy.di

import android.content.Context
import com.example.speechbuddy.data.local.WeightTableImpl
import com.example.speechbuddy.data.local.WeigthTableOperations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeightTableModule {

    @Singleton
    @Provides
    fun provideWeightTableOperations(@ApplicationContext context: Context): WeigthTableOperations {
        return WeightTableImpl(context)
    }
}