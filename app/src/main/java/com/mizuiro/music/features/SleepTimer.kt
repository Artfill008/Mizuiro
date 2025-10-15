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
 * Sleep Timer Manager
 * 
 * Provides gentle fade-out with kawaii bedtime animations
 * for peaceful sleep
 */
@Singleton
class SleepTimer @Inject constructor() {
    
    private val _sleepTimerState = MutableStateFlow(SleepTimerState())
    val sleepTimerState: StateFlow<SleepTimerState> = _sleepTimerState.asStateFlow()
    
    private val _bedtimeAnimation = MutableStateFlow(BedtimeAnimation.IDLE)
    val bedtimeAnimation: StateFlow<BedtimeAnimation> = _bedtimeAnimation.asStateFlow()
    
    /**
     * Start sleep timer
     */
    fun startSleepTimer(durationMinutes: Int, fadeType: FadeType = FadeType.GENTLE) {
        val endTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        
        _sleepTimerState.value = SleepTimerState(
            isActive = true,
            durationMinutes = durationMinutes,
            remainingMinutes = durationMinutes,
            endTime = endTime,
            fadeType = fadeType,
            currentVolume = 1.0f
        )
        
        _bedtimeAnimation.value = BedtimeAnimation.STARTING
    }
    
    /**
     * Update sleep timer
     */
    fun updateSleepTimer() {
        val currentState = _sleepTimerState.value
        if (!currentState.isActive) return
        
        val now = System.currentTimeMillis()
        val remainingMs = currentState.endTime - now
        
        if (remainingMs <= 0) {
            // Timer finished
            _sleepTimerState.value = currentState.copy(
                isActive = false,
                remainingMinutes = 0,
                currentVolume = 0f
            )
            _bedtimeAnimation.value = BedtimeAnimation.FINISHED
            return
        }
        
        val remainingMinutes = (remainingMs / (60 * 1000)).toInt()
        val fadeProgress = 1f - (remainingMs.toFloat() / (currentState.durationMinutes * 60 * 1000f))
        
        val newVolume = calculateVolume(fadeProgress, currentState.fadeType)
        
        _sleepTimerState.value = currentState.copy(
            remainingMinutes = remainingMinutes,
            currentVolume = newVolume,
            fadeProgress = fadeProgress
        )
        
        // Update animation based on progress
        _bedtimeAnimation.value = when {
            fadeProgress < 0.2f -> BedtimeAnimation.STARTING
            fadeProgress < 0.5f -> BedtimeAnimation.FADING
            fadeProgress < 0.8f -> BedtimeAnimation.SLEEPY
            else -> BedtimeAnimation.DREAMING
        }
    }
    
    /**
     * Calculate volume based on fade progress and type
     */
    private fun calculateVolume(fadeProgress: Float, fadeType: FadeType): Float {
        return when (fadeType) {
            FadeType.GENTLE -> {
                val smoothFade = 1f - (fadeProgress * fadeProgress)
                smoothFade.coerceAtLeast(0f)
            }
            FadeType.LINEAR -> {
                (1f - fadeProgress).coerceAtLeast(0f)
            }
            FadeType.EXPONENTIAL -> {
                val expFade = kotlin.math.exp(-fadeProgress * 3f).toFloat()
                expFade.coerceAtLeast(0f)
            }
            FadeType.STEPPED -> {
                when {
                    fadeProgress < 0.5f -> 1f
                    fadeProgress < 0.7f -> 0.7f
                    fadeProgress < 0.9f -> 0.3f
                    else -> 0f
                }
            }
        }
    }
    
    /**
     * Stop sleep timer
     */
    fun stopSleepTimer() {
        _sleepTimerState.value = SleepTimerState()
        _bedtimeAnimation.value = BedtimeAnimation.IDLE
    }
    
    /**
     * Get sleep timer display text
     */
    fun getSleepTimerDisplay(): String {
        val state = _sleepTimerState.value
        if (!state.isActive) return "Sleep Timer Off"
        
        val hours = state.remainingMinutes / 60
        val minutes = state.remainingMinutes % 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }
    
    /**
     * Get bedtime message
     */
    fun getBedtimeMessage(): String {
        val animation = _bedtimeAnimation.value
        return when (animation) {
            BedtimeAnimation.IDLE -> "Sweet dreams await"
            BedtimeAnimation.STARTING -> "Getting ready for bed"
            BedtimeAnimation.FADING -> "Music is gently fading"
            BedtimeAnimation.SLEEPY -> "Feeling sleepy yet?"
            BedtimeAnimation.DREAMING -> "Drifting into dreams"
            BedtimeAnimation.FINISHED -> "Good night, sleep well"
        }
    }
    
    /**
     * Get bedtime emoji
     */
    fun getBedtimeEmoji(): String {
        val animation = _bedtimeAnimation.value
        return when (animation) {
            BedtimeAnimation.IDLE -> "🌙"
            BedtimeAnimation.STARTING -> "😴"
            BedtimeAnimation.FADING -> "💤"
            BedtimeAnimation.SLEEPY -> "😪"
            BedtimeAnimation.DREAMING -> "✨"
            BedtimeAnimation.FINISHED -> "🌙"
        }
    }
}

/**
 * Sleep Timer Canvas Composable
 */
@Composable
fun SleepTimerCanvas(
    modifier: Modifier = Modifier,
    sleepTimerState: SleepTimerState,
    bedtimeAnimation: BedtimeAnimation
) {
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
        drawBedtimeAnimation(
            sleepTimerState = sleepTimerState,
            bedtimeAnimation = bedtimeAnimation,
            animationPhase = animationPhase
        )
    }
}

/**
 * Draw bedtime animation
 */
private fun DrawScope.drawBedtimeAnimation(
    sleepTimerState: SleepTimerState,
    bedtimeAnimation: BedtimeAnimation,
    animationPhase: Float
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    when (bedtimeAnimation) {
        BedtimeAnimation.IDLE -> {
            // Draw gentle moon
            drawMoon(centerX, centerY, animationPhase)
        }
        BedtimeAnimation.STARTING -> {
            // Draw stars appearing
            drawStars(centerX, centerY, animationPhase)
            drawMoon(centerX, centerY, animationPhase)
        }
        BedtimeAnimation.FADING -> {
            // Draw music notes fading
            drawFadingNotes(centerX, centerY, animationPhase)
            drawStars(centerX, centerY, animationPhase)
        }
        BedtimeAnimation.SLEEPY -> {
            // Draw sleepy eyes
            drawSleepyEyes(centerX, centerY, animationPhase)
            drawStars(centerX, centerY, animationPhase)
        }
        BedtimeAnimation.DREAMING -> {
            // Draw dream bubbles
            drawDreamBubbles(centerX, centerY, animationPhase)
            drawStars(centerX, centerY, animationPhase)
        }
        BedtimeAnimation.FINISHED -> {
            // Draw peaceful scene
            drawPeacefulScene(centerX, centerY, animationPhase)
        }
    }
}

/**
 * Draw moon
 */
private fun DrawScope.drawMoon(centerX: Float, centerY: Float, animationPhase: Float) {
    val moonY = centerY + kotlin.math.sin(animationPhase * 2 * kotlin.math.PI).toFloat() * 10f
    val moonSize = 40f
    
    drawCircle(
        color = Color(0xFFFFF8DC).copy(alpha = 0.8f),
        radius = moonSize,
        center = Offset(centerX, moonY)
    )
    
    // Draw moon craters
    drawCircle(
        color = Color(0xFFE6E6FA).copy(alpha = 0.6f),
        radius = moonSize * 0.3f,
        center = Offset(centerX - moonSize * 0.3f, moonY - moonSize * 0.2f)
    )
}

/**
 * Draw stars
 */
private fun DrawScope.drawStars(centerX: Float, centerY: Float, animationPhase: Float) {
    val starCount = 20
    val starSize = 3f
    
    repeat(starCount) { i ->
        val angle = (i * 2 * kotlin.math.PI / starCount) + animationPhase * kotlin.math.PI
        val radius = 100f + kotlin.math.sin(animationPhase * 2 * kotlin.math.PI + i) * 20f
        val x = centerX + kotlin.math.cos(angle).toFloat() * radius
        val y = centerY + kotlin.math.sin(angle).toFloat() * radius
        
        val twinkle = kotlin.math.sin(animationPhase * 4 * kotlin.math.PI + i).toFloat()
        val alpha = (0.5f + twinkle * 0.5f).coerceIn(0f, 1f)
        
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = starSize,
            center = Offset(x, y)
        )
    }
}

/**
 * Draw fading music notes
 */
private fun DrawScope.drawFadingNotes(centerX: Float, centerY: Float, animationPhase: Float) {
    val noteCount = 5
    val noteSize = 8f
    
    repeat(noteCount) { i ->
        val x = centerX + (i - noteCount / 2) * 30f
        val y = centerY + kotlin.math.sin(animationPhase * 2 * kotlin.math.PI + i) * 20f
        
        val fadeAlpha = (1f - animationPhase).coerceAtLeast(0f)
        
        drawCircle(
            color = Color(0xFF87CEEB).copy(alpha = fadeAlpha * 0.7f),
            radius = noteSize,
            center = Offset(x, y)
        )
    }
}

/**
 * Draw sleepy eyes
 */
private fun DrawScope.drawSleepyEyes(centerX: Float, centerY: Float, animationPhase: Float) {
    val eyeY = centerY + kotlin.math.sin(animationPhase * 2 * kotlin.math.PI).toFloat() * 5f
    val eyeSize = 15f
    
    // Left eye
    drawCircle(
        color = Color(0xFF2F4F4F).copy(alpha = 0.8f),
        radius = eyeSize,
        center = Offset(centerX - 20f, eyeY)
    )
    
    // Right eye
    drawCircle(
        color = Color(0xFF2F4F4F).copy(alpha = 0.8f),
        radius = eyeSize,
        center = Offset(centerX + 20f, eyeY)
    )
    
    // Sleepy lines
    val lineY = eyeY + eyeSize + 5f
    drawLine(
        color = Color(0xFF2F4F4F).copy(alpha = 0.6f),
        start = Offset(centerX - 30f, lineY),
        end = Offset(centerX + 30f, lineY),
        strokeWidth = 2f
    )
}

/**
 * Draw dream bubbles
 */
private fun DrawScope.drawDreamBubbles(centerX: Float, centerY: Float, animationPhase: Float) {
    val bubbleCount = 8
    val bubbleSize = 12f
    
    repeat(bubbleCount) { i ->
        val angle = (i * 2 * kotlin.math.PI / bubbleCount) + animationPhase * kotlin.math.PI
        val radius = 80f + kotlin.math.sin(animationPhase * 2 * kotlin.math.PI + i) * 30f
        val x = centerX + kotlin.math.cos(angle).toFloat() * radius
        val y = centerY + kotlin.math.sin(angle).toFloat() * radius
        
        val bubbleAlpha = (0.3f + kotlin.math.sin(animationPhase * 3 * kotlin.math.PI + i).toFloat() * 0.4f).coerceIn(0f, 1f)
        
        drawCircle(
            color = Color(0xFFE6E6FA).copy(alpha = bubbleAlpha),
            radius = bubbleSize,
            center = Offset(x, y),
            style = Stroke(width = 2f)
        )
    }
}

/**
 * Draw peaceful scene
 */
private fun DrawScope.drawPeacefulScene(centerX: Float, centerY: Float, animationPhase: Float) {
    // Draw gentle waves
    val waveY = centerY + 50f
    val waveHeight = 20f
    
    val path = Path().apply {
        moveTo(0f, waveY)
        
        for (x in 0..size.width.toInt() step 4) {
            val y = waveY + kotlin.math.sin(
                (x * 0.02f + animationPhase * 2 * kotlin.math.PI)
            ).toFloat() * waveHeight
            lineTo(x.toFloat(), y)
        }
    }
    
    drawPath(
        path = path,
        color = Color(0xFF87CEEB).copy(alpha = 0.6f),
        style = Stroke(width = 3f)
    )
    
    // Draw moon
    drawMoon(centerX, centerY - 50f, animationPhase)
}

/**
 * Sleep timer state data class
 */
data class SleepTimerState(
    val isActive: Boolean = false,
    val durationMinutes: Int = 0,
    val remainingMinutes: Int = 0,
    val endTime: Long = 0L,
    val fadeType: FadeType = FadeType.GENTLE,
    val currentVolume: Float = 1.0f,
    val fadeProgress: Float = 0f
)

/**
 * Fade type enum
 */
enum class FadeType {
    GENTLE, LINEAR, EXPONENTIAL, STEPPED
}

/**
 * Bedtime animation enum
 */
enum class BedtimeAnimation {
    IDLE, STARTING, FADING, SLEEPY, DREAMING, FINISHED
}
