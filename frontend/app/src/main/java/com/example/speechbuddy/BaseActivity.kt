package com.example.speechbuddy

import androidx.appcompat.app.AppCompatActivity
import com.example.speechbuddy.domain.SessionManager
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

}