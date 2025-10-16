package com.mizuiro.music.ui.components.decorations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.mizuiro.music.ui.theme.MizuiroLight
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Wobbly Line Component
 * 
 * A key decorative element of the Mizuiro aesthetic.
 * Creates organic, wavy lines that add character and movement.
 * Features:
 * - Animated wavy path
 * - Customizable amplitude and frequency
 * - Linear easing for retro feel
 * - Used as dividers and decorative elements
 */

@Composable
fun WobblyLine(
    modifier: Modifier = Modifier,
    color: Color = MizuiroLight,
    strokeWidth: Float = 2f,
    amplitude: Float = 8f,
    frequency: Float = 0.15f,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wobbly_line")
    
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "wobbly_offset"
    )
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        
        val path = Path()
        val points = 50 // Number of control points
        
        path.moveTo(0f, centerY)
        
        for (i in 0..points) {
            val x = (width / points) * i
            val time = if (animated) offset else 0f
            val y = centerY + 
                    sin(x * frequency + time) * amplitude +
                    sin(x * frequency * 0.5f + time * 0.7f) * amplitude * 0.3f
            
            if (i == 0) {
                path.lineTo(x, y)
            } else {
                val prevX = (width / points) * (i - 1)
                val controlX1 = prevX + (x - prevX) * 0.3f
                val controlX2 = x - (x - prevX) * 0.3f
                path.cubicTo(
                    controlX1, path.currentY,
                    controlX2, y,
                    x, y
                )
            }
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
fun WobblyDivider(
    modifier: Modifier = Modifier,
    color: Color = MizuiroLight.copy(alpha = 0.6f),
    strokeWidth: Float = 2f
) {
    WobblyLine(
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth,
        amplitude = 6f,
        frequency = 0.12f,
        animated = true
    )
}

@Composable
fun WobblyBorder(
    modifier: Modifier = Modifier,
    color: Color = MizuiroLight,
    strokeWidth: Float = 2f,
    intensity: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wobbly_border")
    
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "border_offset"
    )
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val amplitude = 8f * intensity
        
        // Top border
        drawWobblyPath(
            start = Offset(0f, 0f),
            end = Offset(width, 0f),
            amplitude = amplitude,
            frequency = 0.1f,
            offset = offset,
            color = color,
            strokeWidth = strokeWidth
        )
        
        // Right border
        drawWobblyPath(
            start = Offset(width, 0f),
            end = Offset(width, height),
            amplitude = amplitude,
            frequency = 0.1f,
            offset = offset,
            color = color,
            strokeWidth = strokeWidth
        )
        
        // Bottom border
        drawWobblyPath(
            start = Offset(width, height),
            end = Offset(0f, height),
            amplitude = amplitude,
            frequency = 0.1f,
            offset = offset,
            color = color,
            strokeWidth = strokeWidth
        )
        
        // Left border
        drawWobblyPath(
            start = Offset(0f, height),
            end = Offset(0f, 0f),
            amplitude = amplitude,
            frequency = 0.1f,
            offset = offset,
            color = color,
            strokeWidth = strokeWidth
        )
    }
}

private fun DrawScope.drawWobblyPath(
    start: Offset,
    end: Offset,
    amplitude: Float,
    frequency: Float,
    offset: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    val points = 30
    
    path.moveTo(start.x, start.y)
    
    for (i in 0..points) {
        val progress = i.toFloat() / points
        val x = start.x + (end.x - start.x) * progress
        val y = start.y + (end.y - start.y) * progress
        
        val wobble = sin(progress * frequency * 10f + offset) * amplitude
        val perpendicularX = -(end.y - start.y) / (end.x - start.x + 0.001f)
        val perpendicularY = 1f
        
        val length = kotlin.math.sqrt(perpendicularX * perpendicularX + perpendicularY * perpendicularY)
        val normalizedX = perpendicularX / length
        val normalizedY = perpendicularY / length
        
        val finalX = x + wobble * normalizedX
        val finalY = y + wobble * normalizedY
        
        if (i == 0) {
            path.lineTo(finalX, finalY)
        } else {
            val prevProgress = (i - 1).toFloat() / points
            val prevX = start.x + (end.x - start.x) * prevProgress
            val prevY = start.y + (end.y - start.y) * prevProgress
            val prevWobble = sin(prevProgress * frequency * 10f + offset) * amplitude
            val prevFinalX = prevX + prevWobble * normalizedX
            val prevFinalY = prevY + prevWobble * normalizedY
            
            val controlX1 = prevFinalX + (finalX - prevFinalX) * 0.3f
            val controlY1 = prevFinalY + (finalY - prevFinalY) * 0.3f
            val controlX2 = finalX - (finalX - prevFinalX) * 0.3f
            val controlY2 = finalY - (finalY - prevFinalY) * 0.3f
            
            path.cubicTo(
                controlX1, controlY1,
                controlX2, controlY2,
                finalX, finalY
            )
        }
    }
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )
}
