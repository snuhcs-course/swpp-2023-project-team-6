package com.example.speechbuddy.service

import com.example.speechbuddy.data.remote.models.AccessTokenDto
import com.example.speechbuddy.data.remote.models.AuthTokenDto
import com.example.speechbuddy.data.remote.requests.AuthLoginRequest
import com.example.speechbuddy.data.remote.requests.AuthLogoutRequest
import com.example.speechbuddy.data.remote.requests.AuthRefreshRequest
import com.example.speechbuddy.data.remote.requests.AuthResetPasswordRequest
import com.example.speechbuddy.data.remote.requests.AuthSendCodeRequest
import com.example.speechbuddy.data.remote.requests.AuthSignupRequest
import com.example.speechbuddy.data.remote.requests.AuthVerifyEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthService {

    @POST("/user/signup/")
    suspend fun signup(
        @Body signupRequest: AuthSignupRequest
    ): Response<Void>

    @POST("/user/login/")
    suspend fun login(
        @Body loginRequest: AuthLoginRequest
    ): Response<AuthTokenDto>

    @POST("/user/validateemail/signup/send/")
    suspend fun sendCodeForSignup(
        @Body sendCodeRequest: AuthSendCodeRequest
    ): Response<Void>

    @POST("/user/validateemail/pw/send/")
    suspend fun sendCodeForResetPassword(
        @Body sendCodeRequest: AuthSendCodeRequest
    ): Response<Void>

    @POST("/user/validateemail/signup/accept/")
    suspend fun verifyEmailForSignup(
        @Body verifyEmailRequest: AuthVerifyEmailRequest
    ): Response<Void>

    @POST("/user/validateemail/pw/accept/")
    suspend fun verifyEmailForResetPassword(
        @Body verifyEmailRequest: AuthVerifyEmailRequest
    ): Response<AccessTokenDto>

    @PATCH("/user/profile/password/")
    suspend fun resetPassword(
        @Body resetPasswordRequest: AuthResetPasswordRequest
    ): Response<Void>

    @POST("/user/refresh/")
    suspend fun refresh(
        @Body refreshRequest: AuthRefreshRequest
    ): Response<AccessTokenDto>

    @POST("/user/logout/")
    suspend fun logout(
        @Header("Authorization") header: String,
        @Body logoutRequest: AuthLogoutRequest
    ): Response<Void>

}