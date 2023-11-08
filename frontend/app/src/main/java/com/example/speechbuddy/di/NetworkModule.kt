package com.example.speechbuddy.di

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.speechbuddy.MainApplication
import com.example.speechbuddy.data.remote.models.AuthTokenDtoMapper
import com.example.speechbuddy.data.remote.models.ErrorResponseMapper
import com.example.speechbuddy.data.remote.models.SymbolIdDtoMapper
import com.example.speechbuddy.service.AuthService
import com.example.speechbuddy.service.SymbolCreationService
import com.example.speechbuddy.utils.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.net.ConnectException
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client =
            OkHttpClient.Builder().addInterceptor(logger).addInterceptor(AuthInterceptor(context)).build()

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

    @Singleton
    @Provides
    fun provideErrorResponseMapper(): ErrorResponseMapper {
        return ErrorResponseMapper()
    }

    @Singleton
    @Provides
    fun provideSymbolIdDtoMapper(): SymbolIdDtoMapper {
        return SymbolIdDtoMapper()
    }

    @Singleton
    @Provides
    fun provideSymbolCreationService(retrofit: Retrofit): SymbolCreationService {
        return retrofit.create(SymbolCreationService::class.java)
    }

}

class AuthInterceptor(private val context: Context): Interceptor {

    private val requestsWithoutAuth = listOf("signup", "login", "refresh", "validateemail")

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable(context)) {
            throw NotConnectException
        }

        val builder = chain.request().newBuilder()
        val accessToken = MainApplication.token_prefs.getAccessToken()
        if (accessToken != null && requiresAuth(chain.request())) {
            builder.addHeader("Authorization", "Bearer $accessToken")
        }
        try {
            return chain.proceed(builder.build())
        } catch (e: ConnectException) {
            throw NotConnectException
        }
    }

    private fun requiresAuth(request: Request): Boolean {
        return !requestsWithoutAuth.any { request.url.toString().contains(it) }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return result
    }

}

object NotConnectException : IOException() {
    override val message: String
        get() = "네트워크 연결이 원활하지 않습니다."
}