package com.example.speechbuddy.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.speechbuddy.service.BackupService
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


class RealImageDownloader @Inject constructor(
    private val backupService: BackupService,
    private val context: Context
) : ImageDownloader {
    private fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
        // Get the app's internal storage directory
        val internalDir = context.filesDir
        val imageFile = File(internalDir, "$fileName")

        // Compress the bitmap as PNG and write it to the file
        FileOutputStream(imageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        return imageFile
    }

    fun convertStreamToBitmap(inputStream: InputStream): Bitmap? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024) // Adjust if necessary
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteArrayOutputStream.write(buffer, 0, len)
        }
        val byteArray = byteArrayOutputStream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    override fun downloadImage(url: String, filename: String) {
        runBlocking {
            val call: ResponseBody = backupService.getImage(url)
            bitmapToFile(context, call.byteStream().use(BitmapFactory::decodeStream) ,filename)
        }
        // Implement the logic to download the image from the URL
        // This could be using Retrofit, OkHttp, or any other HTTP client
    }
}