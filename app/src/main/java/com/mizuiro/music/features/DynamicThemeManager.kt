package com.mizuiro.music.features

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dynamic Theme Manager
 * 
 * Manages time-based and seasonal theme variations
 * for the Mizuiro aesthetic
 */
@Singleton
class DynamicThemeManager @Inject constructor() {
    
    private val _currentTheme = MutableStateFlow(MizuiroTheme.DAWN)
    val currentTheme: StateFlow<MizuiroTheme> = _currentTheme.asStateFlow()
    
    private val _particleEffects = MutableStateFlow(false)
    val particleEffects: StateFlow<Boolean> = _particleEffects.asStateFlow()
    
    /**
     * Update theme based on current time and season
     */
    fun updateTheme() {
        val now = LocalDateTime.now()
        val time = now.toLocalTime()
        val season = getCurrentSeason(now.monthValue)
        
        val theme = when {
            time.isBefore(LocalTime.of(6, 0)) -> MizuiroTheme.NIGHT
            time.isBefore(LocalTime.of(12, 0)) -> MizuiroTheme.DAWN
            time.isBefore(LocalTime.of(18, 0)) -> MizuiroTheme.DAY
            else -> MizuiroTheme.DUSK
        }
        
        _currentTheme.value = theme
        _particleEffects.value = shouldShowParticles(theme, season)
    }
    
    /**
     * Get current season
     */
    private fun getCurrentSeason(month: Int): Season {
        return when (month) {
            in 3..5 -> Season.SPRING
            in 6..8 -> Season.SUMMER
            in 9..11 -> Season.AUTUMN
            else -> Season.WINTER
        }
    }
    
    /**
     * Check if particles should be shown
     */
    private fun shouldShowParticles(theme: MizuiroTheme, season: Season): Boolean {
        return when (theme) {
            MizuiroTheme.DAWN, MizuiroTheme.DUSK -> true
            MizuiroTheme.NIGHT -> season == Season.WINTER
            MizuiroTheme.DAY -> season == Season.SPRING
        }
    }
    
    /**
     * Get seasonal mascot
     */
    fun getSeasonalMascot(season: Season): String {
        return when (season) {
            Season.SPRING -> "🌸"
            Season.SUMMER -> "☀️"
            Season.AUTUMN -> "🍂"
            Season.WINTER -> "❄️"
        }
    }
    
    /**
     * Get theme description
     */
    fun getThemeDescription(theme: MizuiroTheme): String {
        return when (theme) {
            MizuiroTheme.DAWN -> "Gentle morning light"
            MizuiroTheme.DAY -> "Bright afternoon sun"
            MizuiroTheme.DUSK -> "Peaceful evening glow"
            MizuiroTheme.NIGHT -> "Starry night sky"
        }
    }
}

/**
 * Mizuiro Theme variations
 */
enum class MizuiroTheme {
    DAWN, DAY, DUSK, NIGHT
}

/**
 * Season enum
 */
enum class Season {
    SPRING, SUMMER, AUTUMN, WINTER
}

/**
 * Dynamic theme data class
 */
data class DynamicTheme(
    val theme: MizuiroTheme,
    val season: Season,
    val baseColor: Color,
    val accentColor: Color,
    val backgroundColor: Color,
    val textColor: Color,
    val particleColor: Color,
    val mascot: String,
    val description: String,
    val showParticles: Boolean
)

/**
 * Theme color definitions
 */
object MizuiroThemeColors {
    val DawnBase = Color(0xFF87CEEB) // Sky blue
    val DawnAccent = Color(0xFFFFB6C1) // Light pink
    val DawnBackground = Color(0xFFF0F8FF) // Alice blue
    val DawnText = Color(0xFF2F4F4F) // Dark slate gray
    val DawnParticle = Color(0xFFFFE4E1) // Misty rose
    
    val DayBase = Color(0xFF4682B4) // Steel blue
    val DayAccent = Color(0xFFFF69B4) // Hot pink
    val DayBackground = Color(0xFFE6F3FF) // Light blue
    val DayText = Color(0xFF191970) // Midnight blue
    val DayParticle = Color(0xFFFFF0F5) // Lavender blush
    
    val DuskBase = Color(0xFF5F9EA0) // Cadet blue
    val DuskAccent = Color(0xFFFF1493) // Deep pink
    val DuskBackground = Color(0xFFF5F5DC) // Beige
    val DuskText = Color(0xFF2F2F2F) // Dark gray
    val DuskParticle = Color(0xFFFFEBCD) // Blanched almond
    
    val NightBase = Color(0xFF2F4F4F) // Dark slate gray
    val NightAccent = Color(0xFF9370DB) // Medium purple
    val NightBackground = Color(0xFF1C1C1C) // Dark gray
    val NightText = Color(0xFFF0F8FF) // Alice blue
    val NightParticle = Color(0xFFE6E6FA) // Lavender
}

/**
 * Get dynamic theme colors
 */
fun getDynamicThemeColors(theme: MizuiroTheme): DynamicTheme {
    val season = getCurrentSeason()
    return when (theme) {
        MizuiroTheme.DAWN -> DynamicTheme(
            theme = theme,
            season = season,
            baseColor = MizuiroThemeColors.DawnBase,
            accentColor = MizuiroThemeColors.DawnAccent,
            backgroundColor = MizuiroThemeColors.DawnBackground,
            textColor = MizuiroThemeColors.DawnText,
            particleColor = MizuiroThemeColors.DawnParticle,
            mascot = "🌅",
            description = "Gentle morning light",
            showParticles = true
        )
        MizuiroTheme.DAY -> DynamicTheme(
            theme = theme,
            season = season,
            baseColor = MizuiroThemeColors.DayBase,
            accentColor = MizuiroThemeColors.DayAccent,
            backgroundColor = MizuiroThemeColors.DayBackground,
            textColor = MizuiroThemeColors.DayText,
            particleColor = MizuiroThemeColors.DayParticle,
            mascot = "☀️",
            description = "Bright afternoon sun",
            showParticles = season == Season.SPRING
        )
        MizuiroTheme.DUSK -> DynamicTheme(
            theme = theme,
            season = season,
            baseColor = MizuiroThemeColors.DuskBase,
            accentColor = MizuiroThemeColors.DuskAccent,
            backgroundColor = MizuiroThemeColors.DuskBackground,
            textColor = MizuiroThemeColors.DuskText,
            particleColor = MizuiroThemeColors.DuskParticle,
            mascot = "🌆",
            description = "Peaceful evening glow",
            showParticles = true
        )
        MizuiroTheme.NIGHT -> DynamicTheme(
            theme = theme,
            season = season,
            baseColor = MizuiroThemeColors.NightBase,
            accentColor = MizuiroThemeColors.NightAccent,
            backgroundColor = MizuiroThemeColors.NightBackground,
            textColor = MizuiroThemeColors.NightText,
            particleColor = MizuiroThemeColors.NightParticle,
            mascot = "🌙",
            description = "Starry night sky",
            showParticles = season == Season.WINTER
        )
    }
}

private fun getCurrentSeason(): Season {
    val month = LocalDateTime.now().monthValue
    return when (month) {
        in 3..5 -> Season.SPRING
        in 6..8 -> Season.SUMMER
        in 9..11 -> Season.AUTUMN
        else -> Season.WINTER
    }
}
