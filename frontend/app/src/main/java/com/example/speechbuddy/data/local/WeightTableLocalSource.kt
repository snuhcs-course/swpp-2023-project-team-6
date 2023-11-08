package com.example.speechbuddy.data.local

import android.content.Context
import java.io.File

class WeightTableImpl(private val context: Context) : WeigthTableOperations {
    override fun readFromFile(filename: String) :File{
        val file = File(context.filesDir, filename)
        return file
    }

    override fun writeToFile(filename: String, content: String) {
        val file = File(context.filesDir, filename)
        file.appendText(content)
    }

    override fun replaceFileContent(filename: String, oldString: String, newString: String) {
        val file = File(context.filesDir, filename)
        val reader = file.inputStream().bufferedReader()
        val writer = file.outputStream().bufferedWriter()


        val con = file.readLines()
        var cnt = 1
        for (line in con){

        }



        val content = file.readText()
        val updateContent = content.replace(oldString, newString)
        file.writeText(updateContent)
    }
}