package com.example.speechbuddy.repository

import com.example.speechbuddy.data.local.CategoryDao
import com.example.speechbuddy.data.local.SymbolDao
import com.example.speechbuddy.data.local.models.CategoryEntity
import com.example.speechbuddy.data.local.models.CategoryMapper
import com.example.speechbuddy.data.local.models.SymbolEntity
import com.example.speechbuddy.data.local.models.SymbolMapper
import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.models.Entry
import com.example.speechbuddy.domain.models.Symbol
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SymbolRepositoryTest {
    private lateinit var symbolDao: SymbolDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var symbolMapper: SymbolMapper
    private lateinit var categoryMapper: CategoryMapper
    private lateinit var mockSymbolRepository: SymbolRepository

    val categoryTypes = listOf(
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
        symbolMapper = mockk(relaxed = true)
        categoryMapper = mockk(relaxed = true)

        // create symbolEntities
        val symbolEntities = mutableListOf<SymbolEntity>()
        for (i in 1..500) {
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

        mockSymbolRepository = SymbolRepository(symbolDao, categoryDao, symbolMapper, categoryMapper)
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

        val result = mockSymbolRepository.getSymbols(mockQuery).first()[0]

        assertEquals(symbol, result)
    }

    @Test
    fun `should return list of Symbols when nothing is inputted`() = runBlocking {
        val symbolList = mutableListOf<Symbol>()

        // create symbolList
        for (i in 1..500) {
            val tmpObj = Symbol(
                id = 1,
                text = "symbol$i",
                imageUrl = null,
                categoryId = i,
                isFavorite = false,
                isMine = false
            )
            symbolList.add(tmpObj)
        }

        val result = mockSymbolRepository.getSymbols("").first()

        assertEquals(symbolList.toList(), result)
    }

    @Test
    fun `should return Category when query is inputted`() = runBlocking {
        val mockQuery = "가족"

        val category = Category(1, "가족")

        val categoryEntity = CategoryEntity(1, "가족")

        coEvery { categoryDao.getCategoriesByQuery(mockQuery) } returns flowOf(listOf(categoryEntity))

        val result = mockSymbolRepository.getCategories(mockQuery).first()[0]

        assertEquals(category, result)
    }

    @Test
    fun `should return list of Categories when nothing is inputted`() = runBlocking {
        val categoryList = mutableListOf<Category>()
        var cnt = 1
        for (category in categoryTypes) {
            categoryList.add(Category(cnt++, category))
        }

        val result = mockSymbolRepository.getCategories("").first()

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
                id = 1,
                text = "symbol$i",
                imageUrl = null,
                categoryId = i,
                isFavorite = false,
                isMine = false
            )
            symbolList.add(tmpObj)
        }


        val entryList = mutableListOf<Entry>()
        entryList.addAll(categoryList)
        entryList.addAll(symbolList)

        val result = mockSymbolRepository.getEntries("").first()

        assertEquals(entryList, result)
    }

    @Test
    fun `should return list of Entries when query(symbol) is inputted`() = runBlocking {
        val mockQuery = "가다"

        val entryList = mutableListOf<Entry>()
        entryList.addAll(listOf(Symbol(3, "가다", null, 1, false, false)))

        coEvery { symbolDao.getSymbolsByQuery(mockQuery) } returns flowOf(
            listOf(
                SymbolEntity(
                    3,
                    "가다",
                    null,
                    1,
                    false,
                    false
                )
            )
        )
        coEvery { categoryDao.getCategoriesByQuery(mockQuery) } returns flowOf(listOf())

        val result = mockSymbolRepository.getEntries(mockQuery).first()

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

        val result = mockSymbolRepository.getEntries(mockQuery).first()

        assertEquals(entryList, result)
    }

    @Test
    fun `should return list of Entries when query(symbol&category) is inputted`() = runBlocking {
        val mockQuery = "가족"

        val entryList = mutableListOf<Entry>()
        entryList.addAll(listOf(Category(1, "가족")))
        entryList.addAll(listOf(Symbol(11, "가족", null, 1, false, false)))

        coEvery { symbolDao.getSymbolsByQuery(mockQuery) } returns flowOf(
            listOf(
                SymbolEntity(
                    11,
                    "가족",
                    null,
                    1,
                    false,
                    false
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

        val result = mockSymbolRepository.getEntries(mockQuery).first()

        assertEquals(entryList, result)
    }

    @Test
    fun `should return list of Entries when query(no match) is inputted`() = runBlocking {
        val mockQuery = "가족"

        val entryList = mutableListOf<Entry>()

        coEvery { symbolDao.getSymbolsByQuery(mockQuery) } returns flowOf(listOf())
        coEvery { categoryDao.getCategoriesByQuery(mockQuery) } returns flowOf(listOf())

        val result = mockSymbolRepository.getEntries(mockQuery).first()

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

        val result = mockSymbolRepository.getFavoriteSymbols(mockQuery).first()

        assertEquals(listOf(favSymbol), result)
    }

    @Test
    fun `should return list of Favorite Symbols when query(no match) is inputted`() =
        runBlocking {
            val mockQuery = "123"

            coEvery { symbolDao.getFavoriteSymbolsByQuery(mockQuery) } returns flowOf(listOf())

            val result = mockSymbolRepository.getFavoriteSymbols(mockQuery).first()

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

        val result = mockSymbolRepository.getFavoriteSymbols("").first()

        assertEquals(expectedResult, result)
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

        val result = mockSymbolRepository.getSymbolsByCategory(category).first()

        assertEquals(listOf(symbol), result)
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

        mockSymbolRepository.updateFavorite(symbol, bool)

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

        mockSymbolRepository.updateFavorite(symbol, bool)

        coVerify { symbolDao.updateSymbol(expectedSymbolEntity) }

    }
}