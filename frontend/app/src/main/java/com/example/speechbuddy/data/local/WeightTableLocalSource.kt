package com.example.speechbuddy.data.local

import android.content.Context
import java.io.File

class WeightTableImpl(private val context: Context) : WeigthTableOperations {
    override fun readFromFile(filename: String): File {
        val file = File(context.filesDir, filename)
        return file
    }

    override fun writeToFile(filename: String, content: String) {
        val file = File(context.filesDir, filename)
        file.appendText(content)
    }

    override fun replaceFileContent(filename: String, lineNo: Int, newString: String) {
        val file = File(context.filesDir, filename)

        val lines = file.readLines().toMutableList()

        var cnt = 0
        for (i in (0 until lines.size)) {
            if (lineNo == cnt) {
                lines[i] = newString
            }
            cnt++
        }

        file.writeText(lines.joinToString(System.lineSeparator()).replace(" ", ""))
    }
}