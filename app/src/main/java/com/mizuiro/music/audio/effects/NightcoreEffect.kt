package com.mizuiro.music.audio.effects

import kotlin.math.*

/**
 * Nightcore Effect
 * 
 * The signature effect of Mizuiro Music - combines pitch shifting
 * and tempo increase to create the characteristic "nightcore" sound.
 * 
 * Features:
 * - Pitch shifting (typically +3 to +7 semitones)
 * - Tempo increase (typically 1.2x to 1.5x)
 * - Optional formant preservation
 * - Kawaii boost for extra cuteness
 */
class NightcoreEffect(
    override val id: String = "nightcore_${System.currentTimeMillis()}"
) : AudioEffect {
    
    override val name = "Nightcore"
    override val description = "Classic nightcore effect with pitch and tempo boost"
    override val category = EffectCategory.SPECIAL
    override var isEnabled = true
    
    // Parameters
    private var pitchShift = 4.0f // semitones
    private var tempoMultiplier = 1.25f // 1.0 = normal, 1.25 = 25% faster
    private var formantPreservation = 0.7f // 0.0 = none, 1.0 = full
    private var kawaiiBoost = 0.3f // 0.0 = none, 1.0 = maximum kawaii
    private var brightness = 0.5f // 0.0 = dark, 1.0 = bright
    private var energy = 0.8f // 0.0 = calm, 1.0 = energetic
    
    // Internal state
    private var phase = 0.0
    private var sampleRate = 44100
    private val buffer = mutableListOf<Float>()
    private var bufferIndex = 0
    
    override val parameters: Map<String, EffectParameter>
        get() = mapOf(
            "pitch_shift" to EffectParameter(
                name = "Pitch Shift",
                value = pitchShift,
                minValue = -12.0f,
                maxValue = 12.0f,
                defaultValue = 4.0f,
                unit = ParameterUnit.SEMITONES,
                description = "Pitch shift in semitones (positive = higher)"
            ),
            "tempo_multiplier" to EffectParameter(
                name = "Tempo",
                value = tempoMultiplier,
                minValue = 0.5f,
                maxValue = 2.0f,
                defaultValue = 1.25f,
                unit = ParameterUnit.RATIO,
                description = "Tempo multiplier (1.0 = normal speed)"
            ),
            "formant_preservation" to EffectParameter(
                name = "Formant Preserve",
                value = formantPreservation,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.7f,
                unit = ParameterUnit.PERCENT,
                description = "Preserve vocal formants for natural sound"
            ),
            "kawaii_boost" to EffectParameter(
                name = "Kawaii Boost",
                value = kawaiiBoost,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.3f,
                unit = ParameterUnit.PERCENT,
                description = "Extra kawaii factor (◠‿◠)"
            ),
            "brightness" to EffectParameter(
                name = "Brightness",
                value = brightness,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.5f,
                unit = ParameterUnit.PERCENT,
                description = "Overall brightness of the effect"
            ),
            "energy" to EffectParameter(
                name = "Energy",
                value = energy,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.8f,
                unit = ParameterUnit.PERCENT,
                description = "Energy level of the effect"
            )
        )
    
    override fun process(input: FloatArray, sampleRate: Int): FloatArray {
        this.sampleRate = sampleRate
        val output = FloatArray(input.size)
        
        for (i in input.indices) {
            // Apply pitch shifting
            val pitchShifted = applyPitchShift(input[i])
            
            // Apply tempo change (time stretching)
            val tempoChanged = applyTempoChange(pitchShifted)
            
            // Apply kawaii boost
            val kawaiiBoosted = applyKawaiiBoost(tempoChanged)
            
            // Apply brightness
            val brightened = applyBrightness(kawaiiBoosted)
            
            // Apply energy
            val energized = applyEnergy(brightened)
            
            output[i] = energized
        }
        
        return output
    }
    
    private fun applyPitchShift(sample: Float): Float {
        // Simple pitch shifting using phase vocoder technique
        val pitchRatio = 2.0.pow(pitchShift / 12.0)
        val phaseIncrement = 2.0 * PI * pitchRatio / sampleRate
        
        phase += phaseIncrement
        if (phase >= 2.0 * PI) {
            phase -= 2.0 * PI
        }
        
        // Apply formant preservation
        val formantPreserved = if (formantPreservation > 0.0f) {
            sample * (1.0f - formantPreservation) + sample * formantPreservation * sin(phase).toFloat()
        } else {
            sample
        }
        
        return formantPreserved
    }
    
    private fun applyTempoChange(sample: Float): Float {
        // Simple tempo change using buffer
        buffer.add(sample)
        
        if (buffer.size > sampleRate) {
            buffer.removeAt(0)
        }
        
        val index = (bufferIndex * tempoMultiplier).toInt()
        if (index < buffer.size) {
            bufferIndex++
            return buffer[index]
        }
        
        return sample
    }
    
    private fun applyKawaiiBoost(sample: Float): Float {
        // Kawaii boost adds harmonic content and sparkle
        val boost = 1.0f + kawaiiBoost * 0.5f
        val harmonic = sin(phase * 2.0).toFloat() * kawaiiBoost * 0.1f
        val sparkle = sin(phase * 4.0).toFloat() * kawaiiBoost * 0.05f
        
        return sample * boost + harmonic + sparkle
    }
    
    private fun applyBrightness(sample: Float): Float {
        // Brightness affects high-frequency content
        val brightnessFactor = 1.0f + brightness * 0.3f
        val highFreq = sin(phase * 8.0).toFloat() * brightness * 0.1f
        
        return sample * brightnessFactor + highFreq
    }
    
    private fun applyEnergy(sample: Float): Float {
        // Energy affects dynamics and punch
        val energyFactor = 1.0f + energy * 0.4f
        val punch = if (abs(sample) > 0.5f) sample * energy * 0.2f else 0.0f
        
        return sample * energyFactor + punch
    }
    
    fun setPitchShift(value: Float) {
        pitchShift = value.coerceIn(-12.0f, 12.0f)
    }
    
    fun setTempoMultiplier(value: Float) {
        tempoMultiplier = value.coerceIn(0.5f, 2.0f)
    }
    
    fun setFormantPreservation(value: Float) {
        formantPreservation = value.coerceIn(0.0f, 1.0f)
    }
    
    fun setKawaiiBoost(value: Float) {
        kawaiiBoost = value.coerceIn(0.0f, 1.0f)
    }
    
    fun setBrightness(value: Float) {
        brightness = value.coerceIn(0.0f, 1.0f)
    }
    
    fun setEnergy(value: Float) {
        energy = value.coerceIn(0.0f, 1.0f)
    }
    
    override fun reset() {
        phase = 0.0
        buffer.clear()
        bufferIndex = 0
    }
    
    override fun clone(): AudioEffect {
        val clone = NightcoreEffect()
        clone.setPitchShift(pitchShift)
        clone.setTempoMultiplier(tempoMultiplier)
        clone.setFormantPreservation(formantPreservation)
        clone.setKawaiiBoost(kawaiiBoost)
        clone.setBrightness(brightness)
        clone.setEnergy(energy)
        return clone
    }
}

/**
 * Nightcore presets
 */
object NightcorePresets {
    val CLASSIC = EffectPreset(
        id = "nightcore_classic",
        name = "Classic Nightcore",
        description = "Traditional nightcore sound",
        category = EffectCategory.SPECIAL,
        effects = listOf(
            EffectInstance(
                effectId = "nightcore",
                parameters = mapOf(
                    "pitch_shift" to 4.0f,
                    "tempo_multiplier" to 1.25f,
                    "formant_preservation" to 0.7f,
                    "kawaii_boost" to 0.3f,
                    "brightness" to 0.5f,
                    "energy" to 0.8f
                )
            )
        )
    )
    
    val KAWAII = EffectPreset(
        id = "nightcore_kawaii",
        name = "Kawaii Nightcore",
        description = "Extra kawaii nightcore (◠‿◠)",
        category = EffectCategory.SPECIAL,
        effects = listOf(
            EffectInstance(
                effectId = "nightcore",
                parameters = mapOf(
                    "pitch_shift" to 5.0f,
                    "tempo_multiplier" to 1.3f,
                    "formant_preservation" to 0.8f,
                    "kawaii_boost" to 0.7f,
                    "brightness" to 0.8f,
                    "energy" to 0.9f
                )
            )
        )
    )
    
    val DARK = EffectPreset(
        id = "nightcore_dark",
        name = "Dark Nightcore",
        description = "Moody nightcore with darker tones",
        category = EffectCategory.SPECIAL,
        effects = listOf(
            EffectInstance(
                effectId = "nightcore",
                parameters = mapOf(
                    "pitch_shift" to 3.0f,
                    "tempo_multiplier" to 1.15f,
                    "formant_preservation" to 0.5f,
                    "kawaii_boost" to 0.1f,
                    "brightness" to 0.2f,
                    "energy" to 0.6f
                )
            )
        )
    )
    
    val EXTREME = EffectPreset(
        id = "nightcore_extreme",
        name = "Extreme Nightcore",
        description = "Maximum nightcore effect",
        category = EffectCategory.SPECIAL,
        effects = listOf(
            EffectInstance(
                effectId = "nightcore",
                parameters = mapOf(
                    "pitch_shift" to 7.0f,
                    "tempo_multiplier" to 1.5f,
                    "formant_preservation" to 0.6f,
                    "kawaii_boost" to 0.5f,
                    "brightness" to 0.9f,
                    "energy" to 1.0f
                )
            )
        )
    )
}
