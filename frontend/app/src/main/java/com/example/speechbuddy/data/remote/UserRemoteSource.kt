package com.example.speechbuddy.data.remote

import com.example.speechbuddy.data.remote.models.UserDto
import com.example.speechbuddy.service.UserService
import com.example.speechbuddy.utils.ResponseHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class UserRemoteSource @Inject constructor(
    private val authService: UserService,
    private val responseHandler: ResponseHandler
) {

    suspend fun getMyInfoUser(authHeader: String): Flow<Response<UserDto>> =
        flow {
            try {
                val result = authService.getMyInfo(authHeader)
                emit(result)
            } catch (e: Exception) {
                emit(responseHandler.getConnectionErrorResponse())
            }
        }

}