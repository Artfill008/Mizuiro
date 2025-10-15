package com.mizuiro.music.features

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lyrics Manager
 * 
 * Manages lyrics display with beautiful Mizuiro styling
 * and emotional presentation
 */
@Singleton
class LyricsManager @Inject constructor() {
    
    private val _lyricsState = MutableStateFlow(LyricsState())
    val lyricsState: StateFlow<LyricsState> = _lyricsState.asStateFlow()
    
    private val _currentLyrics = MutableStateFlow<List<LyricLine>>(emptyList())
    val currentLyrics: StateFlow<List<LyricLine>> = _currentLyrics.asStateFlow()
    
    /**
     * Load lyrics for a track
     */
    suspend fun loadLyrics(trackId: String, trackTitle: String, artist: String) {
        // Simulate lyrics loading
        val lyrics = generateMockLyrics(trackTitle, artist)
        _currentLyrics.value = lyrics
        _lyricsState.value = LyricsState(
            isLoading = false,
            hasLyrics = lyrics.isNotEmpty(),
            currentTrackId = trackId,
            currentLineIndex = 0
        )
    }
    
    /**
     * Update current line based on playback position
     */
    fun updateCurrentLine(positionMs: Long) {
        val lyrics = _currentLyrics.value
        if (lyrics.isEmpty()) return
        
        val currentIndex = findCurrentLineIndex(lyrics, positionMs)
        _lyricsState.value = _lyricsState.value.copy(currentLineIndex = currentIndex)
    }
    
    /**
     * Find current line index based on position
     */
    private fun findCurrentLineIndex(lyrics: List<LyricLine>, positionMs: Long): Int {
        return lyrics.indexOfLast { it.startTimeMs <= positionMs }.coerceAtLeast(0)
    }
    
    /**
     * Generate mock lyrics for demonstration
     */
    private fun generateMockLyrics(title: String, artist: String): List<LyricLine> {
        return when (title.lowercase()) {
            "sad song" -> listOf(
                LyricLine("In the water blue memories", 0, LyricEmotion.SAD),
                LyricLine("Where dreams fade like morning mist", 3000, LyricEmotion.SAD),
                LyricLine("I find myself lost in the echoes", 6000, LyricEmotion.MELANCHOLIC),
                LyricLine("Of a love that never existed", 9000, LyricEmotion.SAD),
                LyricLine("But the water remembers", 12000, LyricEmotion.HOPEFUL),
                LyricLine("And so do I", 15000, LyricEmotion.NOSTALGIC)
            )
            "happy song" -> listOf(
                LyricLine("Sunshine through the window", 0, LyricEmotion.HAPPY),
                LyricLine("Dancing in the water blue light", 3000, LyricEmotion.JOYFUL),
                LyricLine("Every moment feels like forever", 6000, LyricEmotion.EUPHORIC),
                LyricLine("In this kawaii world of mine", 9000, LyricEmotion.CUTE),
                LyricLine("Let's dance together", 12000, LyricEmotion.HAPPY),
                LyricLine("In the Mizuiro sky", 15000, LyricEmotion.NOSTALGIC)
            )
            else -> listOf(
                LyricLine("♪♪♪ $title ♪♪♪", 0, LyricEmotion.NEUTRAL),
                LyricLine("by $artist", 2000, LyricEmotion.NEUTRAL),
                LyricLine("", 4000, LyricEmotion.NEUTRAL),
                LyricLine("Lyrics not available", 6000, LyricEmotion.NEUTRAL),
                LyricLine("But the music speaks", 8000, LyricEmotion.NOSTALGIC),
                LyricLine("In water blue memories", 10000, LyricEmotion.MELANCHOLIC)
            )
        }
    }
    
    /**
     * Get lyrics display style based on emotion
     */
    fun getLyricsStyle(emotion: LyricEmotion): LyricsStyle {
        return when (emotion) {
            LyricEmotion.SAD -> LyricsStyle(
                color = Color(0xFF4682B4),
                fontSize = 16f,
                fontWeight = "normal",
                opacity = 0.8f,
                animation = "fade"
            )
            LyricEmotion.MELANCHOLIC -> LyricsStyle(
                color = Color(0xFF5F9EA0),
                fontSize = 18f,
                fontWeight = "bold",
                opacity = 0.9f,
                animation = "float"
            )
            LyricEmotion.HAPPY -> LyricsStyle(
                color = Color(0xFFFF69B4),
                fontSize = 20f,
                fontWeight = "bold",
                opacity = 1.0f,
                animation = "bounce"
            )
            LyricEmotion.JOYFUL -> LyricsStyle(
                color = Color(0xFFFF1493),
                fontSize = 22f,
                fontWeight = "bold",
                opacity = 1.0f,
                animation = "sparkle"
            )
            LyricEmotion.EUPHORIC -> LyricsStyle(
                color = Color(0xFFFFB6C1),
                fontSize = 24f,
                fontWeight = "bold",
                opacity = 1.0f,
                animation = "rainbow"
            )
            LyricEmotion.CUTE -> LyricsStyle(
                color = Color(0xFFFFC0CB),
                fontSize = 18f,
                fontWeight = "normal",
                opacity = 0.9f,
                animation = "wiggle"
            )
            LyricEmotion.NOSTALGIC -> LyricsStyle(
                color = Color(0xFF9370DB),
                fontSize = 16f,
                fontWeight = "italic",
                opacity = 0.7f,
                animation = "fade"
            )
            LyricEmotion.HOPEFUL -> LyricsStyle(
                color = Color(0xFF87CEEB),
                fontSize = 18f,
                fontWeight = "bold",
                opacity = 0.9f,
                animation = "glow"
            )
            LyricEmotion.NEUTRAL -> LyricsStyle(
                color = Color(0xFF2F4F4F),
                fontSize = 16f,
                fontWeight = "normal",
                opacity = 0.8f,
                animation = "none"
            )
        }
    }
    
    /**
     * Clear current lyrics
     */
    fun clearLyrics() {
        _currentLyrics.value = emptyList()
        _lyricsState.value = LyricsState()
    }
}

/**
 * Lyrics state data class
 */
data class LyricsState(
    val isLoading: Boolean = false,
    val hasLyrics: Boolean = false,
    val currentTrackId: String? = null,
    val currentLineIndex: Int = 0,
    val isVisible: Boolean = false
)

/**
 * Lyric line data class
 */
data class LyricLine(
    val text: String,
    val startTimeMs: Long,
    val emotion: LyricEmotion,
    val endTimeMs: Long = startTimeMs + 3000
)

/**
 * Lyric emotion enum
 */
enum class LyricEmotion {
    SAD, MELANCHOLIC, HAPPY, JOYFUL, EUPHORIC, CUTE, NOSTALGIC, HOPEFUL, NEUTRAL
}

/**
 * Lyrics display style
 */
data class LyricsStyle(
    val color: Color,
    val fontSize: Float,
    val fontWeight: String,
    val opacity: Float,
    val animation: String
)

/**
 * Lyrics display options
 */
data class LyricsDisplayOptions(
    val showEmotions: Boolean = true,
    val showTimestamps: Boolean = false,
    val autoScroll: Boolean = true,
    val highlightCurrent: Boolean = true,
    val showBackground: Boolean = true,
    val fontSize: Float = 16f,
    val animationSpeed: Float = 1.0f
)
