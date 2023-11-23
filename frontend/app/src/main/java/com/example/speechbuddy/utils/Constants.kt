package com.example.speechbuddy.utils

class Constants {
    companion object {
        const val BASE_URL = "http://54.180.112.72:8000/"

        const val DEFAULT_SYMBOL_IMAGE_PATH = "file:///android_asset/images/symbol/"
        const val CATEGORY_IMAGE_PATH = "file:///android_asset/images/category/"

        const val DATABASE_NAME = "speechbuddy-db"
        const val SYMBOL_DATA_FILENAME = "symbols.json"
        const val CATEGORY_DATA_FILENAME = "categories.json"

        const val AUTH_TOKEN_PREFS: String = "com.example.speechbuddy.AUTH_TOKEN_PREFS"
        const val ACCESS_TOKEN_PREF: String = "com.example.speechbuddy.ACCESS_TOKEN_PREF"
        const val REFRESH_TOKEN_PREF: String = "com.example.speechbuddy.REFRESH_TOKEN_PREF"

        const val SETTINGS_PREFS: String = "com.example.speechbuddy.SETTINGS_PREFS"
        const val AUTO_BACKUP_PREF: String = "com.example.speechbuddy.AUTO_BACKUP_PREF"
        const val DARK_MODE_PREF: String = "com.example.speechbuddy.DARK_MODE_PREF"
        const val INITIAL_PAGE_PREF: String = "com.example.speechbuddy.INITIAL_PAGE_PREF"
        const val LAST_BACKUP_DATE_PREF: String = "com.example.speechbuddy.LAST_BACKUP_DATE_PREF"

        const val MINIMUM_PASSWORD_LENGTH = 8
        const val MAXIMUM_NICKNAME_LENGTH = 15
        const val CODE_LENGTH = 6
        const val MAXIMUM_LINES_FOR_SYMBOL_TEXT = 2
        const val MAXIMUM_SYMBOL_TEXT_LENGTH = 20

        const val DEFAULT_SYMBOL_COUNT = 500
    }
}