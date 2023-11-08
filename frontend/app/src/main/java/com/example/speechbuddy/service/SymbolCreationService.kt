package com.example.speechbuddy.service

import com.example.speechbuddy.data.remote.models.SymbolIdDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SymbolCreationService {
    @Multipart
    @POST("/symbol/backup/")
    suspend fun createSymbolBackup(
        @Part("text") symbolText: String,
        @Part("category") categoryId: Int,
        @Part("image") image: MultipartBody.Part
    ): Response<SymbolIdDto>
}
