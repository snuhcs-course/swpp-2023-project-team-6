package com.example.speechbuddy.data.local

import android.content.Context
import java.io.File

class WeightTableImpl(private val context: Context) : WeigthTableOperations {
    override fun readFromFile(filename: String) :File{
        // Implementation that uses the context to read from a file
        val file = File(context.filesDir, filename)
        return file
    }

    override fun writeToFile(filename: String, content: String) {
        // Implementation that uses the context to write to a file
    }
}