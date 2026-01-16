package com.gaurav.smartcook.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gaurav.smartcook.R

val Nunito = FontFamily(
    Font(R.font.nunitoregular, FontWeight.Normal),
    Font(R.font.nunitomedium, FontWeight.Medium)
)

val Merriweather = FontFamily(
    Font(R.font.merriweathervariablefontopszwdthwght, FontWeight.Normal),
    Font(R.font.merriweathervariablefontopszwdthwght, FontWeight.Bold),
            Font(R.font.merriweatheritalicvariablefontopszwdthwght, FontWeight.Normal)

)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Merriweather,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)