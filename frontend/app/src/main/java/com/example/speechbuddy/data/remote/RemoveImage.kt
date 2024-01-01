package com.example.speechbuddy.data.remote


import android.content.Context
import com.example.speechbuddy.domain.models.Symbol
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveImage @Inject constructor(
    private val context: Context
) {
    fun checkImage(symbolList: List<Symbol>): List<String> {
        val internalDir = context.filesDir
        val removeList = mutableListOf<String>()
        for (symbol in symbolList.drop(499)) { // compare after 500
            internalDir.listFiles()?.forEach { file ->
                // Check if the file matches your criteria
                if (file.name == "symbol_${symbol.id}.png") {
                    removeList.add(file.name)
                }
            }
        }
        return removeList
    }

    fun removeImage(filename: String) {
        runBlocking {
            context.deleteFile(filename)
        }
    }
}