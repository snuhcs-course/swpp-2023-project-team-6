package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.UserDao
import com.example.speechbuddy.data.local.models.UserMapper
import com.example.speechbuddy.data.remote.UserRemoteSource
import com.example.speechbuddy.data.remote.models.UserDtoMapper
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.User
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.ResponseHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userMapper: UserMapper,
    private val userDtoMapper: UserDtoMapper,
    private val userRemoteSource: UserRemoteSource,
    private val responseHandler: ResponseHandler,
    private val sessionManager: SessionManager
) {

    fun getMyInfo(): Flow<Resource<User>> {
        return userDao.getUserById(sessionManager.userId.value ?: -1).map { userEntity ->
            if (userEntity != null) Resource.success(userMapper.mapToDomainModel(userEntity))
            else Resource.error("Unable to find user", null)
        }
    }

    suspend fun getMyInfoFromRemote(): Flow<Resource<User>> {
        return userRemoteSource.getMyInfoUser(getAuthHeader()).map { response ->
            if (response.isSuccessful && response.code() == ResponseCode.SUCCESS.value) {
                response.body()?.let { userDto ->
                    val user = userDtoMapper.mapToDomainModel(userDto)
                    val userEntity = userMapper.mapFromDomainModel(user)
                    userDao.insertUser(userEntity)
                    Resource.success(user)
                } ?: returnUnknownError()
            } else {
                response.errorBody()?.let { responseBody ->
                    val errorMsgKey = responseHandler.parseErrorResponse(responseBody).key
                    Resource.error(
                        errorMsgKey, null
                    )
                } ?: returnUnknownError()
            }
        }
    }

    private fun getAuthHeader(): String {
        val accessToken = sessionManager.cachedToken.value?.accessToken
        return "Bearer $accessToken"
    }

    private fun <T> returnUnknownError(): Resource<T> {
        return Resource.error(
            "Unknown error", null
        )
    }

}