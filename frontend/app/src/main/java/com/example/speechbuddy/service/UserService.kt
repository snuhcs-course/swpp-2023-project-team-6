package com.example.speechbuddy.service

import com.example.speechbuddy.data.TokenResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {

    @POST("/user/login")
    suspend fun login(
        @Query("email") email: String, @Query("password") password: String
    ): TokenResponse

}