package com.mizuiro.music.audio.effects

import kotlinx.serialization.Serializable

/**
 * Audio Effect System
 * 
 * Comprehensive audio effects system for Mizuiro Music.
 * Supports real-time effects, presets, and custom effect chains.
 */

/**
 * Base interface for all audio effects
 */
interface AudioEffect {
    val id: String
    val name: String
    val description: String
    val category: EffectCategory
    val isEnabled: Boolean
    val parameters: Map<String, EffectParameter>
    
    fun process(input: FloatArray, sampleRate: Int): FloatArray
    fun reset()
    fun clone(): AudioEffect
}

/**
 * Effect categories for organization
 */
@Serializable
enum class EffectCategory {
    PITCH,
    TEMPO,
    FILTER,
    DISTORTION,
    REVERB,
    DELAY,
    CHORUS,
    FLANGER,
    PHASER,
    COMPRESSOR,
    EQUALIZER,
    LIMITER,
    GATE,
    EXPANDER,
    SPECIAL,
    CUSTOM
}

/**
 * Effect parameter definition
 */
@Serializable
data class EffectParameter(
    val name: String,
    val value: Float,
    val minValue: Float,
    val maxValue: Float,
    val defaultValue: Float,
    val unit: ParameterUnit,
    val description: String,
    val isEnabled: Boolean = true
)

/**
 * Parameter units
 */
@Serializable
enum class ParameterUnit {
    PERCENT,
    DECIBEL,
    HERTZ,
    MILLISECONDS,
    SECONDS,
    BPM,
    RATIO,
    SEMITONES,
    CENTS,
    NONE
}

/**
 * Effect preset
 */
@Serializable
data class EffectPreset(
    val id: String,
    val name: String,
    val description: String,
    val category: EffectCategory,
    val effects: List<EffectInstance>,
    val isCustom: Boolean = false,
    val isPublic: Boolean = false,
    val author: String? = null,
    val tags: List<String> = emptyList(),
    val rating: Float = 0.0f,
    val downloadCount: Int = 0,
    val dateCreated: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis()
)

/**
 * Effect instance with parameters
 */
@Serializable
data class EffectInstance(
    val effectId: String,
    val isEnabled: Boolean = true,
    val parameters: Map<String, Float> = emptyMap(),
    val position: Int = 0
)

/**
 * Effect chain for processing
 */
@Serializable
data class EffectChain(
    val id: String,
    val name: String,
    val effects: List<EffectInstance>,
    val isEnabled: Boolean = true,
    val inputGain: Float = 0.0f,
    val outputGain: Float = 0.0f,
    val dryWet: Float = 1.0f
)

/**
 * Real-time effect processor
 */
class EffectProcessor {
    private val effects = mutableListOf<AudioEffect>()
    private var isProcessing = false
    
    fun addEffect(effect: AudioEffect) {
        effects.add(effect)
    }
    
    fun removeEffect(effectId: String) {
        effects.removeAll { it.id == effectId }
    }
    
    fun clearEffects() {
        effects.clear()
    }
    
    fun process(input: FloatArray, sampleRate: Int): FloatArray {
        if (!isProcessing) return input
        
        var output = input
        for (effect in effects) {
            if (effect.isEnabled) {
                output = effect.process(output, sampleRate)
            }
        }
        return output
    }
    
    fun startProcessing() {
        isProcessing = true
    }
    
    fun stopProcessing() {
        isProcessing = false
    }
    
    fun reset() {
        effects.forEach { it.reset() }
    }
}

/**
 * Effect factory for creating effects
 */
object EffectFactory {
    fun createEffect(effectType: EffectType, id: String = ""): AudioEffect {
        return when (effectType) {
            EffectType.NIGHTCORE -> NightcoreEffect(id)
            EffectType.VOCALOID -> VocaloidEffect(id)
            EffectType.PITCH_SHIFT -> PitchShiftEffect(id)
            EffectType.TEMPO_CHANGE -> TempoChangeEffect(id)
            EffectType.LOW_PASS -> LowPassFilterEffect(id)
            EffectType.HIGH_PASS -> HighPassFilterEffect(id)
            EffectType.BAND_PASS -> BandPassFilterEffect(id)
            EffectType.DISTORTION -> DistortionEffect(id)
            EffectType.BITCRUSH -> BitcrushEffect(id)
            EffectType.REVERB -> ReverbEffect(id)
            EffectType.DELAY -> DelayEffect(id)
            EffectType.CHORUS -> ChorusEffect(id)
            EffectType.FLANGER -> FlangerEffect(id)
            EffectType.PHASER -> PhaserEffect(id)
            EffectType.COMPRESSOR -> CompressorEffect(id)
            EffectType.EQUALIZER -> EqualizerEffect(id)
            EffectType.LIMITER -> LimiterEffect(id)
            EffectType.GATE -> GateEffect(id)
            EffectType.EXPANDER -> ExpanderEffect(id)
            EffectType.MIZUIRO_GLITCH -> MizuiroGlitchEffect(id)
            EffectType.KAWAII_BOOST -> KawaiiBoostEffect(id)
            EffectType.NOSTALGIA_FILTER -> NostalgiaFilterEffect(id)
            EffectType.DIGITAL_DREAM -> DigitalDreamEffect(id)
        }
    }
}

/**
 * Effect types
 */
enum class EffectType {
    // Pitch and Tempo
    NIGHTCORE,
    VOCALOID,
    PITCH_SHIFT,
    TEMPO_CHANGE,
    
    // Filters
    LOW_PASS,
    HIGH_PASS,
    BAND_PASS,
    
    // Distortion
    DISTORTION,
    BITCRUSH,
    
    // Time-based
    REVERB,
    DELAY,
    CHORUS,
    FLANGER,
    PHASER,
    
    // Dynamics
    COMPRESSOR,
    LIMITER,
    GATE,
    EXPANDER,
    
    // EQ
    EQUALIZER,
    
    // Mizuiro Special Effects
    MIZUIRO_GLITCH,
    KAWAII_BOOST,
    NOSTALGIA_FILTER,
    DIGITAL_DREAM
}
