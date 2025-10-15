package com.mizuiro.music.ui.components.decorations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mizuiro.music.ui.theme.BlushPink
import com.mizuiro.music.ui.theme.CoolWhite
import com.mizuiro.music.ui.theme.FadedBlack
import com.mizuiro.music.ui.theme.MizuiroBase
import com.mizuiro.music.ui.theme.MizuiroLight
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Kawaii Mascot Component
 * 
 * A cute, pixelated mascot character that embodies the Mizuiro aesthetic.
 * Features:
 * - Cinnamoroll-inspired design
 * - Multiple animation states
 * - Low frame rate for pixelated feel
 * - Used in loading states, empty states, and decorations
 */

enum class MascotState {
    Idle,       // Default state with blinking
    Happy,      // Excited state
    Sad,        // Disappointed state
    Working,    // Busy/loading state
    Dancing,    // Celebrating state
    Sleeping,   // Resting state
    Curious,    // Questioning state
    Dead,       // Error state
    Excited,    // Overjoyed state
    Tired,      // Exhausted state
    Listening   // Attentive state
}

@Composable
fun KawaiiMascot(
    state: MascotState = MascotState.Idle,
    size: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_animation")
    
    // Blinking animation
    val blink by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )
    
    // Breathing animation
    val breathe by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    
    // Dancing animation
    val dance by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "dance"
    )
    
    Canvas(
        modifier = modifier.size(size)
    ) {
        val center = size.center
        val radius = size.minDimension / 2f
        val scale = when (state) {
            MascotState.Dancing -> 1f + sin(dance * 2 * PI.toFloat()) * 0.1f
            MascotState.Excited -> 1f + sin(dance * 4 * PI.toFloat()) * 0.05f
            else -> breathe
        }
        
        drawMascot(
            center = center,
            radius = radius * scale,
            state = state,
            blink = blink
        )
    }
}

private fun DrawScope.drawMascot(
    center: Offset,
    radius: Float,
    state: MascotState,
    blink: Float
) {
    // Main body (white circle)
    drawCircle(
        color = CoolWhite,
        radius = radius,
        center = center
    )
    
    // Body outline
    drawCircle(
        color = FadedBlack,
        radius = radius,
        center = center,
        style = Stroke(width = 2f)
    )
    
    // Ears
    val earRadius = radius * 0.4f
    val earOffset = radius * 0.6f
    
    // Left ear
    drawCircle(
        color = CoolWhite,
        radius = earRadius,
        center = Offset(center.x - earOffset, center.y - earOffset)
    )
    drawCircle(
        color = MizuiroBase,
        radius = earRadius * 0.6f,
        center = Offset(center.x - earOffset, center.y - earOffset)
    )
    
    // Right ear
    drawCircle(
        color = CoolWhite,
        radius = earRadius,
        center = Offset(center.x + earOffset, center.y - earOffset)
    )
    drawCircle(
        color = MizuiroBase,
        radius = earRadius * 0.6f,
        center = Offset(center.x + earOffset, center.y - earOffset)
    )
    
    // Eyes
    val eyeRadius = radius * 0.15f
    val eyeY = center.y - radius * 0.2f
    val eyeSpacing = radius * 0.3f
    
    when (state) {
        MascotState.Idle, MascotState.Listening -> {
            // Normal eyes with blinking
            val eyeOpenness = if (blink < 0.1f) 0.1f else 1f
            
            // Left eye
            drawCircle(
                color = FadedBlack,
                radius = eyeRadius,
                center = Offset(center.x - eyeSpacing, eyeY)
            )
            if (eyeOpenness < 1f) {
                drawLine(
                    color = FadedBlack,
                    start = Offset(center.x - eyeSpacing - eyeRadius, eyeY),
                    end = Offset(center.x - eyeSpacing + eyeRadius, eyeY),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }
            
            // Right eye
            drawCircle(
                color = FadedBlack,
                radius = eyeRadius,
                center = Offset(center.x + eyeSpacing, eyeY)
            )
            if (eyeOpenness < 1f) {
                drawLine(
                    color = FadedBlack,
                    start = Offset(center.x + eyeSpacing - eyeRadius, eyeY),
                    end = Offset(center.x + eyeSpacing + eyeRadius, eyeY),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }
        }
        
        MascotState.Happy, MascotState.Excited, MascotState.Dancing -> {
            // Happy eyes (crescent shape)
            drawHappyEyes(center, eyeSpacing, eyeY, eyeRadius)
        }
        
        MascotState.Sad, MascotState.Tired -> {
            // Sad eyes (downward crescents)
            drawSadEyes(center, eyeSpacing, eyeY, eyeRadius)
        }
        
        MascotState.Sleeping -> {
            // Closed eyes (lines)
            drawLine(
                color = FadedBlack,
                start = Offset(center.x - eyeSpacing - eyeRadius, eyeY),
                end = Offset(center.x - eyeSpacing + eyeRadius, eyeY),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = FadedBlack,
                start = Offset(center.x + eyeSpacing - eyeRadius, eyeY),
                end = Offset(center.x + eyeSpacing + eyeRadius, eyeY),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
        
        MascotState.Curious -> {
            // One eye normal, one raised
            drawCircle(
                color = FadedBlack,
                radius = eyeRadius,
                center = Offset(center.x - eyeSpacing, eyeY)
            )
            drawCircle(
                color = FadedBlack,
                radius = eyeRadius,
                center = Offset(center.x + eyeSpacing, eyeY - radius * 0.1f)
            )
        }
        
        MascotState.Dead -> {
            // X eyes
            drawDeadEyes(center, eyeSpacing, eyeY, eyeRadius)
        }
        
        MascotState.Working -> {
            // Concentrated eyes (smaller)
            drawCircle(
                color = FadedBlack,
                radius = eyeRadius * 0.7f,
                center = Offset(center.x - eyeSpacing, eyeY)
            )
            drawCircle(
                color = FadedBlack,
                radius = eyeRadius * 0.7f,
                center = Offset(center.x + eyeSpacing, eyeY)
            )
        }
    }
    
    // Mouth
    val mouthY = center.y + radius * 0.2f
    when (state) {
        MascotState.Happy, MascotState.Excited, MascotState.Dancing -> {
            // Happy mouth (smile)
            drawHappyMouth(center, mouthY, radius)
        }
        
        MascotState.Sad, MascotState.Tired -> {
            // Sad mouth (frown)
            drawSadMouth(center, mouthY, radius)
        }
        
        MascotState.Sleeping -> {
            // Zzz
            drawSleepingMouth(center, mouthY, radius)
        }
        
        MascotState.Dead -> {
            // X mouth
            drawLine(
                color = FadedBlack,
                start = Offset(center.x - radius * 0.2f, mouthY - radius * 0.1f),
                end = Offset(center.x + radius * 0.2f, mouthY + radius * 0.1f),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = FadedBlack,
                start = Offset(center.x - radius * 0.2f, mouthY + radius * 0.1f),
                end = Offset(center.x + radius * 0.2f, mouthY - radius * 0.1f),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
        
        else -> {
            // Neutral mouth (small circle)
            drawCircle(
                color = FadedBlack,
                radius = radius * 0.05f,
                center = Offset(center.x, mouthY)
            )
        }
    }
    
    // Special effects based on state
    when (state) {
        MascotState.Excited, MascotState.Dancing -> {
            // Sparkles around the mascot
            drawSparkles(center, radius)
        }
        
        MascotState.Working -> {
            // Sweat drops
            drawSweatDrops(center, radius)
        }
    }
}

private fun DrawScope.drawHappyEyes(center: Offset, eyeSpacing: Float, eyeY: Float, eyeRadius: Float) {
    val path = Path()
    
    // Left eye
    path.moveTo(center.x - eyeSpacing - eyeRadius, eyeY)
    path.quadraticBezierTo(
        center.x - eyeSpacing, eyeY - eyeRadius * 0.5f,
        center.x - eyeSpacing + eyeRadius, eyeY
    )
    drawPath(path, FadedBlack, style = Stroke(width = 2f, cap = StrokeCap.Round))
    
    // Right eye
    path.reset()
    path.moveTo(center.x + eyeSpacing - eyeRadius, eyeY)
    path.quadraticBezierTo(
        center.x + eyeSpacing, eyeY - eyeRadius * 0.5f,
        center.x + eyeSpacing + eyeRadius, eyeY
    )
    drawPath(path, FadedBlack, style = Stroke(width = 2f, cap = StrokeCap.Round))
}

private fun DrawScope.drawSadEyes(center: Offset, eyeSpacing: Float, eyeY: Float, eyeRadius: Float) {
    val path = Path()
    
    // Left eye
    path.moveTo(center.x - eyeSpacing - eyeRadius, eyeY)
    path.quadraticBezierTo(
        center.x - eyeSpacing, eyeY + eyeRadius * 0.5f,
        center.x - eyeSpacing + eyeRadius, eyeY
    )
    drawPath(path, FadedBlack, style = Stroke(width = 2f, cap = StrokeCap.Round))
    
    // Right eye
    path.reset()
    path.moveTo(center.x + eyeSpacing - eyeRadius, eyeY)
    path.quadraticBezierTo(
        center.x + eyeSpacing, eyeY + eyeRadius * 0.5f,
        center.x + eyeSpacing + eyeRadius, eyeY
    )
    drawPath(path, FadedBlack, style = Stroke(width = 2f, cap = StrokeCap.Round))
}

private fun DrawScope.drawDeadEyes(center: Offset, eyeSpacing: Float, eyeY: Float, eyeRadius: Float) {
    // Left X
    drawLine(
        color = FadedBlack,
        start = Offset(center.x - eyeSpacing - eyeRadius, eyeY - eyeRadius),
        end = Offset(center.x - eyeSpacing + eyeRadius, eyeY + eyeRadius),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = FadedBlack,
        start = Offset(center.x - eyeSpacing - eyeRadius, eyeY + eyeRadius),
        end = Offset(center.x - eyeSpacing + eyeRadius, eyeY - eyeRadius),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
    
    // Right X
    drawLine(
        color = FadedBlack,
        start = Offset(center.x + eyeSpacing - eyeRadius, eyeY - eyeRadius),
        end = Offset(center.x + eyeSpacing + eyeRadius, eyeY + eyeRadius),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = FadedBlack,
        start = Offset(center.x + eyeSpacing - eyeRadius, eyeY + eyeRadius),
        end = Offset(center.x + eyeSpacing + eyeRadius, eyeY - eyeRadius),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawHappyMouth(center: Offset, mouthY: Float, radius: Float) {
    val path = Path()
    path.moveTo(center.x - radius * 0.3f, mouthY)
    path.quadraticBezierTo(
        center.x, mouthY + radius * 0.2f,
        center.x + radius * 0.3f, mouthY
    )
    drawPath(path, FadedBlack, style = Stroke(width = 2f, cap = StrokeCap.Round))
}

private fun DrawScope.drawSadMouth(center: Offset, mouthY: Float, radius: Float) {
    val path = Path()
    path.moveTo(center.x - radius * 0.3f, mouthY)
    path.quadraticBezierTo(
        center.x, mouthY - radius * 0.2f,
        center.x + radius * 0.3f, mouthY
    )
    drawPath(path, FadedBlack, style = Stroke(width = 2f, cap = StrokeCap.Round))
}

private fun DrawScope.drawSleepingMouth(center: Offset, mouthY: Float, radius: Float) {
    // Draw "Z" shape
    drawLine(
        color = FadedBlack,
        start = Offset(center.x - radius * 0.2f, mouthY - radius * 0.1f),
        end = Offset(center.x + radius * 0.2f, mouthY - radius * 0.1f),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = FadedBlack,
        start = Offset(center.x + radius * 0.2f, mouthY - radius * 0.1f),
        end = Offset(center.x - radius * 0.2f, mouthY + radius * 0.1f),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = FadedBlack,
        start = Offset(center.x - radius * 0.2f, mouthY + radius * 0.1f),
        end = Offset(center.x + radius * 0.2f, mouthY + radius * 0.1f),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawSparkles(center: Offset, radius: Float) {
    val sparkleRadius = radius * 0.1f
    val sparkleDistance = radius * 1.5f
    
    // Draw 4 sparkles around the mascot
    for (i in 0..3) {
        val angle = i * PI.toFloat() / 2f
        val sparkleX = center.x + cos(angle) * sparkleDistance
        val sparkleY = center.y + sin(angle) * sparkleDistance
        
        // Draw star shape
        val starPath = Path()
        starPath.moveTo(sparkleX, sparkleY - sparkleRadius)
        starPath.lineTo(sparkleX + sparkleRadius * 0.3f, sparkleY - sparkleRadius * 0.3f)
        starPath.lineTo(sparkleX + sparkleRadius, sparkleY)
        starPath.lineTo(sparkleX + sparkleRadius * 0.3f, sparkleY + sparkleRadius * 0.3f)
        starPath.lineTo(sparkleX, sparkleY + sparkleRadius)
        starPath.lineTo(sparkleX - sparkleRadius * 0.3f, sparkleY + sparkleRadius * 0.3f)
        starPath.lineTo(sparkleX - sparkleRadius, sparkleY)
        starPath.lineTo(sparkleX - sparkleRadius * 0.3f, sparkleY - sparkleRadius * 0.3f)
        starPath.close()
        
        drawPath(starPath, BlushPink, style = Fill)
    }
}

private fun DrawScope.drawSweatDrops(center: Offset, radius: Float) {
    // Draw small sweat drops
    drawCircle(
        color = MizuiroLight,
        radius = radius * 0.05f,
        center = Offset(center.x - radius * 0.3f, center.y - radius * 0.1f)
    )
    drawCircle(
        color = MizuiroLight,
        radius = radius * 0.03f,
        center = Offset(center.x + radius * 0.2f, center.y - radius * 0.2f)
    )
}
