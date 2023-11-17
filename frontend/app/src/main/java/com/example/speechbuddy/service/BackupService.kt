package com.example.speechbuddy.service

import com.example.speechbuddy.data.remote.models.SettingsBackupDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface BackupService {

    @POST("/setting/backup/")
    suspend fun displayBackup(
        @Header("Authorization") header: String,
        @Body settingsBackupDto: SettingsBackupDto
    ): Response<Void>

    @GET("/setting/backup/")
    suspend fun getSettings(
        @Header("Authorization") header: String
    ): Response<SettingsBackupDto>

}