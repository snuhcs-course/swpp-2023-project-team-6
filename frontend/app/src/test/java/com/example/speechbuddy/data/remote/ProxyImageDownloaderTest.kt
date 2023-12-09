package com.example.speechbuddy.data.remote

import android.content.Context
import com.example.speechbuddy.domain.models.Symbol
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ProxyImageDownloaderTest {

    private val mockRealImageDownloader: RealImageDownloader = mockk(relaxed = true)
    private val mockContext: Context = mockk()
    private val proxyImageDownloader = ProxyImageDownloader(mockRealImageDownloader, mockContext)

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Before
    fun setUp() {
        mockkStatic(File::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `checkImage should download image if not present and id is greater than 500`() {
        // Arrange
        val symbolList =
            listOf(Symbol(501, "symbol", "http://example.com/image.png", 1, false, true))
        val filesDir = mockk<File>()
        val mockFile: File = mockk()

        every { mockContext.filesDir } returns filesDir
        every { filesDir.listFiles() } returns arrayOf(mockFile)
        every { mockFile.name } returns "other_file.png"

        // Act
        proxyImageDownloader.checkImage(symbolList)

        // Assert
        coVerify {
            mockRealImageDownloader.downloadImage(
                "http://example.com/image.png",
                "symbol_501.png"
            )
        }
    }

    @Test
    fun `checkImage should not download image if present`() {
        // Arrange
        val symbolList =
            listOf(Symbol(501, "symbol", "http://example.com/image.png", 1, false, true))
        val filesDir = mockk<File>()
        val mockFile: File = mockk()

        every { mockContext.filesDir } returns filesDir
        every { filesDir.listFiles() } returns arrayOf(mockFile)
        every { mockFile.name } returns "symbol_501.png"

        // Act
        proxyImageDownloader.checkImage(symbolList)

        // Assert
        coVerify(exactly = 0) { mockRealImageDownloader.downloadImage(any(), any()) }
    }
}