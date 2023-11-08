package com.example.speechbuddy.data.local

import java.io.File

interface WeigthTableOperations {
    fun readFromFile(filename: String): File
    fun writeToFile(filename: String, content: String)
}