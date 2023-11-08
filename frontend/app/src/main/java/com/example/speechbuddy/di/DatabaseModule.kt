package com.example.speechbuddy.di

import android.content.Context
import com.example.speechbuddy.data.local.AppDatabase
import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

<<<<<<< HEAD
import com.example.speechbuddy.data.local.AuthTokenPrefsManager

=======
import com.example.speechbuddy.data.local.AppDatabase
import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao

>>>>>>> main
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideSymbolDao(appDatabase: AppDatabase): SymbolDao {
        return appDatabase.symbolDao()
    }

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

}