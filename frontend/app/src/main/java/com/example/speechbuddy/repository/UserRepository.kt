package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.UserDao
import com.example.speechbuddy.data.local.UserIdPrefsManager
import com.example.speechbuddy.data.local.models.UserMapper
import com.example.speechbuddy.data.remote.UserRemoteSource
import com.example.speechbuddy.data.remote.models.UserDtoMapper
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.User
import com.example.speechbuddy.utils.Constants.Companion.GUEST_ID
import com.example.speechbuddy.utils.Resource
import com.example.speechbuddy.utils.ResponseCode
import com.example.speechbuddy.utils.ResponseHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userMapper: UserMapper,
    private val userDtoMapper: UserDtoMapper,
    private val userRemoteSource: UserRemoteSource,
    private val userIdPrefsManager: UserIdPrefsManager,
    private val responseHandler: ResponseHandler,
    private val sessionManager: SessionManager
) {

    fun getMyInfo(): Flow<Resource<User>> {
        return userDao.getUserById(sessionManager.userId.value!!).map { userEntity ->
            if (userEntity != null) Resource.success(userMapper.mapToDomainModel(userEntity))
            else Resource.error("Unable to find user", null)
        }
    }

    suspend fun getMyInfoFromRemote(accessToken: String?): Flow<Resource<User>> {
        return userRemoteSource.getMyInfoUser("Bearer $accessToken").map { response ->
            if (response.isSuccessful && response.code() == ResponseCode.SUCCESS.value) {
                response.body()?.let { userDto ->
                    val user = userDtoMapper.mapToDomainModel(userDto)
                    userIdPrefsManager.saveUserId(user.id)
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

    fun setGuestMode() {
        CoroutineScope(Dispatchers.IO).launch {
            userIdPrefsManager.saveUserId(GUEST_ID)
        }
    }

    suspend fun deleteUserInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.deleteUserById(sessionManager.userId.value!!)
            userIdPrefsManager.clearUserId()
        }
    }

    private fun <T> returnUnknownError(): Resource<T> {
        return Resource.error(
            "Unknown error", null
        )
    }

}