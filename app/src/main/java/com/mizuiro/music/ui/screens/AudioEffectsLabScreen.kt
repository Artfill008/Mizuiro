package com.mizuiro.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mizuiro.music.audio.effects.*
import com.mizuiro.music.ui.components.buttons.MizuiroButton
import com.mizuiro.music.ui.components.buttons.MizuiroIconButton
import com.mizuiro.music.ui.components.decorations.KawaiiMascot
import com.mizuiro.music.ui.components.decorations.MascotState
import com.mizuiro.music.ui.components.decorations.WobblyDivider
import com.mizuiro.music.ui.theme.*

/**
 * Audio Effects Lab Screen
 * 
 * The main interface for the Audio Effects Lab where users can
 * experiment with various audio effects while maintaining the
 * Mizuiro aesthetic.
 */
@Composable
fun AudioEffectsLabScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEffectSelected: (AudioEffect) -> Unit = {},
    onPresetSelected: (EffectPreset) -> Unit = {},
    onChainSelected: (EffectChain) -> Unit = {}
) {
    var selectedEffect by remember { mutableStateOf<AudioEffect?>(null) }
    var selectedPreset by remember { mutableStateOf<EffectPreset?>(null) }
    var selectedChain by remember { mutableStateOf<EffectChain?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CoolWhite,
                        IcyGrey.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ASCII art header
                    Text(
                        text = "╔════════════════════════════════╗",
                        style = VT323Style,
                        color = CyberSilver
                    )
                    Text(
                        text = "║      AUDIO EFFECTS LAB       ║",
                        style = VT323Style,
                        color = MizuiroBase
                    )
                    Text(
                        text = "╚════════════════════════════════╝",
                        style = VT323Style,
                        color = CyberSilver
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Kawaii mascot
                    KawaiiMascot(
                        state = if (isProcessing) MascotState.Working else MascotState.Happy,
                        size = 64.dp
                    )
                    
                    Text(
                        text = "Experiment with Audio Magic! (◠‿◠)",
                        style = H1,
                        color = FadedBlack,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Create your perfect sound with Mizuiro effects",
                        style = Body,
                        color = SteelBlue,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Wobbly divider
            item {
                WobblyDivider()
            }
            
            // Effect Categories
            item {
                Text(
                    text = "Effect Categories",
                    style = H2,
                    color = FadedBlack
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(EffectCategory.values()) { category ->
                        EffectCategoryCard(
                            category = category,
                            onClick = { /* TODO: Filter by category */ }
                        )
                    }
                }
            }
            
            // Featured Effects
            item {
                Text(
                    text = "Featured Effects",
                    style = H2,
                    color = FadedBlack
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(getFeaturedEffects()) { effect ->
                        EffectCard(
                            effect = effect,
                            isSelected = selectedEffect?.id == effect.id,
                            onClick = { 
                                selectedEffect = effect
                                onEffectSelected(effect)
                            }
                        )
                    }
                }
            }
            
            // Mizuiro Special Effects
            item {
                Text(
                    text = "Mizuiro Special Effects",
                    style = H2,
                    color = FadedBlack
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(getMizuiroEffects()) { effect ->
                        EffectCard(
                            effect = effect,
                            isSelected = selectedEffect?.id == effect.id,
                            onClick = { 
                                selectedEffect = effect
                                onEffectSelected(effect)
                            }
                        )
                    }
                }
            }
            
            // Effect Presets
            item {
                Text(
                    text = "Effect Presets",
                    style = H2,
                    color = FadedBlack
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(getEffectPresets()) { preset ->
                        PresetCard(
                            preset = preset,
                            isSelected = selectedPreset?.id == preset.id,
                            onClick = { 
                                selectedPreset = preset
                                onPresetSelected(preset)
                            }
                        )
                    }
                }
            }
            
            // Effect Chains
            item {
                Text(
                    text = "Effect Chains",
                    style = H2,
                    color = FadedBlack
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(getEffectChains()) { chain ->
                        ChainCard(
                            chain = chain,
                            isSelected = selectedChain?.id == chain.id,
                            onClick = { 
                                selectedChain = chain
                                onChainSelected(chain)
                            }
                        )
                    }
                }
            }
            
            // Control Panel
            item {
                ControlPanel(
                    isProcessing = isProcessing,
                    onToggleProcessing = { isProcessing = !isProcessing },
                    onReset = { 
                        selectedEffect = null
                        selectedPreset = null
                        selectedChain = null
                    }
                )
            }
            
            // Decorative elements
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(5) {
                        KawaiiMascot(
                            state = MascotState.Dancing,
                            size = 24.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EffectCategoryCard(
    category: EffectCategory,
    onClick: () -> Unit
) {
    MizuiroButton(
        text = category.name,
        onClick = onClick,
        secondary = true,
        fullWidth = false
    )
}

@Composable
fun EffectCard(
    effect: AudioEffect,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(120.dp),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MizuiroBase.copy(alpha = 0.3f) else CoolWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = effect.name,
                style = H2,
                color = FadedBlack
            )
            
            Text(
                text = effect.description,
                style = Caption,
                color = SteelBlue,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = effect.category.name,
                    style = Caption,
                    color = SteelBlue.copy(alpha = 0.7f)
                )
                
                if (effect.isEnabled) {
                    Text(
                        text = "●",
                        style = Caption,
                        color = MizuiroBase
                    )
                }
            }
        }
    }
}

@Composable
fun PresetCard(
    preset: EffectPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(100.dp),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MizuiroBase.copy(alpha = 0.3f) else CoolWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = preset.name,
                style = H2,
                color = FadedBlack
            )
            
            Text(
                text = preset.description,
                style = Caption,
                color = SteelBlue,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${preset.effects.size} effects",
                    style = Caption,
                    color = SteelBlue.copy(alpha = 0.7f)
                )
                
                if (preset.isCustom) {
                    Text(
                        text = "★",
                        style = Caption,
                        color = BlushPink
                    )
                }
            }
        }
    }
}

@Composable
fun ChainCard(
    chain: EffectChain,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(100.dp),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MizuiroBase.copy(alpha = 0.3f) else CoolWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = chain.name,
                style = H2,
                color = FadedBlack
            )
            
            Text(
                text = "${chain.effects.size} effects",
                style = Caption,
                color = SteelBlue
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chain",
                    style = Caption,
                    color = SteelBlue.copy(alpha = 0.7f)
                )
                
                if (chain.isEnabled) {
                    Text(
                        text = "●",
                        style = Caption,
                        color = MizuiroBase
                    )
                }
            }
        }
    }
}

@Composable
fun ControlPanel(
    isProcessing: Boolean,
    onToggleProcessing: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = IcyGrey.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Control Panel",
                style = H2,
                color = FadedBlack
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MizuiroButton(
                    text = if (isProcessing) "⏸ Stop" else "▶ Start",
                    onClick = onToggleProcessing,
                    fullWidth = false
                )
                
                MizuiroButton(
                    text = "🔄 Reset",
                    onClick = onReset,
                    secondary = true,
                    fullWidth = false
                )
                
                MizuiroButton(
                    text = "💾 Save",
                    onClick = { /* TODO: Save effect chain */ },
                    secondary = true,
                    fullWidth = false
                )
            }
        }
    }
}

// Sample data functions
private fun getFeaturedEffects(): List<AudioEffect> {
    return listOf(
        NightcoreEffect(),
        VocaloidEffect(),
        MizuiroGlitchEffect(),
        KawaiiBoostEffect()
    )
}

private fun getMizuiroEffects(): List<AudioEffect> {
    return listOf(
        MizuiroGlitchEffect(),
        KawaiiBoostEffect(),
        NostalgiaFilterEffect(),
        DigitalDreamEffect()
    )
}

private fun getEffectPresets(): List<EffectPreset> {
    return listOf(
        NightcorePresets.CLASSIC,
        NightcorePresets.KAWAII,
        VocaloidPresets.HATSUNE_MIKU,
        VocaloidPresets.KAITO
    )
}

private fun getEffectChains(): List<EffectChain> {
    return listOf(
        EffectChain(
            id = "mizuiro_dream",
            name = "Mizuiro Dream",
            effects = listOf(
                EffectInstance("nightcore"),
                EffectInstance("kawaii_boost"),
                EffectInstance("nostalgia_filter")
            )
        ),
        EffectChain(
            id = "digital_nostalgia",
            name = "Digital Nostalgia",
            effects = listOf(
                EffectInstance("mizuiro_glitch"),
                EffectInstance("nostalgia_filter"),
                EffectInstance("digital_dream")
            )
        )
    )
}
