package com.example.speechbuddy.di

import com.example.speechbuddy.data.remote.models.AccessTokenDtoMapper
import com.example.speechbuddy.data.remote.models.AuthTokenDtoMapper
import com.example.speechbuddy.data.remote.models.MySymbolDtoMapper
import com.example.speechbuddy.data.remote.models.UserDtoMapper
import com.example.speechbuddy.data.remote.requests.AuthRefreshRequest
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.service.AuthService
import com.example.speechbuddy.service.BackupService
import com.example.speechbuddy.service.SymbolCreationService
import com.example.speechbuddy.service.UserService
import com.example.speechbuddy.utils.Constants
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.ResponseHandler
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        authServiceProvider: Provider<AuthService>, sessionManager: SessionManager
    ): Retrofit {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder().addInterceptor(logger)
            .addInterceptor(AuthInterceptor(authServiceProvider, sessionManager)).build()

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
    fun provideBackupService(retrofit: Retrofit): BackupService {
        return retrofit.create(BackupService::class.java)
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
    private val authServiceProvider: Provider<AuthService>,
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val authService = authServiceProvider.get()

        try {
            val builder = chain.request().newBuilder()
            var response = chain.proceed(builder.build())

            // Attempt refresh when FORBIDDEN is returned
            if (response.code == ResponseCode.FORBIDDEN.value) {
                response.close()

                val refreshToken = sessionManager.cachedToken.value?.refreshToken ?: ""
                val authTokenResponse =
                    runBlocking {
                        authService.refresh(AuthRefreshRequest(refreshToken))
                    }
                val newAccessToken = authTokenResponse.body()?.accessToken

                sessionManager.setAuthToken(AuthToken(newAccessToken, refreshToken))

                builder.header("Authorization", "Bearer $newAccessToken")
                response = chain.proceed(builder.build())
            }

            return response
        } catch (e: ConnectException) {
            throw e
        }
    }

}