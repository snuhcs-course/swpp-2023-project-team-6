package com.example.speechbuddy.di

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.speechbuddy.data.remote.models.AccessTokenDtoMapper
import com.example.speechbuddy.data.remote.models.AuthTokenDtoMapper
import com.example.speechbuddy.data.remote.models.MySymbolDtoMapper
import com.example.speechbuddy.data.remote.models.UserDtoMapper
import com.example.speechbuddy.data.remote.requests.AuthRefreshRequest
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.service.AuthService
import com.example.speechbuddy.service.SymbolCreationService
import com.example.speechbuddy.service.UserService
import com.example.speechbuddy.utils.Constants
import com.example.speechbuddy.utils.ResponseHandler
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(
        @ApplicationContext context: Context,
        authService: Provider<AuthService>,
        sessionManager: SessionManager
    ): Retrofit {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client =
            OkHttpClient.Builder().addInterceptor(logger)
                .addInterceptor(AuthInterceptor(context, authService, sessionManager))
                .build()

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return Retrofit.Builder().baseUrl(Constants.BASE_URL).client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
    }

    @Singleton
    @Provides
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideUserDtoMapper(): UserDtoMapper {
        return UserDtoMapper()
    }

    @Singleton
    @Provides
    fun provideAuthTokenDtoMapper(): AuthTokenDtoMapper {
        return AuthTokenDtoMapper()
    }

    @Singleton
    @Provides
    fun provideAccessTokenDtoMapper(): AccessTokenDtoMapper {
        return AccessTokenDtoMapper()
    }

    @Singleton
    @Provides
    fun provideResponseHandler(): ResponseHandler {
        return ResponseHandler()
    }

    @Singleton
    @Provides
    fun provideMySymbolDtoMapper(): MySymbolDtoMapper {
        return MySymbolDtoMapper()
    }

    @Singleton
    @Provides
    fun provideSymbolCreationService(retrofit: Retrofit): SymbolCreationService {
        return retrofit.create(SymbolCreationService::class.java)
    }

}

class AuthInterceptor @Inject constructor(
    private val context: Context,
    private var authService: Provider<AuthService>,
    private val sessionManager: SessionManager
) : Interceptor {
//    private val sessionManager: SessionManager = SessionManager()

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable(context)) throw ConnectException()

        try {
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()
            val response = chain.proceed(builder.build())
            response.close() // close request that cause 403

            return if (response.code == 403) {
                val refreshToken = sessionManager.cachedToken.value!!.refreshToken!!
                val responseMadeWithRefreshToken =
                    runBlocking { authService.get().refresh(AuthRefreshRequest(refreshToken)) }
                val newAuthToken = responseMadeWithRefreshToken.body()?.accessToken

                sessionManager.setAuthToken(AuthToken(newAuthToken, refreshToken))

                builder.header("Authorization", "Bearer $newAuthToken")
                chain.proceed(builder.build())
            } else {
                chain.proceed(builder.build())
            }
        } catch (e: ConnectException) {
            throw e
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(
            NetworkCapabilities.TRANSPORT_ETHERNET
        ) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

}
