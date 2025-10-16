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
 * Digital Pet Manager
 * 
 * Manages a kawaii mascot that evolves with music taste
 * and provides emotional companionship
 */
@Singleton
class DigitalPet @Inject constructor() {
    
    private val _petState = MutableStateFlow(PetState())
    val petState: StateFlow<PetState> = _petState.asStateFlow()
    
    private val _petData = MutableStateFlow(PetData())
    val petData: StateFlow<PetData> = _petData.asStateFlow()
    
    /**
     * Update pet based on music listening
     */
    fun updatePet(musicData: MusicListeningData) {
        val currentPet = _petData.value
        val newPet = currentPet.copy(
            happiness = (currentPet.happiness + musicData.happinessBoost).coerceIn(0f, 100f),
            energy = (currentPet.energy + musicData.energyBoost).coerceIn(0f, 100f),
            musicTaste = updateMusicTaste(currentPet.musicTaste, musicData),
            level = calculateLevel(currentPet),
            lastInteraction = System.currentTimeMillis()
        )
        
        _petData.value = newPet
        _petState.value = PetState(
            mood = calculateMood(newPet),
            isVisible = true,
            animation = getPetAnimation(newPet)
        )
    }
    
    /**
     * Update music taste based on listening data
     */
    private fun updateMusicTaste(currentTaste: MusicTaste, musicData: MusicListeningData): MusicTaste {
        return currentTaste.copy(
            sadSongs = currentTaste.sadSongs + musicData.sadSongs,
            happySongs = currentTaste.happySongs + musicData.happySongs,
            energeticSongs = currentTaste.energeticSongs + musicData.energeticSongs,
            calmSongs = currentTaste.calmSongs + musicData.calmSongs,
            totalSongs = currentTaste.totalSongs + 1
        )
    }
    
    /**
     * Calculate pet level based on data
     */
    private fun calculateLevel(petData: PetData): Int {
        val totalSongs = petData.musicTaste.totalSongs
        return when {
            totalSongs >= 1000 -> 10
            totalSongs >= 500 -> 9
            totalSongs >= 250 -> 8
            totalSongs >= 100 -> 7
            totalSongs >= 50 -> 6
            totalSongs >= 25 -> 5
            totalSongs >= 10 -> 4
            totalSongs >= 5 -> 3
            totalSongs >= 2 -> 2
            else -> 1
        }
    }
    
    /**
     * Calculate pet mood based on data
     */
    private fun calculateMood(petData: PetData): PetMood {
        val happiness = petData.happiness
        val energy = petData.energy
        
        return when {
            happiness >= 80 && energy >= 80 -> PetMood.EUPHORIC
            happiness >= 60 && energy >= 60 -> PetMood.HAPPY
            happiness >= 40 && energy >= 40 -> PetMood.CONTENT
            happiness >= 20 && energy >= 20 -> PetMood.SAD
            else -> PetMood.MELANCHOLIC
        }
    }
    
    /**
     * Get pet animation based on mood and level
     */
    private fun getPetAnimation(petData: PetData): PetAnimation {
        return when (petData.level) {
            in 1..3 -> PetAnimation.BABY
            in 4..6 -> PetAnimation.CHILD
            in 7..8 -> PetAnimation.TEEN
            else -> PetAnimation.ADULT
        }
    }
    
    /**
     * Feed the pet
     */
    fun feedPet() {
        val currentPet = _petData.value
        _petData.value = currentPet.copy(
            happiness = (currentPet.happiness + 10f).coerceAtMost(100f),
            energy = (currentPet.energy + 5f).coerceAtMost(100f),
            lastInteraction = System.currentTimeMillis()
        )
    }
    
    /**
     * Play with the pet
     */
    fun playWithPet() {
        val currentPet = _petData.value
        _petData.value = currentPet.copy(
            happiness = (currentPet.happiness + 15f).coerceAtMost(100f),
            energy = (currentPet.energy - 10f).coerceAtLeast(0f),
            lastInteraction = System.currentTimeMillis()
        )
    }
    
    /**
     * Get pet appearance based on level and mood
     */
    fun getPetAppearance(petData: PetData): PetAppearance {
        val level = petData.level
        val mood = petData.mood
        
        return PetAppearance(
            emoji = getPetEmoji(level, mood),
            color = getPetColor(mood),
            size = getPetSize(level),
            accessories = getPetAccessories(level, mood)
        )
    }
    
    /**
     * Get pet emoji based on level and mood
     */
    private fun getPetEmoji(level: Int, mood: PetMood): String {
        val baseEmoji = when (level) {
            in 1..3 -> "🐣"
            in 4..6 -> "🐥"
            in 7..8 -> "🐤"
            else -> "🐦"
        }
        
        return when (mood) {
            PetMood.EUPHORIC -> "✨$baseEmoji✨"
            PetMood.HAPPY -> "😊$baseEmoji"
            PetMood.CONTENT -> baseEmoji
            PetMood.SAD -> "😢$baseEmoji"
            PetMood.MELANCHOLIC -> "💙$baseEmoji"
        }
    }
    
    /**
     * Get pet color based on mood
     */
    private fun getPetColor(mood: PetMood): Color {
        return when (mood) {
            PetMood.EUPHORIC -> Color(0xFFFF69B4)
            PetMood.HAPPY -> Color(0xFFFFB6C1)
            PetMood.CONTENT -> Color(0xFF87CEEB)
            PetMood.SAD -> Color(0xFF4682B4)
            PetMood.MELANCHOLIC -> Color(0xFF2F4F4F)
        }
    }
    
    /**
     * Get pet size based on level
     */
    private fun getPetSize(level: Int): Float {
        return when (level) {
            in 1..3 -> 0.5f
            in 4..6 -> 0.7f
            in 7..8 -> 0.9f
            else -> 1.0f
        }
    }
    
    /**
     * Get pet accessories based on level and mood
     */
    private fun getPetAccessories(level: Int, mood: PetMood): List<String> {
        val accessories = mutableListOf<String>()
        
        if (level >= 5) accessories.add("🎵")
        if (level >= 7) accessories.add("🎧")
        if (level >= 9) accessories.add("👑")
        
        when (mood) {
            PetMood.EUPHORIC -> accessories.add("✨")
            PetMood.HAPPY -> accessories.add("😊")
            PetMood.SAD -> accessories.add("💧")
            PetMood.MELANCHOLIC -> accessories.add("🌙")
            else -> {}
        }
        
        return accessories
    }
}

/**
 * Digital Pet Canvas Composable
 */
@Composable
fun DigitalPetCanvas(
    modifier: Modifier = Modifier,
    petData: PetData,
    petState: PetState
) {
    val appearance = remember(petData.level, petData.mood) {
        DigitalPet().getPetAppearance(petData)
    }
    
    val infiniteTransition = rememberInfiniteTransition()
    val animationPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        drawPet(
            petData = petData,
            appearance = appearance,
            animationPhase = animationPhase
        )
    }
}

/**
 * Draw the digital pet
 */
private fun DrawScope.drawPet(
    petData: PetData,
    appearance: PetAppearance,
    animationPhase: Float
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val petSize = appearance.size * 50f
    
    // Draw pet body
    val bodyY = centerY + kotlin.math.sin(animationPhase * 2 * kotlin.math.PI).toFloat() * 5f
    drawCircle(
        color = appearance.color.copy(alpha = 0.8f),
        radius = petSize,
        center = Offset(centerX, bodyY)
    )
    
    // Draw pet eyes
    val eyeY = bodyY - petSize * 0.3f
    drawCircle(
        color = Color.White,
        radius = petSize * 0.1f,
        center = Offset(centerX - petSize * 0.2f, eyeY)
    )
    drawCircle(
        color = Color.White,
        radius = petSize * 0.1f,
        center = Offset(centerX + petSize * 0.2f, eyeY)
    )
    
    // Draw pet accessories
    appearance.accessories.forEachIndexed { index, accessory ->
        val accessoryY = bodyY - petSize * (1.2f + index * 0.3f)
        // Draw accessory (simplified as small circles)
        drawCircle(
            color = appearance.color.copy(alpha = 0.6f),
            radius = petSize * 0.05f,
            center = Offset(centerX, accessoryY)
        )
    }
}

/**
 * Pet state data class
 */
data class PetState(
    val mood: PetMood = PetMood.CONTENT,
    val isVisible: Boolean = true,
    val animation: PetAnimation = PetAnimation.BABY
)

/**
 * Pet data class
 */
data class PetData(
    val name: String = "Mizuiro",
    val level: Int = 1,
    val happiness: Float = 50f,
    val energy: Float = 50f,
    val musicTaste: MusicTaste = MusicTaste(),
    val lastInteraction: Long = System.currentTimeMillis()
)

/**
 * Music taste data class
 */
data class MusicTaste(
    val sadSongs: Int = 0,
    val happySongs: Int = 0,
    val energeticSongs: Int = 0,
    val calmSongs: Int = 0,
    val totalSongs: Int = 0
)

/**
 * Pet mood enum
 */
enum class PetMood {
    EUPHORIC, HAPPY, CONTENT, SAD, MELANCHOLIC
}

/**
 * Pet animation enum
 */
enum class PetAnimation {
    BABY, CHILD, TEEN, ADULT
}

/**
 * Pet appearance data class
 */
data class PetAppearance(
    val emoji: String,
    val color: Color,
    val size: Float,
    val accessories: List<String>
)

/**
 * Music listening data for pet updates
 */
data class MusicListeningData(
    val happinessBoost: Float = 0f,
    val energyBoost: Float = 0f,
    val sadSongs: Int = 0,
    val happySongs: Int = 0,
    val energeticSongs: Int = 0,
    val calmSongs: Int = 0
)
