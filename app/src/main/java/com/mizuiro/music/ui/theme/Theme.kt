package com.mizuiro.music.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Mizuiro Theme
 * 
 * The main theme composable that applies the Mizuiro aesthetic.
 * Currently only supports light theme as per the specification.
 */

private val MizuiroLightColorScheme = lightColorScheme(
    primary = MizuiroBase,
    onPrimary = FadedBlack,
    primaryContainer = MizuiroLight,
    onPrimaryContainer = FadedBlack,
    
    secondary = SteelBlue,
    onSecondary = FadedBlack,
    secondaryContainer = IcyGrey,
    onSecondaryContainer = FadedBlack,
    
    tertiary = BlushPink,
    onTertiary = FadedBlack,
    tertiaryContainer = PastelLavender,
    onTertiaryContainer = FadedBlack,
    
    error = BlushPink,
    onError = FadedBlack,
    errorContainer = PastelLavender,
    onErrorContainer = FadedBlack,
    
    background = CoolWhite,
    onBackground = FadedBlack,
    surface = CoolWhite,
    onSurface = FadedBlack,
    surfaceVariant = IcyGrey,
    onSurfaceVariant = SteelBlue,
    
    outline = CyberSilver,
    outlineVariant = IcyGrey,
    
    scrim = Black,
    inverseSurface = FadedBlack,
    inverseOnSurface = CoolWhite,
    inversePrimary = MizuiroLight
)

// Dark theme (for future implementation)
private val MizuiroDarkColorScheme = darkColorScheme(
    primary = MizuiroBase,
    onPrimary = FadedBlack,
    primaryContainer = MizuiroLight,
    onPrimaryContainer = FadedBlack,
    
    secondary = SteelBlue,
    onSecondary = FadedBlack,
    secondaryContainer = IcyGrey,
    onSecondaryContainer = FadedBlack,
    
    tertiary = BlushPink,
    onTertiary = FadedBlack,
    tertiaryContainer = PastelLavender,
    onTertiaryContainer = FadedBlack,
    
    error = BlushPink,
    onError = FadedBlack,
    errorContainer = PastelLavender,
    onErrorContainer = FadedBlack,
    
    background = DarkCoolWhite,
    onBackground = CoolWhite,
    surface = DarkCoolWhite,
    onSurface = CoolWhite,
    surfaceVariant = DarkMizuiro,
    onSurfaceVariant = DarkSteelBlue,
    
    outline = CyberSilver,
    outlineVariant = IcyGrey,
    
    scrim = Black,
    inverseSurface = CoolWhite,
    inverseOnSurface = FadedBlack,
    inversePrimary = MizuiroBase
)

@Composable
fun MizuiroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        MizuiroDarkColorScheme
    } else {
        MizuiroLightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MizuiroTypography,
        shapes = MizuiroShapes,
        content = content
    )
}
