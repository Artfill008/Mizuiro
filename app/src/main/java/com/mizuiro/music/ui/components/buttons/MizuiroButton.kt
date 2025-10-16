package com.mizuiro.music.ui.components.buttons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mizuiro.music.ui.theme.ButtonShape
import com.mizuiro.music.ui.theme.CyberSilver
import com.mizuiro.music.ui.theme.FadedBlack
import com.mizuiro.music.ui.theme.H2
import com.mizuiro.music.ui.theme.MizuiroBase
import com.mizuiro.music.ui.theme.MizuiroLight
import com.mizuiro.music.ui.theme.MizuiroMedium
import com.mizuiro.music.ui.theme.Transparent
import com.mizuiro.music.ui.theme.White

/**
 * Mizuiro Button Component
 * 
 * Custom button implementation that rejects Material Design 3
 * in favor of the Mizuiro aesthetic. Features:
 * - Rounded rectangle shape (radius: 20dp)
 * - Mizuiro color scheme
 * - Subtle glow effect instead of ripple
 * - Pressed state with scale animation
 * - No Material Design ripple effects
 */

@Composable
fun MizuiroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    secondary: Boolean = false,
    fullWidth: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { androidx.compose.runtime.mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "button_scale"
    )
    
    val buttonColors = if (secondary) {
        ButtonColors(
            background = Transparent,
            text = FadedBlack,
            border = CyberSilver
        )
    } else {
        ButtonColors(
            background = MizuiroBase,
            text = FadedBlack,
            border = White.copy(alpha = 0.25f)
        )
    }
    
    val buttonModifier = if (fullWidth) {
        modifier.fillMaxWidth()
    } else {
        modifier
    }
    
    Box(
        modifier = buttonModifier
            .scale(scale)
            .clip(ButtonShape)
            .background(
                brush = if (secondary) {
                    Brush.linearGradient(
                        colors = listOf(buttonColors.background, buttonColors.background)
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(buttonColors.background, MizuiroLight)
                    )
                }
            )
            .border(
                width = if (secondary) 1.5.dp else 2.dp,
                color = buttonColors.border,
                shape = ButtonShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null, // No ripple effect
                enabled = enabled,
                onClick = {
                    isPressed = true
                    onClick()
                    // Reset pressed state after animation
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(100)
                        isPressed = false
                    }
                }
            )
            .padding(
                horizontal = if (secondary) 24.dp else 32.dp,
                vertical = if (secondary) 12.dp else 16.dp
            )
    ) {
        Text(
            text = text,
            style = H2.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = buttonColors.text,
                shadow = if (!secondary) {
                    Shadow(
                        color = MizuiroLight,
                        blurRadius = 8f,
                        offset = androidx.compose.ui.geometry.Offset(0f, 2f)
                    )
                } else null
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun MizuiroIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    secondary: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { androidx.compose.runtime.mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "icon_button_scale"
    )
    
    val buttonColors = if (secondary) {
        ButtonColors(
            background = Transparent,
            text = FadedBlack,
            border = CyberSilver
        )
    } else {
        ButtonColors(
            background = IcyGrey,
            text = FadedBlack,
            border = CyberSilver
        )
    }
    
    Box(
        modifier = modifier
            .size(44.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(buttonColors.background)
            .border(
                width = 1.dp,
                color = buttonColors.border,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null, // No ripple effect
                enabled = enabled,
                onClick = {
                    isPressed = true
                    onClick()
                    // Reset pressed state after animation
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(100)
                        isPressed = false
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

private data class ButtonColors(
    val background: Color,
    val text: Color,
    val border: Color
)
