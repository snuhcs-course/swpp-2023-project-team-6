package com.example.speechbuddy.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.speechbuddy.service.BackupService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.InputStream

class RealImageDownloaderTest {

    private val mockBackupService: BackupService = mockk()
    private val mockContext: Context = mockk()
    private val realImageDownloader = RealImageDownloader(mockBackupService, mockContext)

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Before
    fun setUp() {
        mockkStatic(BitmapFactory::class)
        mockkStatic(Bitmap::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    @Test
    fun `downloadImage should download and save image`() {
        // Arrange
        val url = "http://example.com/image.png"
        val filename = "image.png"
        val mockResponseBody: ResponseBody = mockk()
        val mockBitmap: Bitmap = mockk()
        val mockInputStream: InputStream = ByteArrayInputStream(ByteArray(0))
        val filesDir = tempFolder.newFolder()

        coEvery { mockContext.filesDir } returns filesDir
        coEvery { mockResponseBody.byteStream() } returns mockInputStream
        coEvery { BitmapFactory.decodeStream(mockInputStream) } returns mockBitmap
        coEvery { mockBackupService.getImage(url) } returns mockResponseBody
        coEvery { mockBitmap.compress(any(), any(), any()) } returns true

        // Act
        realImageDownloader.downloadImage(url, filename)

        // Assert
        coVerify { mockBackupService.getImage(url) }
        coVerify { BitmapFactory.decodeStream(mockInputStream) }
    }
}