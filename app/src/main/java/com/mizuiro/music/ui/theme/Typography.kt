package com.mizuiro.music.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Mizuiro Typography System
 * 
 * Defines the complete typography system following Mizuiro aesthetic principles.
 * Uses rounded fonts for main text and pixelated fonts for retro elements.
 */

// Font families
val MPlusRounded = FontFamily(
    Font(R.font.m_plus_rounded_1c_regular, FontWeight.Normal),
    Font(R.font.m_plus_rounded_1c_medium, FontWeight.Medium),
    Font(R.font.m_plus_rounded_1c_bold, FontWeight.Bold)
)

val VT323 = FontFamily(
    Font(R.font.vt323_regular, FontWeight.Normal)
)

// Fallback fonts if custom fonts are not available
val FallbackRounded = FontFamily(
    Font(androidx.compose.ui.text.font.FontFamily.Default, FontWeight.Normal),
    Font(androidx.compose.ui.text.font.FontFamily.Default, FontWeight.Medium),
    Font(androidx.compose.ui.text.font.FontFamily.Default, FontWeight.Bold)
)

val FallbackRetro = FontFamily.Monospace

// Typography definitions
val MizuiroTypography = Typography(
    // Display styles
    displayLarge = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp,
        lineHeight = 33.6.sp, // 1.2 line height
        color = FadedBlack
    ),
    displayMedium = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = (-0.25).sp,
        lineHeight = 28.8.sp,
        color = FadedBlack
    ),
    displaySmall = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        lineHeight = 24.sp,
        color = FadedBlack
    ),
    
    // Headline styles
    headlineLarge = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 0.sp,
        lineHeight = 25.2.sp, // 1.4 line height
        color = FadedBlack
    ),
    headlineMedium = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        lineHeight = 22.4.sp, // 1.4 line height
        color = FadedBlack
    ),
    headlineSmall = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.sp,
        lineHeight = 19.6.sp, // 1.4 line height
        color = FadedBlack
    ),
    
    // Title styles
    titleLarge = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        lineHeight = 22.4.sp, // 1.4 line height
        color = FadedBlack
    ),
    titleMedium = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.sp,
        lineHeight = 19.6.sp, // 1.4 line height
        color = FadedBlack
    ),
    titleSmall = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.sp,
        lineHeight = 16.8.sp, // 1.4 line height
        color = FadedBlack
    ),
    
    // Body styles
    bodyLarge = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        letterSpacing = 0.3.sp,
        lineHeight = 19.5.sp, // 1.5 line height
        color = SteelBlue
    ),
    bodyMedium = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.3.sp,
        lineHeight = 18.sp, // 1.5 line height
        color = SteelBlue
    ),
    bodySmall = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 0.3.sp,
        lineHeight = 16.5.sp, // 1.5 line height
        color = SteelBlue
    ),
    
    // Label styles
    labelLarge = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 14.3.sp, // 1.3 line height
        color = SteelBlue
    ),
    labelMedium = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 13.sp, // 1.3 line height
        color = SteelBlue
    ),
    labelSmall = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 11.7.sp, // 1.3 line height
        color = SteelBlue
    )
)

// Custom Mizuiro text styles
val H1 = TextStyle(
    fontFamily = MPlusRounded,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    letterSpacing = (-0.5).sp,
    lineHeight = 33.6.sp,
    color = FadedBlack
)

val H2 = TextStyle(
    fontFamily = MPlusRounded,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    letterSpacing = 0.sp,
    lineHeight = 22.4.sp,
    color = FadedBlack
)

val Body = TextStyle(
    fontFamily = MPlusRounded,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    letterSpacing = 0.3.sp,
    lineHeight = 19.5.sp,
    color = SteelBlue
)

val Caption = TextStyle(
    fontFamily = MPlusRounded,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    letterSpacing = 0.5.sp,
    lineHeight = 14.3.sp,
    color = SteelBlue
)

// Retro/Digital styles
val VT323Style = TextStyle(
    fontFamily = VT323,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    letterSpacing = 0.sp,
    color = MizuiroMedium
)

val RetroDigital = TextStyle(
    fontFamily = VT323,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    letterSpacing = 0.sp,
    color = MizuiroBase
)
