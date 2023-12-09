package com.example.speechbuddy.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.speechbuddy.service.BackupService
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealImageDownloader @Inject constructor(
    private val backupService: BackupService,
    private val context: Context
) : ImageDownloader {
    private fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
        // Get the app's internal storage directory
        val internalDir = context.filesDir
        val imageFile = File(internalDir, fileName)

        // Compress the bitmap as PNG and write it to the file
        FileOutputStream(imageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        return imageFile
    }

    override fun downloadImage(url: String, filename: String) {
        runBlocking {
            val call: ResponseBody = backupService.getImage(url)
            bitmapToFile(context, call.byteStream().use(BitmapFactory::decodeStream), filename)
        }
    }
}