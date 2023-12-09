package com.example.speechbuddy.data.remote

import android.content.Context
import com.example.speechbuddy.domain.models.Symbol
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProxyImageDownloader @Inject constructor(
    private val realImageDownloader: RealImageDownloader,
    private val context: Context
) : ImageDownloader {
    fun checkImage(symbolList: List<Symbol>) {
        val internalDir = context.filesDir
        val flag = mutableListOf<Boolean>()
        for (symbol in symbolList.drop(499)) { // compare after 500
            internalDir.listFiles()?.forEach { file ->
                // Check if the file matches your criteria
                flag.add(file.name == "symbol_${symbol.id}.png")
            }
            val result = flag.any{ it }
            if (!result && symbol.id > 500) {
                symbol.imageUrl?.let { downloadImage(it, "symbol_${symbol.id}.png") }
            }
        }

    }

    override fun downloadImage(url: String, filename: String) {
        realImageDownloader.downloadImage(url, filename)
    }

}