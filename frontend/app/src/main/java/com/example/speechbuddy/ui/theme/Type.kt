package com.example.speechbuddy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.speechbuddy.R

val manropeFamily = FontFamily(
    Font(R.font.Manrope_Bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.Manrope_ExtraBold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(R.font.Manrope_SemiBold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.Manrope_Regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.Manrope_Light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.Manrope_ExtraLight, FontWeight.ExtraLight, FontStyle.Normal),
    Font(R.font.Manrope_Medium, FontWeight.Medium, FontStyle.Normal)
)

// Set of Material typography styles to start with
val Typography = Typography(
    // h1
    displayLarge = TextStyle(
        fontFamily = manropeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
    ),
    // h2
    displayMedium = TextStyle(
        fontFamily = manropeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
    ),
    // h3
    displaySmall = TextStyle(
        fontFamily = manropeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
    // h4
    headlineMedium = TextStyle(
        fontFamily = manropeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    ),
    // h5
    headlineSmall = TextStyle(
        fontFamily = manropeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
    ),
    // h2_regular
    bodyMedium = TextStyle(
        fontFamily = manropeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
    ),
    // h4_regular
    bodySmall = TextStyle(
        fontFamily = manropeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),

)
