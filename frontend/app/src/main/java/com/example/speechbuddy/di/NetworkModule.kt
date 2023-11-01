package com.example.speechbuddy.di

import com.example.speechbuddy.MainApplication
import com.example.speechbuddy.data.remote.models.AuthTokenDtoMapper
import com.example.speechbuddy.service.AuthService
import com.example.speechbuddy.utils.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client =
            OkHttpClient.Builder().addInterceptor(logger).addInterceptor(AuthInterceptor()).build()

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return Retrofit.Builder().baseUrl(Constants.BASE_URL).client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
    }

    @Singleton
    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthTokenDtoMapper(): AuthTokenDtoMapper {
        return AuthTokenDtoMapper()
    }

}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val auth = MainApplication.token_prefs.getAccessToken()
        if (auth != null && shouldAddAuthToken(chain.request())) { // If it requires Auth token
            builder.addHeader("Authorization", "Bearer $auth")
        }
        return chain.proceed(builder.build())
    }

    private fun shouldAddAuthToken(request: Request): Boolean {
        // List of url to exclude
        val substrings = listOf("signup", "validateemail", "login", "refresh")
        val containSubstrings = substrings.any { request.url.toString().contains(it) }
        return !containSubstrings
    }
}