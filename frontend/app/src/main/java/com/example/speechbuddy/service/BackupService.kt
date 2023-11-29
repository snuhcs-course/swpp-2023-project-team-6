package com.example.speechbuddy.service

import com.example.speechbuddy.data.remote.models.FavoritesListDto
import com.example.speechbuddy.data.remote.models.SettingsBackupDto
import com.example.speechbuddy.data.remote.models.SymbolListDto
import com.example.speechbuddy.data.remote.requests.BackupWeightTableRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface BackupService {

    @POST("/setting/backup/")
    suspend fun displayBackup(
        @Header("Authorization") header: String,
        @Body settingsBackupDto: SettingsBackupDto
    ): Response<Void>

    @POST("/symbol/enable/")
    suspend fun symbolListBackup(
        @Query("id") id: String? = null,
        @Header("Authorization") header: String
    ): Response<Void>

    @POST("/symbol/favorite/backup/")
    suspend fun favoriteSymbolBackup(
        @Query("id") id: String? = null,
        @Header("Authorization") header: String
    ): Response<Void>

    @POST("/weight/backup/")
    suspend fun weightTableBackup(
        @Header("Authorization") header: String,
        @Body backupWeightTableRequest: BackupWeightTableRequest
    ): Response<Void>

    @GET("/setting/backup/")
    suspend fun getDisplaySettings(
        @Header("Authorization") header: String
    ): Response<SettingsBackupDto>

    @GET("/symbol/")
    suspend fun getSymbolList(
        @Header("Authorization") header: String
    ): Response<SymbolListDto>

    @GET("/symbol/favorite/backup/")
    suspend fun getFavoriteSymbolList(
        @Header("Authorization") header: String
    ): Response<FavoritesListDto>

    @GET("/weight/backup/")
    suspend fun getWeightTable(
        @Header("Authorization") header: String
    ): Response<BackupWeightTableRequest>

    @GET
    suspend fun getImage(
        @Url imgUrl: String
    ): ResponseBody
}