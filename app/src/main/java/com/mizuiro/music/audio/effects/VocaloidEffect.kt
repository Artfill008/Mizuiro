package com.mizuiro.music.audio.effects

import kotlin.math.*

/**
 * Vocaloid Effect
 * 
 * Simulates the characteristic Vocaloid sound with formant shifting,
 * pitch correction, and synthetic vocal processing.
 * 
 * Features:
 * - Formant shifting for synthetic voice
 * - Pitch correction and quantization
 * - Vocal synthesis parameters
 * - Kawaii vocal processing
 */
class VocaloidEffect(
    override val id: String = "vocaloid_${System.currentTimeMillis()}"
) : AudioEffect {
    
    override val name = "Vocaloid"
    override val description = "Synthetic vocal processing like Vocaloid"
    override val category = EffectCategory.SPECIAL
    override var isEnabled = true
    
    // Parameters
    private var formantShift = 0.5f // 0.0 = natural, 1.0 = synthetic
    private var pitchQuantization = 0.8f // 0.0 = natural, 1.0 = quantized
    private var vocalBrightness = 0.6f // 0.0 = dark, 1.0 = bright
    private var kawaiiFactor = 0.7f // 0.0 = normal, 1.0 = kawaii
    private var breathiness = 0.3f // 0.0 = clear, 1.0 = breathy
    private var vibrato = 0.4f // 0.0 = none, 1.0 = strong
    private var gender = 0.5f // 0.0 = male, 1.0 = female
    
    // Internal state
    private var phase = 0.0
    private var vibratoPhase = 0.0
    private var sampleRate = 44100
    private val formantBuffer = mutableListOf<Float>()
    private val pitchBuffer = mutableListOf<Float>()
    
    override val parameters: Map<String, EffectParameter>
        get() = mapOf(
            "formant_shift" to EffectParameter(
                name = "Formant Shift",
                value = formantShift,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.5f,
                unit = ParameterUnit.PERCENT,
                description = "Shift formants for synthetic voice"
            ),
            "pitch_quantization" to EffectParameter(
                name = "Pitch Quantize",
                value = pitchQuantization,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.8f,
                unit = ParameterUnit.PERCENT,
                description = "Quantize pitch to musical notes"
            ),
            "vocal_brightness" to EffectParameter(
                name = "Vocal Brightness",
                value = vocalBrightness,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.6f,
                unit = ParameterUnit.PERCENT,
                description = "Brightness of the vocal sound"
            ),
            "kawaii_factor" to EffectParameter(
                name = "Kawaii Factor",
                value = kawaiiFactor,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.7f,
                unit = ParameterUnit.PERCENT,
                description = "Kawaii vocal processing (◠‿◠)"
            ),
            "breathiness" to EffectParameter(
                name = "Breathiness",
                value = breathiness,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.3f,
                unit = ParameterUnit.PERCENT,
                description = "Breathy vocal quality"
            ),
            "vibrato" to EffectParameter(
                name = "Vibrato",
                value = vibrato,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.4f,
                unit = ParameterUnit.PERCENT,
                description = "Vibrato depth"
            ),
            "gender" to EffectParameter(
                name = "Gender",
                value = gender,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.5f,
                unit = ParameterUnit.PERCENT,
                description = "Gender shift (0.0 = male, 1.0 = female)"
            )
        )
    
    override fun process(input: FloatArray, sampleRate: Int): FloatArray {
        this.sampleRate = sampleRate
        val output = FloatArray(input.size)
        
        for (i in input.indices) {
            // Apply formant shifting
            val formantShifted = applyFormantShift(input[i])
            
            // Apply pitch quantization
            val pitchQuantized = applyPitchQuantization(formantShifted)
            
            // Apply vocal brightness
            val brightened = applyVocalBrightness(pitchQuantized)
            
            // Apply kawaii processing
            val kawaiiProcessed = applyKawaiiProcessing(brightened)
            
            // Apply breathiness
            val breathy = applyBreathiness(kawaiiProcessed)
            
            // Apply vibrato
            val vibratoApplied = applyVibrato(breathy)
            
            // Apply gender shift
            val genderShifted = applyGenderShift(vibratoApplied)
            
            output[i] = genderShifted
        }
        
        return output
    }
    
    private fun applyFormantShift(sample: Float): Float {
        // Formant shifting for synthetic voice
        val formantRatio = 1.0f + formantShift * 0.5f
        val shifted = sample * formantRatio
        
        // Add formant resonance
        val resonance = sin(phase * 2.0).toFloat() * formantShift * 0.2f
        return shifted + resonance
    }
    
    private fun applyPitchQuantization(sample: Float): Float {
        // Pitch quantization to musical notes
        if (pitchQuantization > 0.0f) {
            val quantized = (sample * 12.0f).roundToInt() / 12.0f
            return sample * (1.0f - pitchQuantization) + quantized * pitchQuantization
        }
        return sample
    }
    
    private fun applyVocalBrightness(sample: Float): Float {
        // Brightness affects high-frequency content
        val brightnessFactor = 1.0f + vocalBrightness * 0.4f
        val highFreq = sin(phase * 4.0).toFloat() * vocalBrightness * 0.15f
        
        return sample * brightnessFactor + highFreq
    }
    
    private fun applyKawaiiProcessing(sample: Float): Float {
        // Kawaii processing adds cute vocal characteristics
        val kawaiiBoost = 1.0f + kawaiiFactor * 0.3f
        val kawaiiHarmonic = sin(phase * 3.0).toFloat() * kawaiiFactor * 0.1f
        val kawaiiSparkle = sin(phase * 6.0).toFloat() * kawaiiFactor * 0.05f
        
        return sample * kawaiiBoost + kawaiiHarmonic + kawaiiSparkle
    }
    
    private fun applyBreathiness(sample: Float): Float {
        // Add breathy quality
        val breathyNoise = (Math.random() - 0.5).toFloat() * breathiness * 0.1f
        val breathyFilter = sin(phase * 0.5).toFloat() * breathiness * 0.05f
        
        return sample + breathyNoise + breathyFilter
    }
    
    private fun applyVibrato(sample: Float): Float {
        // Apply vibrato modulation
        vibratoPhase += 2.0 * PI * 5.0 / sampleRate // 5 Hz vibrato
        if (vibratoPhase >= 2.0 * PI) {
            vibratoPhase -= 2.0 * PI
        }
        
        val vibratoMod = sin(vibratoPhase).toFloat() * vibrato * 0.1f
        return sample * (1.0f + vibratoMod)
    }
    
    private fun applyGenderShift(sample: Float): Float {
        // Gender shift affects formant frequencies
        val genderRatio = 1.0f + (gender - 0.5f) * 0.3f
        val genderShifted = sample * genderRatio
        
        // Add gender-specific harmonics
        val genderHarmonic = sin(phase * (1.0 + gender)).toFloat() * 0.1f
        
        return genderShifted + genderHarmonic
    }
    
    fun setFormantShift(value: Float) {
        formantShift = value.coerceIn(0.0f, 1.0f)
    }
    
    fun setPitchQuantization(value: Float) {
        pitchQuantization = value.coerceIn(0.0f, 1.0f)
    }
    
    fun setVocalBrightness(value: Float) {
        vocalBrightness = value.coerceIn(0.0f, 1.0f)
    }
    
    fun setKawaiiFactor(value: Float) {
        kawaiiFactor = value.coerceIn(0.0f, 1.0f)
    }
    
    fun setBreathiness(value: Float) {
        breathiness = value.coerceIn(0.0f, 1.0f)
    }
    
    fun setVibrato(value: Float) {
        vibrato = value.coerceIn(0.0f, 1.0f)
    }
    
    fun setGender(value: Float) {
        gender = value.coerceIn(0.0f, 1.0f)
    }
    
    override fun reset() {
        phase = 0.0
        vibratoPhase = 0.0
        formantBuffer.clear()
        pitchBuffer.clear()
    }
    
    override fun clone(): AudioEffect {
        val clone = VocaloidEffect()
        clone.setFormantShift(formantShift)
        clone.setPitchQuantization(pitchQuantization)
        clone.setVocalBrightness(vocalBrightness)
        clone.setKawaiiFactor(kawaiiFactor)
        clone.setBreathiness(breathiness)
        clone.setVibrato(vibrato)
        clone.setGender(gender)
        return clone
    }
}

/**
 * Vocaloid presets
 */
object VocaloidPresets {
    val HATSUNE_MIKU = EffectPreset(
        id = "vocaloid_miku",
        name = "Hatsune Miku",
        description = "Classic Miku sound",
        category = EffectCategory.SPECIAL,
        effects = listOf(
            EffectInstance(
                effectId = "vocaloid",
                parameters = mapOf(
                    "formant_shift" to 0.6f,
                    "pitch_quantization" to 0.8f,
                    "vocal_brightness" to 0.7f,
                    "kawaii_factor" to 0.8f,
                    "breathiness" to 0.2f,
                    "vibrato" to 0.3f,
                    "gender" to 0.8f
                )
            )
        )
    )
    
    val KAITO = EffectPreset(
        id = "vocaloid_kaito",
        name = "KAITO",
        description = "Male Vocaloid sound",
        category = EffectCategory.SPECIAL,
        effects = listOf(
            EffectInstance(
                effectId = "vocaloid",
                parameters = mapOf(
                    "formant_shift" to 0.4f,
                    "pitch_quantization" to 0.7f,
                    "vocal_brightness" to 0.5f,
                    "kawaii_factor" to 0.6f,
                    "breathiness" to 0.4f,
                    "vibrato" to 0.5f,
                    "gender" to 0.2f
                )
            )
        )
    )
    
    val KAWAII = EffectPreset(
        id = "vocaloid_kawaii",
        name = "Kawaii Vocaloid",
        description = "Extra kawaii vocal processing (◠‿◠)",
        category = EffectCategory.SPECIAL,
        effects = listOf(
            EffectInstance(
                effectId = "vocaloid",
                parameters = mapOf(
                    "formant_shift" to 0.8f,
                    "pitch_quantization" to 0.9f,
                    "vocal_brightness" to 0.9f,
                    "kawaii_factor" to 1.0f,
                    "breathiness" to 0.1f,
                    "vibrato" to 0.6f,
                    "gender" to 0.9f
                )
            )
        )
    )
    
    val DARK = EffectPreset(
        id = "vocaloid_dark",
        name = "Dark Vocaloid",
        description = "Moody synthetic voice",
        category = EffectCategory.SPECIAL,
        effects = listOf(
            EffectInstance(
                effectId = "vocaloid",
                parameters = mapOf(
                    "formant_shift" to 0.3f,
                    "pitch_quantization" to 0.6f,
                    "vocal_brightness" to 0.3f,
                    "kawaii_factor" to 0.2f,
                    "breathiness" to 0.6f,
                    "vibrato" to 0.2f,
                    "gender" to 0.5f
                )
            )
        )
    )
}
