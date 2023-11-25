package com.example.speechbuddy.data.remote

interface ImageDownloader {
    fun downloadImage(url: String, filename: String)
}