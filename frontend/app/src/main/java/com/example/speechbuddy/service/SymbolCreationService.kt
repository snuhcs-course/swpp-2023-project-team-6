package com.example.speechbuddy.service

import com.example.speechbuddy.data.remote.models.MySymbolDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SymbolCreationService {
    @Multipart
    @POST("/symbol/backup/")
    suspend fun createSymbolBackup(
        @Header("Authorization") header: String,
        @Part("text") symbolText: RequestBody,
        @Part("category") categoryId: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<MySymbolDto>
}