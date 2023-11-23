package com.example.speechbuddy.data.remote

import android.content.Context
import android.util.Log
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
        var flag = false
        for (symbol in symbolList) {
            internalDir.listFiles()?.forEach { file ->
                Log.d("test", "checking against $file")

                // Check if the file matches your criteria
                flag = file.name == "symbol_${symbol.id}.png"
            }
            if (!flag && symbol.id > 500) {
                Log.d("test", "downloading ${symbol.id}")
                symbol.imageUrl?.let { downloadImage(it, "symbol_${symbol.id}.png") }
            }
        }

    }

    override fun downloadImage(url: String, filename: String) {
        realImageDownloader.downloadImage(url, filename)
    }

}