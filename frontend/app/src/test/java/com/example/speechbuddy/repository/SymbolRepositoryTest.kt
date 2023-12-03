package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.models.CategoryEntity
import com.example.speechbuddy.data.local.models.CategoryMapper
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.data.remote.MySymbolRemoteSource
import com.example.speechbuddy.data.remote.ProxyImageDownloader
import com.example.speechbuddy.data.remote.models.MySymbolDto
import com.example.speechbuddy.data.remote.models.MySymbolDtoMapper
import com.example.speechbuddy.domain.SessionManager
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Entry
import com.example.speechbuddy.domain.models.MySymbol
import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.utils.ResponseHandler
import com.example.speechbuddy.utils.Status
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SymbolRepositoryTest {
    private lateinit var symbolDao: SymbolDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var symbolMapper: SymbolMapper
    private lateinit var categoryMapper: CategoryMapper
    private lateinit var symbolRepository: SymbolRepository
    private lateinit var mockMySymbolRemoteSource: MySymbolRemoteSource
    private lateinit var mockMySymbolDtoMapper: MySymbolDtoMapper
    private lateinit var mockResponseHandler: ResponseHandler
    private lateinit var mockSessionManager: SessionManager
    private lateinit var mockProxyImageDownloader: ProxyImageDownloader
    private lateinit var mySymbolRemoteSource: MySymbolRemoteSource
    private val mockAccessToken = "testAccessToken"
    private val mockAuthHeader = "Bearer $mockAccessToken"

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

    private val categoryTypes = listOf(
        "가족",
        "계절",
        "교통",
        "기념일/일정",
        "기분감정",
        "놀이",
        "동물",
        "동작",
        "몸",
        "물음",
        "방향",
        "사람",
        "시간",
        "옷",
        "요일",
        "음식",
        "인사사회어",
        "자연",
        "장소",
        "직업",
        "집",
        "학교",
        "형용사",
        "기타"
    )


    @Before
    fun setUp() {
        symbolDao = mockk(relaxed = true)
        categoryDao = mockk(relaxed = true)
        symbolMapper = SymbolMapper()
        categoryMapper = CategoryMapper()
        mockMySymbolDtoMapper = MySymbolDtoMapper()
        mockMySymbolRemoteSource = mockk(relaxed = true)
        mockResponseHandler = ResponseHandler()
        mockSessionManager = mockk(relaxed = true)
        mockProxyImageDownloader = mockk(relaxed = true)
        mySymbolRemoteSource = mockk(relaxed = true)

        // create symbolEntities
        val symbolEntities = mutableListOf<SymbolEntity>()
        for (i in 1..500) {
            val tmpObj = SymbolEntity(
                id = i,
                text = "symbol$i",
                imageUrl = null,
                categoryId = 1,
                isFavorite = false,
                isMine = false
            )
            symbolEntities.add(tmpObj)
        }
        coEvery { symbolDao.getSymbols() } returns flowOf(
            symbolEntities.toList()
        )

        // create Categories Entity
        var cnt = 1
        val categoryEntities = mutableListOf<CategoryEntity>()
        for (category in categoryTypes) {
            categoryEntities.add(CategoryEntity(cnt++, category))
        }
        coEvery { categoryDao.getCategories() } returns flowOf(
            categoryEntities.toList()
        )

        symbolRepository = SymbolRepository(
            symbolDao,
            categoryDao,
            mockMySymbolRemoteSource,
            mockMySymbolDtoMapper,
            mockResponseHandler,
            mockSessionManager,
            symbolMapper,
            categoryMapper,
            mockProxyImageDownloader
        )
    }

    @Test
    fun `should return Symbol when query is inputted`() = runBlocking {
        val mockQuery = "119에 전화해주세요"

        val symbol = Symbol(
            id = 1,
            text = "119에 전화해주세요",
            imageUrl = null,
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )

        val symbolEntity = SymbolEntity(
            id = 1,
            text = "119에 전화해주세요",
            imageUrl = null,
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )

        coEvery { symbolDao.getSymbolsByQuery(mockQuery) } returns flowOf(listOf(symbolEntity))

        val result = symbolRepository.getSymbols(mockQuery).first()[0]

        assertEquals(symbol, result)
    }

    @Test
    fun `should return list of Symbols when nothing is inputted`() = runBlocking {
        val symbolList = mutableListOf<Symbol>()

        // create symbolList
        for (i in 1..500) {
            val tmpObj = Symbol(
                id = i,
                text = "symbol$i",
                imageUrl = null,
                categoryId = 1,
                isFavorite = false,
                isMine = false
            )
            symbolList.add(tmpObj)
        }

        val result = symbolRepository.getSymbols("").first()

        assertEquals(symbolList.toList(), result)
    }

    @Test
    fun `should return Category when query is inputted`() = runBlocking {
        val mockQuery = "가족"

        val category = Category(1, "가족")

        val categoryEntity = CategoryEntity(1, "가족")

        coEvery { categoryDao.getCategoriesByQuery(mockQuery) } returns flowOf(listOf(categoryEntity))

        val result = symbolRepository.getCategories(mockQuery).first()[0]

        assertEquals(category, result)
    }

    @Test
    fun `should return list of Categories when nothing is inputted`() = runBlocking {
        val categoryList = mutableListOf<Category>()
        var cnt = 1
        for (category in categoryTypes) {
            categoryList.add(Category(cnt++, category))
        }

        val result = symbolRepository.getCategories("").first()

        assertEquals(categoryList, result)
    }

    @Test
    fun `should return list of Entries when nothing is inputted`() = runBlocking {
        val categoryList = mutableListOf<Category>()
        var cnt = 1
        for (category in categoryTypes) {
            categoryList.add(Category(cnt++, category))
        }

        val symbolList = mutableListOf<Symbol>()
        // create symbolList
        for (i in 1..500) {
            val tmpObj = Symbol(
                id = i,
                text = "symbol$i",
                imageUrl = null,
                categoryId = 1,
                isFavorite = false,
                isMine = false
            )
            symbolList.add(tmpObj)
        }


        val entryList = mutableListOf<Entry>()
        entryList.addAll(categoryList)
        entryList.addAll(symbolList)

        val result = symbolRepository.getEntries("").first()

        assertEquals(entryList, result)
    }

    @Test
    fun `should return list of Entries when query(symbol) is inputted`() = runBlocking {
        val mockQuery = "가다"

        val entryList = mutableListOf<Entry>()
        entryList.addAll(listOf(Symbol(3, "가다", null, 1, isFavorite = false, isMine = false)))

        coEvery { symbolDao.getSymbolsByQuery(mockQuery) } returns flowOf(
            listOf(
                SymbolEntity(
                    3,
                    "가다",
                    null,
                    1,
                    isFavorite = false,
                    isMine = false
                )
            )
        )
        coEvery { categoryDao.getCategoriesByQuery(mockQuery) } returns flowOf(listOf())

        val result = symbolRepository.getEntries(mockQuery).first()

        assertEquals(entryList, result)
    }

    @Test
    fun `should return list of Entries when query(category) is inputted`() = runBlocking {
        val mockQuery = "기념일/일정"

        val entryList = mutableListOf<Entry>()
        entryList.addAll(listOf(Category(4, "기념일/일정")))

        coEvery { symbolDao.getSymbolsByQuery(mockQuery) } returns flowOf(listOf())
        coEvery { categoryDao.getCategoriesByQuery(mockQuery) } returns flowOf(
            listOf(
                CategoryEntity(
                    4,
                    "기념일/일정"
                )
            )
        )

        val result = symbolRepository.getEntries(mockQuery).first()

        assertEquals(entryList, result)
    }

    @Test
    fun `should return list of Entries when query(symbol&category) is inputted`() = runBlocking {
        val mockQuery = "가족"

        val entryList = mutableListOf<Entry>()
        entryList.addAll(listOf(Category(1, "가족")))
        entryList.addAll(listOf(Symbol(11, "가족", null, 1, isFavorite = false, isMine = false)))

        coEvery { symbolDao.getSymbolsByQuery(mockQuery) } returns flowOf(
            listOf(
                SymbolEntity(
                    11,
                    "가족",
                    null,
                    1,
                    isFavorite = false,
                    isMine = false
                )
            )
        )
        coEvery { categoryDao.getCategoriesByQuery(mockQuery) } returns flowOf(
            listOf(
                CategoryEntity(
                    1,
                    "가족"
                )
            )
        )

        val result = symbolRepository.getEntries(mockQuery).first()

        assertEquals(entryList, result)
    }

    @Test
    fun `should return list of Entries when query(no match) is inputted`() = runBlocking {
        val mockQuery = "가족"

        val entryList = mutableListOf<Entry>()

        coEvery { symbolDao.getSymbolsByQuery(mockQuery) } returns flowOf(listOf())
        coEvery { categoryDao.getCategoriesByQuery(mockQuery) } returns flowOf(listOf())

        val result = symbolRepository.getEntries(mockQuery).first()

        assertEquals(entryList, result)
    }

    @Test
    fun `should return list of Favorite Symbols when query(exists) is inputted`() = runBlocking {
        val mockQuery = "119에 전화해주세요"

        // create symbolEntities
        val favSymbolEntity = SymbolEntity(
            id = 1,
            text = "119에 전화해주세요",
            imageUrl = null,
            categoryId = 1,
            isFavorite = true,
            isMine = false
        )

        val favSymbol = Symbol(
            id = 1,
            text = "119에 전화해주세요",
            imageUrl = null,
            categoryId = 1,
            isFavorite = true,
            isMine = false
        )

        coEvery { symbolDao.getFavoriteSymbolsByQuery(mockQuery) } returns flowOf(
            listOf(
                favSymbolEntity
            )
        )

        val result = symbolRepository.getFavoriteSymbols(mockQuery).first()

        assertEquals(listOf(favSymbol), result)
    }

    @Test
    fun `should return list of Favorite Symbols when query(no match) is inputted`() =
        runBlocking {
            val mockQuery = "123"

            coEvery { symbolDao.getFavoriteSymbolsByQuery(mockQuery) } returns flowOf(listOf())

            val result = symbolRepository.getFavoriteSymbols(mockQuery).first()

            assertEquals(listOf<Symbol>(), result)
        }

    @Test
    fun `should return list of Favorite Symbols when nothing is inputted`() = runBlocking {
        // create symbolEntities
        val symbolEntities = mutableListOf<SymbolEntity>()
        symbolEntities.add(
            SymbolEntity(
                id = 1,
                text = "symbol1",
                imageUrl = null,
                categoryId = 1,
                isFavorite = true,
                isMine = false
            )
        )
        for (i in 2..500) {
            val tmpObj = SymbolEntity(
                id = 1,
                text = "symbol$i",
                imageUrl = null,
                categoryId = i,
                isFavorite = false,
                isMine = false
            )
            symbolEntities.add(tmpObj)
        }

        // create expected result
        val expectedResult = mutableListOf<Symbol>()
        expectedResult.add(
            Symbol(
                id = 1,
                text = "symbol1",
                imageUrl = null,
                categoryId = 1,
                isFavorite = true,
                isMine = false
            )
        )
        for (i in 2..500) {
            val tmpObj = Symbol(
                id = 1,
                text = "symbol$i",
                imageUrl = null,
                categoryId = i,
                isFavorite = false,
                isMine = false
            )
            expectedResult.add(tmpObj)
        }

        coEvery { symbolDao.getFavoriteSymbols() } returns flowOf(symbolEntities)

        val result = symbolRepository.getFavoriteSymbols("").first()

        assertEquals(expectedResult, result)
    }

    @Test
    fun `should return a list of all user-created symbol when nothing is inputted`() = runBlocking {
        // create symbolEntities
        val symbolEntities = mutableListOf<SymbolEntity>()
        for (i in 501..510) {
            val tmpObj = SymbolEntity(
                id = i,
                text = "symbol$i",
                imageUrl = null,
                categoryId = i,
                isFavorite = false,
                isMine = true
            )
            symbolEntities.add(tmpObj)
        }

        val expectedResult = mutableListOf<Symbol>()
        for (i in 501..510) {
            val tmpObj = Symbol(
                id = i,
                text = "symbol$i",
                imageUrl = null,
                categoryId = i,
                isFavorite = false,
                isMine = true
            )
            expectedResult.add(tmpObj)
        }
        coEvery { symbolDao.getUserSymbols() } returns flowOf(symbolEntities)
        val result = symbolRepository.getUserSymbols("").first()
        assertEquals(expectedResult, result)
    }

    @Test
    fun `should return a list of all user-created symbol when query(match) is inputted`() = runBlocking {
        val mockQuery = "피아노"

        val mySymbolEntity = SymbolEntity(
                id = 501,
                text = "피아노",
                imageUrl = null,
                categoryId = 1,
                isFavorite = false,
                isMine = true
            )

        val expectedResult = Symbol(
                id = 501,
                text = "피아노",
                imageUrl = null,
                categoryId = 1,
                isFavorite = false,
                isMine = true
            )

        coEvery { symbolDao.getUserSymbolsByQuery(mockQuery) } returns flowOf(
            listOf(
                mySymbolEntity
            )
        )

        val result = symbolRepository.getUserSymbols(mockQuery).first()
        assertEquals(listOf(expectedResult), result)
    }

    @Test
    fun `should return nothing for getUserSymbols when query(no match) is inputted`() =
        runBlocking {
            val mockQuery = "123"

            coEvery { symbolDao.getUserSymbolsByQuery(mockQuery) } returns flowOf(listOf())

            val result = symbolRepository.getUserSymbols(mockQuery).first()

            assertEquals(listOf<Symbol>(), result)
        }

    @Test
    fun `should return list of all categories for getAllCategories`() = runBlocking {
        val categoryList = mutableListOf<Category>()
        var cnt = 1
        for (category in categoryTypes) {
            categoryList.add(Category(cnt++, category))
        }

        val result = symbolRepository.getAllCategories().first()

        assertEquals(categoryList, result)
    }

    @Test
    fun `should return a symbol by ID when id(exists) is inputted`() = runBlocking {
        val mockId = 1
        val symbolEntity = SymbolEntity(
            id = 1,
            text = "119에 전화해주세요",
            imageUrl = null,
            categoryId = 1,
            isFavorite = true,
            isMine = false
        )

        val symbol = Symbol(
            id = 1,
            text = "119에 전화해주세요",
            imageUrl = null,
            categoryId = 1,
            isFavorite = true,
            isMine = false
        )

        coEvery { symbolDao.getSymbolById(mockId) } returns flowOf(
            symbolEntity
        )

        val result = symbolRepository.getSymbolsById(mockId)

        assertEquals(symbol, result)
    }

    @Test
    fun `should return list of Symbols by Category when query is inputted`() = runBlocking {
        val category = Category(1, "가족")
        // create symbolEntity
        val symbolEntity = SymbolEntity(
            id = 1,
            text = "symbol1",
            imageUrl = null,
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )

        val symbol = Symbol(
            id = 1,
            text = "symbol1",
            imageUrl = null,
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )
        coEvery { symbolDao.getSymbolsByCategoryId(category.id) } returns flowOf(listOf(symbolEntity))

        val result = symbolRepository.getSymbolsByCategory(category).first()

        assertEquals(listOf(symbol), result)
    }

    @Test
    fun `should return list of symbol ids for getUserSymbolsIdString`() = runBlocking {
        val mySymbolId = mutableListOf<Int>()
        for (i in 501..510) {
            mySymbolId.add(i)
        }
        val expectedString = mySymbolId.joinToString(separator = ",")
        coEvery { symbolDao.getUserSymbolsId() } returns flowOf(mySymbolId)
        val result = symbolRepository.getUserSymbolsIdString()
        assertEquals(expectedString, result)
    }

    @Test
    fun `should return empty string for getUserSymbolsIdString when there's no user-created symbols`() = runBlocking {
        val expectedString = ""
        coEvery { symbolDao.getUserSymbolsId() } returns flowOf(emptyList())
        val result = symbolRepository.getUserSymbolsIdString()
        assertEquals(expectedString, result)
    }

    @Test
    fun `should return list of favorite symbol ids for getFavoriteSymbolsIdString`() = runBlocking {
        val myFavSymbolId = mutableListOf<Int>()
        for (i in 1..10) {
            myFavSymbolId.add(i)
        }
        val expectedString = myFavSymbolId.joinToString(separator = ",")
        coEvery { symbolDao.getFavoriteSymbolsId() } returns flowOf(myFavSymbolId)
        val result = symbolRepository.getFavoriteSymbolsIdString()
        assertEquals(expectedString, result)
    }

    @Test
    fun `should return empty string for getFavoriteSymbolsIdString when there's no favorite symbols`() = runBlocking {
        val expectedString = ""
        coEvery { symbolDao.getFavoriteSymbolsId() } returns flowOf(emptyList())
        val result = symbolRepository.getFavoriteSymbolsIdString()
        assertEquals(expectedString, result)
    }

    @Test
    fun `should update Favorite status when symbol and true is inputted`() = runBlocking {
        val symbol = Symbol(
            id = 1,
            text = "symbol1",
            imageUrl = null,
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )
        val expectedSymbolEntity = SymbolEntity(
            id = symbol.id,
            text = symbol.text,
            imageUrl = symbol.imageUrl,
            categoryId = symbol.categoryId,
            isFavorite = true, // This should be the new value
            isMine = symbol.isMine
        )
        val bool = true

        coEvery { symbolDao.updateSymbol(expectedSymbolEntity) } just Runs

        symbolRepository.updateFavorite(symbol, bool)

        coVerify { symbolDao.updateSymbol(expectedSymbolEntity) }
    }

    @Test
    fun `should update Favorite status when symbol and false is inputted`() = runBlocking {
        val symbol = Symbol(
            id = 1,
            text = "symbol1",
            imageUrl = null,
            categoryId = 1,
            isFavorite = true,
            isMine = false
        )
        val expectedSymbolEntity = SymbolEntity(
            id = symbol.id,
            text = symbol.text,
            imageUrl = symbol.imageUrl,
            categoryId = symbol.categoryId,
            isFavorite = true, // This should be the new value
            isMine = symbol.isMine
        )
        val bool = true

        coEvery { symbolDao.updateSymbol(expectedSymbolEntity) } just Runs

        symbolRepository.updateFavorite(symbol, bool)

        coVerify { symbolDao.updateSymbol(expectedSymbolEntity) }

    }

    @Test
    fun `should return nextSymbolId for getNextSymbolId`() = runBlocking {
        val lastSymbolEntity = SymbolEntity(
            id = 500,
            text = "마지막",
            imageUrl = null,
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )
        val expectedNextSymbolId = 501
        coEvery { symbolDao.getLastSymbol() } returns flowOf(lastSymbolEntity)
        val result = symbolRepository.getNextSymbolId().first()
        assertEquals(expectedNextSymbolId, result)
    }

    @Test
    fun `should reset symbols and favorites for resetSymbolsAndFavorites`() = runBlocking{
        coEvery { symbolDao.deleteAllMySymbols() } returns Unit
        coEvery { symbolDao.resetFavoriteSymbols() } returns Unit
        val result = symbolRepository.resetSymbolsAndFavorites()

        assertEquals(Unit, result)
    }

    @Test
    fun `should insert symbol for insertSymbol`() = runBlocking {
        val symbolToInsert = Symbol(
            id = 500,
            text = "마지막",
            imageUrl = null,
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )
        val symbolEntityToInsert = SymbolEntity(
            id = 500,
            text = "마지막",
            imageUrl = null,
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )
        coEvery { symbolDao.insertSymbol(symbolEntityToInsert) } returns Unit
        val result = symbolRepository.insertSymbol(symbolToInsert)
        assertEquals(Unit, result)
    }

    @Test
    fun `should delete symbol for deleteSymbol`() = runBlocking {
        val symbolToDelete = Symbol(
            id = 500,
            text = "마지막",
            imageUrl = null,
            categoryId = 1,
            isFavorite = false,
            isMine = false
        )
        coEvery { symbolDao.deleteSymbolById(symbolToDelete.id) } returns Unit
        val result = symbolRepository.deleteSymbol(symbolToDelete)
        assertEquals(Unit, result)
    }

    private fun createMockImagePart(): MultipartBody.Part {
        // Mocking a MultipartBody.Part for testing
        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val imageBytes = "your_image_bytes".toByteArray() // Replace with actual image bytes
        val requestBody = imageBytes.toRequestBody(mediaType)
        return MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
    }

    @Test
    fun `should return success resource when createSymbolBackup is successful`() {
        runBlocking {
            val symbolText = "Test Symbol"
            val categoryId = 1
            val imagePart = createMockImagePart()
            val mockId = 501
            val mockImageUrl = "http://~~"

            val mySymbolDto = MySymbolDto(
                id = mockId,
                imageUrl = mockImageUrl
            )
            val mySymbol = MySymbol(
                id = mockId,
                imageUrl = mockImageUrl
            )

            val successResponse = Response.success<MySymbolDto>(200, mySymbolDto)

            coEvery { mockSessionManager.cachedToken.value?.accessToken } returns mockAccessToken
            coEvery {
                mySymbolRemoteSource.createSymbolBackup(
                    mockAuthHeader,
                    symbolText,
                    categoryId,
                    imagePart
                )
            } returns flowOf(successResponse)

            val result = symbolRepository.createSymbolBackup(symbolText, categoryId, imagePart)

            result.collect { resource ->
                assert(resource.status == Status.SUCCESS)
                assert(resource.data == mySymbol)
                assert(resource.message == null)
            }
        }
    }

    @Test
    fun `should return error resource when createSymbolBackup fails`() {
        runBlocking {
            val symbolText = "TooooooooooooolongggggggggSymbolllllllTexttttttttttttt"
            val categoryId = 1
            val imagePart = createMockImagePart()

            val errorResponse = Response.error<MySymbolDto>(400, mockErrorResponseBody)

            coEvery { mockSessionManager.cachedToken.value?.accessToken } returns mockAccessToken
            coEvery {
                mySymbolRemoteSource.createSymbolBackup(
                    mockAuthHeader,
                    symbolText,
                    categoryId,
                    imagePart
                )
            } returns flowOf(errorResponse)

            val result = symbolRepository.createSymbolBackup(symbolText, categoryId, imagePart)

            result.collect { resource ->
                assert(resource.status == Status.ERROR)
                assert(resource.data == null)
                assert(resource.message == "key of message")
            }
        }
    }

}
