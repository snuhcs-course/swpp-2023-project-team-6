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

    override fun replaceFileContent(filename: String, lineNo: Int, newString: String) {
        val file = File(context.filesDir, filename)

        // Step 1: Read the file content into memory
        val lines = file.readLines().toMutableList()

        // Step 2: Process the content while not interacting with the file
        var cnt = 0
        for (i in (0 until lines.size)){
            if(lineNo == cnt){
                lines[i] = newString
            }
            cnt++
        }

        // Step 3: Write the new content back to the file
        file.writeText(lines.joinToString(System.lineSeparator()).replace(" ",""))
    }
}