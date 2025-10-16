package com.mizuiro.music.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Mizuiro Color Palette
 * 
 * This defines the complete color system for the Mizuiro aesthetic.
 * All colors follow the cold, melancholic, digital-nostalgic theme.
 */

// Primary Colors
val MizuiroLight = Color(0xFFD4F1F9)    // Main light blue
val MizuiroBase = Color(0xFFA8E6F0)     // Base accent blue
val MizuiroMedium = Color(0xFF7DD3E5)   // Medium intensity blue

// Background Colors
val CoolWhite = Color(0xFFF5F8FA)       // Main background (never pure white)
val IcyGrey = Color(0xFFE8EDEF)         // Secondary background
val Concrete = Color(0xFFD3D8DB)        // Module background

// Accent Colors
val SteelBlue = Color(0xFFB8C5D0)       // Secondary text
val CyberSilver = Color(0xFFC8D4DC)     // Borders, dividers
val FadedBlack = Color(0xFF4A5860)      // Primary text (never pure black)

// Kawaii Accent Colors (used sparingly)
val BlushPink = Color(0xFFFFD4E5)       // Special elements only
val PastelLavender = Color(0xFFE8E4F3)  // Auxiliary kawaii color

// System Colors
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Transparent = Color(0x00000000)

// Semantic Colors
val Success = MizuiroBase
val Warning = PastelLavender
val Error = BlushPink
val Info = SteelBlue

// Dark Theme Colors (for future implementation)
val DarkMizuiro = Color(0xFF2A3A4A)
val DarkCoolWhite = Color(0xFF1A2A3A)
val DarkSteelBlue = Color(0xFF6A7A8A)
