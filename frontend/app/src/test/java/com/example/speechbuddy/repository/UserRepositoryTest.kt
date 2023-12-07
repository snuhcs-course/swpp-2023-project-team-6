package com.example.speechbuddy.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speechbuddy.data.local.UserDao
import com.example.speechbuddy.data.local.UserIdPrefsManager
import com.example.speechbuddy.data.local.models.UserEntity
import com.example.speechbuddy.data.local.models.UserMapper
import com.example.speechbuddy.data.remote.UserRemoteSource
import com.example.speechbuddy.data.remote.models.UserDto
import com.example.speechbuddy.data.remote.models.UserDtoMapper
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.User
import com.example.speechbuddy.utils.ResponseHandler
import com.example.speechbuddy.utils.Status
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class UserRepositoryTest {

    private lateinit var userRepository: UserRepository

    @MockK
    private val userDao = mockk<UserDao>(relaxed = true)
    private val userMapper = UserMapper()
    private val userDtoMapper = UserDtoMapper()
    private val userRemoteSource = mockk<UserRemoteSource>()
    private val userIdPrefsManager = mockk<UserIdPrefsManager>()
    private val responseHandler = ResponseHandler()
    private val sessionManager = mockk<SessionManager>()

    private val mockUserId = 1
    private val mockUserIdLiveData: LiveData<Int?> = MutableLiveData(mockUserId)
    private val mockEmail = "mockEmail@gmail.com"
    private val mockNickname = "mockNickname"
    private val mockGuestId = -1

    private val mockErrorJson = """
    {
        "error": {
            "code": 000,
            "message": {
                "key of message": [
                    "error description"
                ]
            }
        }
    }
    """
    private val mockErrorResponseBody =
        mockErrorJson.toResponseBody("application/json".toMediaType())

    @Before
    fun setup() {
        coEvery { sessionManager.userId } returns mockUserIdLiveData
        userRepository = UserRepository(
            userDao,
            userMapper,
            userDtoMapper,
            userRemoteSource,
            userIdPrefsManager,
            responseHandler,
            sessionManager
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should return user if found in local database when getMyInfo is called`() = runBlocking {
        val userEntity = UserEntity(
            id = mockUserId,
            email = mockEmail,
            nickname = mockNickname
        )
        val user = User(
            id = mockUserId,
            email = mockEmail,
            nickname = mockNickname
        )

        coEvery { userDao.getUserById(mockUserId) } returns flowOf(userEntity)

        val result = userRepository.getMyInfo()

        result.collect { resource ->
            assert(resource.status == Status.SUCCESS)
            assert(resource.data == user)
            assert(resource.message == null)
        }
    }

    @Test
    fun `should return error resource if user not found in local database when getMyInfo is called`() = runBlocking {
        coEvery { userDao.getUserById(mockUserId) } returns flowOf(null)

        val result = userRepository.getMyInfo()

        result.collect { resource ->
            assert(resource.status == Status.ERROR)
            assert(resource.data == null)
            assert(resource.message == "Unable to find user")
        }
    }

    @Test
    fun `should return success if remote call is successful when getMyInfoFromRemote is called`() = runBlocking {
        val accessToken = "mockAccessToken"
        val userDto = UserDto(
            id = mockUserId,
            email = mockEmail,
            nickname = mockNickname
        )
        val user = User(
            id = mockUserId,
            email = mockEmail,
            nickname = mockNickname
        )
        val userEntity = userMapper.mapFromDomainModel(user)

        val successResponse = Response.success<UserDto>(200, userDto)

        coEvery { userRemoteSource.getMyInfoUser("Bearer $accessToken") } returns flowOf(successResponse)
        coEvery { userIdPrefsManager.saveUserId(user.id) } returns Unit
        coEvery { userDao.insertUser(userEntity) } returns Unit

        // Act
        val result = userRepository.getMyInfoFromRemote(accessToken)

        result.collect { resource ->
            assert(resource.status == Status.SUCCESS)
            assert(resource.data == user)
            assert(resource.message == null)
        }
    }

    @Test
    fun `should return error resource if remote call fails when getMyInfoFromRemote is called`() = runBlocking {
        val accessToken = "mockAccessToken"
        val errorResponse = Response.error<UserDto>(400, mockErrorResponseBody)
        coEvery { userRemoteSource.getMyInfoUser("Bearer $accessToken") } returns flowOf(errorResponse)

        val result = userRepository.getMyInfoFromRemote(accessToken)

        result.collect { resource ->
            assert(resource.status == Status.ERROR)
            assert(resource.data == null)
            assert(resource.message == "key of message")
        }
    }

    @Test
    fun `should set guest mode when setGuestMode is called`() = runBlocking {
        coEvery { userIdPrefsManager.saveUserId(mockGuestId) } returns Unit
        userRepository.setGuestMode()
        coVerify { userIdPrefsManager.saveUserId(mockGuestId) }
    }

    @Test
    fun `should delete user info when deleteUserInfo is called`() = runBlocking{
        coEvery { userDao.deleteUserById(any()) } returns Unit
        coEvery { userIdPrefsManager.clearUserId() } returns Unit

        userRepository.deleteUserInfo()

        coVerify { userDao.deleteUserById(mockUserId) }
        coVerify { userIdPrefsManager.clearUserId() }
    }
}