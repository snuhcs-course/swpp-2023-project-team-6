package com.example.speechbuddy.di

import android.content.Context
import com.example.speechbuddy.data.local.AppDatabase
import com.example.speechbuddy.data.local.AuthTokenPrefsManager
import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.UserDao
import com.example.speechbuddy.data.local.models.CategoryMapper
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.data.local.models.UserMapper
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

    @Singleton
    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Singleton
    @Provides
    fun provideSymbolDao(appDatabase: AppDatabase): SymbolDao {
        return appDatabase.symbolDao()
    }

    @Singleton
    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Singleton
    @Provides
    fun provideUserMapper(): UserMapper {
        return UserMapper()
    }

    @Singleton
    @Provides
    fun provideSymbolMapper(): SymbolMapper {
        return SymbolMapper()
    }

    @Singleton
    @Provides
    fun provideCategoryMapper(): CategoryMapper {
        return CategoryMapper()
    }

    @Singleton
    @Provides
    fun provideAuthTokenPrefsManager(@ApplicationContext context: Context): AuthTokenPrefsManager {
        return AuthTokenPrefsManager(context)
    }

}