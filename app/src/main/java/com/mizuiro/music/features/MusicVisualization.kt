package com.mizuiro.music.features

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
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
 * Music Visualization Manager
 * 
 * Creates beautiful water blue audio visualizations
 * with Mizuiro aesthetic
 */
@Singleton
class MusicVisualization @Inject constructor() {
    
    private val _visualizationState = MutableStateFlow(VisualizationState())
    val visualizationState: StateFlow<VisualizationState> = _visualizationState.asStateFlow()
    
    private val _audioData = MutableStateFlow(AudioData())
    val audioData: StateFlow<AudioData> = _audioData.asStateFlow()
    
    /**
     * Update audio data for visualization
     */
    fun updateAudioData(amplitude: Float, frequency: Float, isPlaying: Boolean) {
        _audioData.value = AudioData(
            amplitude = amplitude,
            frequency = frequency,
            isPlaying = isPlaying,
            timestamp = System.currentTimeMillis()
        )
        
        _visualizationState.value = _visualizationState.value.copy(
            isActive = isPlaying,
            intensity = amplitude
        )
    }
    
    /**
     * Get visualization type based on audio data
     */
    fun getVisualizationType(audioData: AudioData): VisualizationType {
        return when {
            audioData.amplitude > 0.8f -> VisualizationType.WAVE
            audioData.amplitude > 0.5f -> VisualizationType.CIRCLES
            audioData.amplitude > 0.2f -> VisualizationType.PARTICLES
            else -> VisualizationType.STATIC
        }
    }
    
    /**
     * Get visualization colors based on theme
     */
    fun getVisualizationColors(theme: MizuiroTheme): VisualizationColors {
        return when (theme) {
            MizuiroTheme.DAWN -> VisualizationColors(
                primary = Color(0xFF87CEEB),
                secondary = Color(0xFFFFB6C1),
                accent = Color(0xFFFFE4E1),
                background = Color(0xFFF0F8FF)
            )
            MizuiroTheme.DAY -> VisualizationColors(
                primary = Color(0xFF4682B4),
                secondary = Color(0xFFFF69B4),
                accent = Color(0xFFFFF0F5),
                background = Color(0xFFE6F3FF)
            )
            MizuiroTheme.DUSK -> VisualizationColors(
                primary = Color(0xFF5F9EA0),
                secondary = Color(0xFFFF1493),
                accent = Color(0xFFFFEBCD),
                background = Color(0xFFF5F5DC)
            )
            MizuiroTheme.NIGHT -> VisualizationColors(
                primary = Color(0xFF2F4F4F),
                secondary = Color(0xFF9370DB),
                accent = Color(0xFFE6E6FA),
                background = Color(0xFF1C1C1C)
            )
        }
    }
}

/**
 * Music Visualization Composable
 */
@Composable
fun MusicVisualizationCanvas(
    modifier: Modifier = Modifier,
    audioData: AudioData,
    theme: MizuiroTheme,
    visualizationType: VisualizationType = VisualizationType.WAVE
) {
    val colors = remember(theme) {
        MusicVisualization().getVisualizationColors(theme)
    }
    
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
            .background(colors.background.copy(alpha = 0.1f))
    ) {
        when (visualizationType) {
            VisualizationType.WAVE -> drawWaveVisualization(
                audioData = audioData,
                colors = colors,
                animationPhase = animationPhase
            )
            VisualizationType.CIRCLES -> drawCircleVisualization(
                audioData = audioData,
                colors = colors,
                animationPhase = animationPhase
            )
            VisualizationType.PARTICLES -> drawParticleVisualization(
                audioData = audioData,
                colors = colors,
                animationPhase = animationPhase
            )
            VisualizationType.STATIC -> drawStaticVisualization(
                colors = colors,
                animationPhase = animationPhase
            )
        }
    }
}

/**
 * Draw wave visualization
 */
private fun DrawScope.drawWaveVisualization(
    audioData: AudioData,
    colors: VisualizationColors,
    animationPhase: Float
) {
    val centerY = size.height / 2
    val waveCount = 3
    val waveHeight = audioData.amplitude * 100f
    
    for (i in 0 until waveCount) {
        val waveColor = when (i) {
            0 -> colors.primary
            1 -> colors.secondary
            else -> colors.accent
        }
        
        val path = Path().apply {
            moveTo(0f, centerY)
            
            for (x in 0..size.width.toInt() step 4) {
                val y = centerY + 
                    (waveHeight * kotlin.math.sin(
                        (x * 0.02f + animationPhase * 2 * kotlin.math.PI + i * kotlin.math.PI / 3).toFloat()
                    )).toFloat()
                lineTo(x.toFloat(), y)
            }
        }
        
        drawPath(
            path = path,
            color = waveColor.copy(alpha = 0.6f),
            style = Stroke(width = 3f)
        )
    }
}

/**
 * Draw circle visualization
 */
private fun DrawScope.drawCircleVisualization(
    audioData: AudioData,
    colors: VisualizationColors,
    animationPhase: Float
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val maxRadius = kotlin.math.min(size.width, size.height) / 2
    val radius = maxRadius * audioData.amplitude
    
    // Draw concentric circles
    for (i in 0..2) {
        val circleRadius = radius * (1f - i * 0.3f)
        val circleColor = when (i) {
            0 -> colors.primary
            1 -> colors.secondary
            else -> colors.accent
        }
        
        drawCircle(
            color = circleColor.copy(alpha = 0.4f),
            radius = circleRadius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2f)
        )
    }
    
    // Draw pulsing center
    val pulseRadius = radius * 0.3f * (1f + kotlin.math.sin(animationPhase * 2 * kotlin.math.PI).toFloat())
    drawCircle(
        color = colors.primary.copy(alpha = 0.8f),
        radius = pulseRadius,
        center = Offset(centerX, centerY)
    )
}

/**
 * Draw particle visualization
 */
private fun DrawScope.drawParticleVisualization(
    audioData: AudioData,
    colors: VisualizationColors,
    animationPhase: Float
) {
    val particleCount = (audioData.amplitude * 50).toInt().coerceAtLeast(5)
    
    repeat(particleCount) { i ->
        val x = (size.width * (i.toFloat() / particleCount) + 
                animationPhase * 100f) % size.width
        val y = size.height / 2 + 
                kotlin.math.sin(animationPhase * 2 * kotlin.math.PI + i) * 50f
        
        val particleSize = (audioData.amplitude * 8f).coerceAtLeast(2f)
        val particleColor = when (i % 3) {
            0 -> colors.primary
            1 -> colors.secondary
            else -> colors.accent
        }
        
        drawCircle(
            color = particleColor.copy(alpha = 0.7f),
            radius = particleSize,
            center = Offset(x.toFloat(), y.toFloat())
        )
    }
}

/**
 * Draw static visualization
 */
private fun DrawScope.drawStaticVisualization(
    colors: VisualizationColors,
    animationPhase: Float
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    // Draw gentle breathing circle
    val breathRadius = 50f * (1f + kotlin.math.sin(animationPhase * kotlin.math.PI).toFloat() * 0.3f)
    
    drawCircle(
        color = colors.primary.copy(alpha = 0.3f),
        radius = breathRadius,
        center = Offset(centerX, centerY),
        style = Stroke(width = 2f)
    )
}

/**
 * Visualization state data class
 */
data class VisualizationState(
    val isActive: Boolean = false,
    val intensity: Float = 0f,
    val type: VisualizationType = VisualizationType.STATIC
)

/**
 * Audio data for visualization
 */
data class AudioData(
    val amplitude: Float = 0f,
    val frequency: Float = 0f,
    val isPlaying: Boolean = false,
    val timestamp: Long = 0L
)

/**
 * Visualization type enum
 */
enum class VisualizationType {
    WAVE, CIRCLES, PARTICLES, STATIC
}

/**
 * Visualization colors
 */
data class VisualizationColors(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color
)

/**
 * Particle data for advanced visualizations
 */
data class Particle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val size: Float,
    val color: Color,
    val life: Float
)

/**
 * Wave data for wave visualizations
 */
data class WaveData(
    val frequency: Float,
    val amplitude: Float,
    val phase: Float,
    val color: Color
)
