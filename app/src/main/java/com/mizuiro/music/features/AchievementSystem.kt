package com.mizuiro.music.features

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Achievement System
 * 
 * Creates kawaii badges for listening milestones
 * and music journey achievements
 */
@Singleton
class AchievementSystem @Inject constructor() {
    
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()
    
    private val _unlockedAchievements = MutableStateFlow<List<Achievement>>(emptyList())
    val unlockedAchievements: StateFlow<List<Achievement>> = _unlockedAchievements.asStateFlow()
    
    private val _achievementStats = MutableStateFlow(AchievementStats())
    val achievementStats: StateFlow<AchievementStats> = _achievementStats.asStateFlow()
    
    init {
        initializeAchievements()
    }
    
    /**
     * Initialize all available achievements
     */
    private fun initializeAchievements() {
        val allAchievements = listOf(
            // Listening Milestones
            Achievement(
                id = "first_song",
                title = "First Steps",
                description = "Listen to your first song",
                emoji = "🎵",
                category = AchievementCategory.LISTENING,
                rarity = AchievementRarity.COMMON,
                requirement = 1,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            Achievement(
                id = "song_10",
                title = "Music Explorer",
                description = "Listen to 10 songs",
                emoji = "🎶",
                category = AchievementCategory.LISTENING,
                rarity = AchievementRarity.COMMON,
                requirement = 10,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            Achievement(
                id = "song_100",
                title = "Melody Master",
                description = "Listen to 100 songs",
                emoji = "🎼",
                category = AchievementCategory.LISTENING,
                rarity = AchievementRarity.RARE,
                requirement = 100,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            Achievement(
                id = "song_1000",
                title = "Music Legend",
                description = "Listen to 1000 songs",
                emoji = "👑",
                category = AchievementCategory.LISTENING,
                rarity = AchievementRarity.LEGENDARY,
                requirement = 1000,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            
            // Time-based Achievements
            Achievement(
                id = "listen_1_hour",
                title = "Hour of Harmony",
                description = "Listen for 1 hour straight",
                emoji = "⏰",
                category = AchievementCategory.TIME,
                rarity = AchievementRarity.COMMON,
                requirement = 60,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            Achievement(
                id = "listen_24_hours",
                title = "Day of Dreams",
                description = "Listen for 24 hours total",
                emoji = "🌅",
                category = AchievementCategory.TIME,
                rarity = AchievementRarity.RARE,
                requirement = 1440,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            
            // Discovery Achievements
            Achievement(
                id = "discover_10_artists",
                title = "Artist Adventurer",
                description = "Discover 10 new artists",
                emoji = "🎨",
                category = AchievementCategory.DISCOVERY,
                rarity = AchievementRarity.COMMON,
                requirement = 10,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            Achievement(
                id = "discover_50_artists",
                title = "Cultural Explorer",
                description = "Discover 50 new artists",
                emoji = "🌍",
                category = AchievementCategory.DISCOVERY,
                rarity = AchievementRarity.RARE,
                requirement = 50,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            
            // Mood Achievements
            Achievement(
                id = "sad_music_10",
                title = "Melancholy Maven",
                description = "Listen to 10 sad songs",
                emoji = "💙",
                category = AchievementCategory.MOOD,
                rarity = AchievementRarity.COMMON,
                requirement = 10,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            Achievement(
                id = "happy_music_10",
                title = "Joy Juggler",
                description = "Listen to 10 happy songs",
                emoji = "😊",
                category = AchievementCategory.MOOD,
                rarity = AchievementRarity.COMMON,
                requirement = 10,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            
            // Special Achievements
            Achievement(
                id = "midnight_listener",
                title = "Midnight Melodies",
                description = "Listen at midnight",
                emoji = "🌙",
                category = AchievementCategory.SPECIAL,
                rarity = AchievementRarity.RARE,
                requirement = 1,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            Achievement(
                id = "rainy_day_music",
                title = "Rainy Day Vibes",
                description = "Listen during rain",
                emoji = "🌧️",
                category = AchievementCategory.SPECIAL,
                rarity = AchievementRarity.RARE,
                requirement = 1,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            ),
            Achievement(
                id = "perfect_playlist",
                title = "Playlist Perfectionist",
                description = "Create a perfect playlist",
                emoji = "✨",
                category = AchievementCategory.SPECIAL,
                rarity = AchievementRarity.LEGENDARY,
                requirement = 1,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null
            )
        )
        
        _achievements.value = allAchievements
    }
    
    /**
     * Update achievement progress
     */
    fun updateAchievementProgress(achievementId: String, progress: Int) {
        val currentAchievements = _achievements.value.toMutableList()
        val achievementIndex = currentAchievements.indexOfFirst { it.id == achievementId }
        
        if (achievementIndex != -1) {
            val achievement = currentAchievements[achievementIndex]
            val updatedAchievement = achievement.copy(
                currentProgress = achievement.currentProgress + progress
            )
            
            currentAchievements[achievementIndex] = updatedAchievement
            
            // Check if achievement is unlocked
            if (!updatedAchievement.isUnlocked && updatedAchievement.currentProgress >= updatedAchievement.requirement) {
                val unlockedAchievement = updatedAchievement.copy(
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
                currentAchievements[achievementIndex] = unlockedAchievement
                
                // Add to unlocked achievements
                val currentUnlocked = _unlockedAchievements.value.toMutableList()
                currentUnlocked.add(unlockedAchievement)
                _unlockedAchievements.value = currentUnlocked
                
                // Update stats
                updateAchievementStats()
            }
            
            _achievements.value = currentAchievements
        }
    }
    
    /**
     * Update achievement statistics
     */
    private fun updateAchievementStats() {
        val allAchievements = _achievements.value
        val unlockedAchievements = _unlockedAchievements.value
        
        _achievementStats.value = AchievementStats(
            totalAchievements = allAchievements.size,
            unlockedAchievements = unlockedAchievements.size,
            commonUnlocked = unlockedAchievements.count { it.rarity == AchievementRarity.COMMON },
            rareUnlocked = unlockedAchievements.count { it.rarity == AchievementRarity.RARE },
            legendaryUnlocked = unlockedAchievements.count { it.rarity == AchievementRarity.LEGENDARY },
            completionPercentage = (unlockedAchievements.size.toFloat() / allAchievements.size * 100).toInt()
        )
    }
    
    /**
     * Get achievements by category
     */
    fun getAchievementsByCategory(category: AchievementCategory): List<Achievement> {
        return _achievements.value.filter { it.category == category }
    }
    
    /**
     * Get achievements by rarity
     */
    fun getAchievementsByRarity(rarity: AchievementRarity): List<Achievement> {
        return _achievements.value.filter { it.rarity == rarity }
    }
    
    /**
     * Get recent achievements
     */
    fun getRecentAchievements(limit: Int = 5): List<Achievement> {
        return _unlockedAchievements.value
            .sortedByDescending { it.unlockedAt }
            .take(limit)
    }
    
    /**
     * Get achievement progress percentage
     */
    fun getAchievementProgress(achievement: Achievement): Float {
        return (achievement.currentProgress.toFloat() / achievement.requirement * 100).coerceAtMost(100f)
    }
    
    /**
     * Get achievement color based on rarity
     */
    fun getAchievementColor(rarity: AchievementRarity): Color {
        return when (rarity) {
            AchievementRarity.COMMON -> Color(0xFF87CEEB)
            AchievementRarity.RARE -> Color(0xFFFF69B4)
            AchievementRarity.LEGENDARY -> Color(0xFFFFD700)
        }
    }
    
    /**
     * Get achievement glow color
     */
    fun getAchievementGlowColor(rarity: AchievementRarity): Color {
        return when (rarity) {
            AchievementRarity.COMMON -> Color(0xFF87CEEB).copy(alpha = 0.3f)
            AchievementRarity.RARE -> Color(0xFFFF69B4).copy(alpha = 0.4f)
            AchievementRarity.LEGENDARY -> Color(0xFFFFD700).copy(alpha = 0.5f)
        }
    }
}

/**
 * Achievement Canvas Composable
 */
@Composable
fun AchievementCanvas(
    modifier: Modifier = Modifier,
    achievement: Achievement,
    isUnlocked: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animationPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        drawAchievement(
            achievement = achievement,
            isUnlocked = isUnlocked,
            animationPhase = animationPhase
        )
    }
}

/**
 * Draw achievement badge
 */
private fun DrawScope.drawAchievement(
    achievement: Achievement,
    isUnlocked: Boolean,
    animationPhase: Float
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val badgeSize = 60f
    
    val achievementSystem = AchievementSystem()
    val color = achievementSystem.getAchievementColor(achievement.rarity)
    val glowColor = achievementSystem.getAchievementGlowColor(achievement.rarity)
    
    // Draw glow effect for unlocked achievements
    if (isUnlocked) {
        val glowSize = badgeSize * (1.2f + kotlin.math.sin(animationPhase * 2 * kotlin.math.PI).toFloat() * 0.1f)
        drawCircle(
            color = glowColor,
            radius = glowSize,
            center = Offset(centerX, centerY)
        )
    }
    
    // Draw achievement badge
    drawCircle(
        color = color.copy(alpha = if (isUnlocked) 1f else 0.6f),
        radius = badgeSize,
        center = Offset(centerX, centerY)
    )
    
    // Draw achievement border
    drawCircle(
        color = Color.White.copy(alpha = 0.8f),
        radius = badgeSize,
        center = Offset(centerX, centerY),
        style = Stroke(width = 3f)
    )
    
    // Draw achievement emoji (simplified as text)
    val emojiSize = badgeSize * 0.6f
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = emojiSize,
        center = Offset(centerX, centerY)
    )
    
    // Draw progress ring for locked achievements
    if (!isUnlocked) {
        val progress = achievement.currentProgress.toFloat() / achievement.requirement
        val startAngle = -90f
        val sweepAngle = 360f * progress
        
        drawArc(
            color = color.copy(alpha = 0.8f),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(centerX - badgeSize, centerY - badgeSize),
            size = androidx.compose.ui.geometry.Size(badgeSize * 2, badgeSize * 2),
            style = Stroke(width = 4f)
        )
    }
}

/**
 * Achievement data class
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val category: AchievementCategory,
    val rarity: AchievementRarity,
    val requirement: Int,
    val currentProgress: Int,
    val isUnlocked: Boolean,
    val unlockedAt: Long?
)

/**
 * Achievement category enum
 */
enum class AchievementCategory {
    LISTENING, TIME, DISCOVERY, MOOD, SPECIAL
}

/**
 * Achievement rarity enum
 */
enum class AchievementRarity {
    COMMON, RARE, LEGENDARY
}

/**
 * Achievement statistics data class
 */
data class AchievementStats(
    val totalAchievements: Int = 0,
    val unlockedAchievements: Int = 0,
    val commonUnlocked: Int = 0,
    val rareUnlocked: Int = 0,
    val legendaryUnlocked: Int = 0,
    val completionPercentage: Int = 0
)
