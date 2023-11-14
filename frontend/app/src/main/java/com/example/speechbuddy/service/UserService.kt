package com.example.speechbuddy.service

import com.example.speechbuddy.data.remote.models.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface UserService {

    @GET("/user/profile/")
    suspend fun getMyInfo(
        @Header("Authorization") header: String
    ): Response<UserDto>

}