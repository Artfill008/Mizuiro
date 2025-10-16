package com.mizuiro.music.audio.effects

import kotlin.math.*

/**
 * Mizuiro-specific audio effects
 * 
 * Custom effects that embody the Mizuiro aesthetic:
 * - Digital nostalgia and Web 1.0 vibes
 * - Kawaii processing and cuteness
 * - Retro glitch and lo-fi aesthetics
 * - Melancholic beauty and fragility
 */

/**
 * Mizuiro Glitch Effect
 * 
 * Adds digital glitch effects reminiscent of early 2000s internet
 * and Web 1.0 aesthetics.
 */
class MizuiroGlitchEffect(
    override val id: String = "mizuiro_glitch_${System.currentTimeMillis()}"
) : AudioEffect {
    
    override val name = "Mizuiro Glitch"
    override val description = "Digital glitch effects with Web 1.0 vibes"
    override val category = EffectCategory.SPECIAL
    override var isEnabled = true
    
    // Parameters
    private var glitchIntensity = 0.3f // 0.0 = none, 1.0 = maximum
    private var digitalNoise = 0.2f // 0.0 = clean, 1.0 = noisy
    private var bitcrush = 0.4f // 0.0 = high quality, 1.0 = 8-bit
    private var scanline = 0.3f // 0.0 = none, 1.0 = strong
    private var nostalgia = 0.5f // 0.0 = modern, 1.0 = retro
    private var fragility = 0.4f // 0.0 = stable, 1.0 = fragile
    
    // Internal state
    private var phase = 0.0
    private var glitchPhase = 0.0
    private var sampleRate = 44100
    private val glitchBuffer = mutableListOf<Float>()
    private var glitchCounter = 0
    
    override val parameters: Map<String, EffectParameter>
        get() = mapOf(
            "glitch_intensity" to EffectParameter(
                name = "Glitch Intensity",
                value = glitchIntensity,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.3f,
                unit = ParameterUnit.PERCENT,
                description = "Intensity of glitch effects"
            ),
            "digital_noise" to EffectParameter(
                name = "Digital Noise",
                value = digitalNoise,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.2f,
                unit = ParameterUnit.PERCENT,
                description = "Digital noise and artifacts"
            ),
            "bitcrush" to EffectParameter(
                name = "Bitcrush",
                value = bitcrush,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.4f,
                unit = ParameterUnit.PERCENT,
                description = "Bit depth reduction"
            ),
            "scanline" to EffectParameter(
                name = "Scanline",
                value = scanline,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.3f,
                unit = ParameterUnit.PERCENT,
                description = "CRT scanline effect"
            ),
            "nostalgia" to EffectParameter(
                name = "Nostalgia",
                value = nostalgia,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.5f,
                unit = ParameterUnit.PERCENT,
                description = "Nostalgic Web 1.0 feel"
            ),
            "fragility" to EffectParameter(
                name = "Fragility",
                value = fragility,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.4f,
                unit = ParameterUnit.PERCENT,
                description = "Fragile, unstable sound"
            )
        )
    
    override fun process(input: FloatArray, sampleRate: Int): FloatArray {
        this.sampleRate = sampleRate
        val output = FloatArray(input.size)
        
        for (i in input.indices) {
            // Apply bitcrushing
            val bitcrushed = applyBitcrush(input[i])
            
            // Apply digital noise
            val noisy = applyDigitalNoise(bitcrushed)
            
            // Apply glitch effects
            val glitched = applyGlitch(noisy)
            
            // Apply scanline effect
            val scanlined = applyScanline(glitched)
            
            // Apply nostalgia filter
            val nostalgic = applyNostalgia(scanlined)
            
            // Apply fragility
            val fragile = applyFragility(nostalgic)
            
            output[i] = fragile
        }
        
        return output
    }
    
    private fun applyBitcrush(sample: Float): Float {
        if (bitcrush > 0.0f) {
            val bits = (16.0f - bitcrush * 8.0f).toInt().coerceAtLeast(1)
            val maxValue = (1 shl (bits - 1)) - 1
            val quantized = (sample * maxValue).roundToInt().toFloat() / maxValue
            return sample * (1.0f - bitcrush) + quantized * bitcrush
        }
        return sample
    }
    
    private fun applyDigitalNoise(sample: Float): Float {
        val noise = (Math.random() - 0.5).toFloat() * digitalNoise * 0.1f
        return sample + noise
    }
    
    private fun applyGlitch(sample: Float): Float {
        glitchPhase += 2.0 * PI * 0.1 / sampleRate // 0.1 Hz glitch rate
        if (glitchPhase >= 2.0 * PI) {
            glitchPhase -= 2.0 * PI
        }
        
        val glitchTrigger = sin(glitchPhase).toFloat()
        if (glitchTrigger > 0.8f && glitchIntensity > 0.0f) {
            // Apply glitch
            val glitchAmount = glitchIntensity * 0.5f
            val glitchSample = (Math.random() - 0.5).toFloat() * glitchAmount
            return sample + glitchSample
        }
        return sample
    }
    
    private fun applyScanline(sample: Float): Float {
        if (scanline > 0.0f) {
            val scanlineFreq = 60.0 // 60 Hz scanline
            val scanlineMod = sin(phase * scanlineFreq / sampleRate * 2.0 * PI).toFloat()
            val scanlineEffect = 1.0f - scanline * 0.3f + scanlineMod * scanline * 0.2f
            return sample * scanlineEffect
        }
        return sample
    }
    
    private fun applyNostalgia(sample: Float): Float {
        if (nostalgia > 0.0f) {
            // Add nostalgic filtering
            val nostalgiaFilter = 1.0f - nostalgia * 0.2f
            val nostalgiaHarmonic = sin(phase * 0.5).toFloat() * nostalgia * 0.1f
            return sample * nostalgiaFilter + nostalgiaHarmonic
        }
        return sample
    }
    
    private fun applyFragility(sample: Float): Float {
        if (fragility > 0.0f) {
            // Add fragile, unstable characteristics
            val fragileMod = (Math.random() - 0.5).toFloat() * fragility * 0.05f
            val fragileTremolo = sin(phase * 0.1).toFloat() * fragility * 0.1f
            return sample * (1.0f + fragileMod + fragileTremolo)
        }
        return sample
    }
    
    override fun reset() {
        phase = 0.0
        glitchPhase = 0.0
        glitchBuffer.clear()
        glitchCounter = 0
    }
    
    override fun clone(): AudioEffect {
        val clone = MizuiroGlitchEffect()
        // Set parameters
        return clone
    }
}

/**
 * Kawaii Boost Effect
 * 
 * Enhances the kawaii (cute) characteristics of audio
 * with sparkle, brightness, and adorable processing.
 */
class KawaiiBoostEffect(
    override val id: String = "kawaii_boost_${System.currentTimeMillis()}"
) : AudioEffect {
    
    override val name = "Kawaii Boost"
    override val description = "Enhance kawaii characteristics (◠‿◠)"
    override val category = EffectCategory.SPECIAL
    override var isEnabled = true
    
    // Parameters
    private var kawaiiLevel = 0.5f // 0.0 = normal, 1.0 = maximum kawaii
    private var sparkle = 0.6f // 0.0 = none, 1.0 = sparkly
    private var brightness = 0.7f // 0.0 = dark, 1.0 = bright
    private var cuteness = 0.8f // 0.0 = normal, 1.0 = cute
    private var sweetness = 0.6f // 0.0 = neutral, 1.0 = sweet
    private var innocence = 0.5f // 0.0 = mature, 1.0 = innocent
    
    // Internal state
    private var phase = 0.0
    private var sparklePhase = 0.0
    private var sampleRate = 44100
    
    override val parameters: Map<String, EffectParameter>
        get() = mapOf(
            "kawaii_level" to EffectParameter(
                name = "Kawaii Level",
                value = kawaiiLevel,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.5f,
                unit = ParameterUnit.PERCENT,
                description = "Overall kawaii intensity (◠‿◠)"
            ),
            "sparkle" to EffectParameter(
                name = "Sparkle",
                value = sparkle,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.6f,
                unit = ParameterUnit.PERCENT,
                description = "Sparkly, twinkling effect"
            ),
            "brightness" to EffectParameter(
                name = "Brightness",
                value = brightness,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.7f,
                unit = ParameterUnit.PERCENT,
                description = "Bright, cheerful sound"
            ),
            "cuteness" to EffectParameter(
                name = "Cuteness",
                value = cuteness,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.8f,
                unit = ParameterUnit.PERCENT,
                description = "Cute, adorable processing"
            ),
            "sweetness" to EffectParameter(
                name = "Sweetness",
                value = sweetness,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.6f,
                unit = ParameterUnit.PERCENT,
                description = "Sweet, pleasant sound"
            ),
            "innocence" to EffectParameter(
                name = "Innocence",
                value = innocence,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.5f,
                unit = ParameterUnit.PERCENT,
                description = "Innocent, pure sound"
            )
        )
    
    override fun process(input: FloatArray, sampleRate: Int): FloatArray {
        this.sampleRate = sampleRate
        val output = FloatArray(input.size)
        
        for (i in input.indices) {
            // Apply kawaii boost
            val kawaiiBoosted = applyKawaiiBoost(input[i])
            
            // Apply sparkle
            val sparkled = applySparkle(kawaiiBoosted)
            
            // Apply brightness
            val brightened = applyBrightness(sparkled)
            
            // Apply cuteness
            val cute = applyCuteness(brightened)
            
            // Apply sweetness
            val sweet = applySweetness(cute)
            
            // Apply innocence
            val innocent = applyInnocence(sweet)
            
            output[i] = innocent
        }
        
        return output
    }
    
    private fun applyKawaiiBoost(sample: Float): Float {
        val boost = 1.0f + kawaiiLevel * 0.3f
        val kawaiiHarmonic = sin(phase * 2.0).toFloat() * kawaiiLevel * 0.1f
        return sample * boost + kawaiiHarmonic
    }
    
    private fun applySparkle(sample: Float): Float {
        sparklePhase += 2.0 * PI * 8.0 / sampleRate // 8 Hz sparkle
        if (sparklePhase >= 2.0 * PI) {
            sparklePhase -= 2.0 * PI
        }
        
        val sparkleMod = sin(sparklePhase).toFloat() * sparkle * 0.15f
        val sparkleHarmonic = sin(sparklePhase * 3.0).toFloat() * sparkle * 0.05f
        return sample + sparkleMod + sparkleHarmonic
    }
    
    private fun applyBrightness(sample: Float): Float {
        val brightnessFactor = 1.0f + brightness * 0.4f
        val brightHarmonic = sin(phase * 4.0).toFloat() * brightness * 0.1f
        return sample * brightnessFactor + brightHarmonic
    }
    
    private fun applyCuteness(sample: Float): Float {
        val cuteBoost = 1.0f + cuteness * 0.2f
        val cuteHarmonic = sin(phase * 1.5).toFloat() * cuteness * 0.08f
        return sample * cuteBoost + cuteHarmonic
    }
    
    private fun applySweetness(sample: Float): Float {
        val sweetFilter = 1.0f + sweetness * 0.15f
        val sweetHarmonic = sin(phase * 0.8).toFloat() * sweetness * 0.06f
        return sample * sweetFilter + sweetHarmonic
    }
    
    private fun applyInnocence(sample: Float): Float {
        val innocentFilter = 1.0f + innocence * 0.1f
        val innocentHarmonic = sin(phase * 0.5).toFloat() * innocence * 0.04f
        return sample * innocentFilter + innocentHarmonic
    }
    
    override fun reset() {
        phase = 0.0
        sparklePhase = 0.0
    }
    
    override fun clone(): AudioEffect {
        val clone = KawaiiBoostEffect()
        // Set parameters
        return clone
    }
}

/**
 * Nostalgia Filter Effect
 * 
 * Adds nostalgic, melancholic filtering reminiscent of
 * early digital era and Web 1.0 aesthetics.
 */
class NostalgiaFilterEffect(
    override val id: String = "nostalgia_filter_${System.currentTimeMillis()}"
) : AudioEffect {
    
    override val name = "Nostalgia Filter"
    override val description = "Nostalgic filtering with Web 1.0 vibes"
    override val category = EffectCategory.SPECIAL
    override var isEnabled = true
    
    // Parameters
    private var nostalgiaLevel = 0.6f // 0.0 = modern, 1.0 = nostalgic
    private var melancholy = 0.5f // 0.0 = happy, 1.0 = melancholic
    private var digitalAge = 0.4f // 0.0 = analog, 1.0 = digital
    private var fragility = 0.3f // 0.0 = robust, 1.0 = fragile
    private var distance = 0.2f // 0.0 = close, 1.0 = distant
    private var warmth = 0.7f // 0.0 = cold, 1.0 = warm
    
    // Internal state
    private var phase = 0.0
    private var sampleRate = 44100
    private val filterBuffer = mutableListOf<Float>()
    
    override val parameters: Map<String, EffectParameter>
        get() = mapOf(
            "nostalgia_level" to EffectParameter(
                name = "Nostalgia Level",
                value = nostalgiaLevel,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.6f,
                unit = ParameterUnit.PERCENT,
                description = "Overall nostalgia intensity"
            ),
            "melancholy" to EffectParameter(
                name = "Melancholy",
                value = melancholy,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.5f,
                unit = ParameterUnit.PERCENT,
                description = "Melancholic, sad beauty"
            ),
            "digital_age" to EffectParameter(
                name = "Digital Age",
                value = digitalAge,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.4f,
                unit = ParameterUnit.PERCENT,
                description = "Early digital era feel"
            ),
            "fragility" to EffectParameter(
                name = "Fragility",
                value = fragility,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.3f,
                unit = ParameterUnit.PERCENT,
                description = "Fragile, delicate sound"
            ),
            "distance" to EffectParameter(
                name = "Distance",
                value = distance,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.2f,
                unit = ParameterUnit.PERCENT,
                description = "Distant, faraway sound"
            ),
            "warmth" to EffectParameter(
                name = "Warmth",
                value = warmth,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.7f,
                unit = ParameterUnit.PERCENT,
                description = "Warm, comforting sound"
            )
        )
    
    override fun process(input: FloatArray, sampleRate: Int): FloatArray {
        this.sampleRate = sampleRate
        val output = FloatArray(input.size)
        
        for (i in input.indices) {
            // Apply nostalgia filter
            val nostalgic = applyNostalgiaFilter(input[i])
            
            // Apply melancholy
            val melancholic = applyMelancholy(nostalgic)
            
            // Apply digital age
            val digital = applyDigitalAge(melancholic)
            
            // Apply fragility
            val fragile = applyFragility(digital)
            
            // Apply distance
            val distant = applyDistance(fragile)
            
            // Apply warmth
            val warm = applyWarmth(distant)
            
            output[i] = warm
        }
        
        return output
    }
    
    private fun applyNostalgiaFilter(sample: Float): Float {
        val nostalgiaFilter = 1.0f - nostalgiaLevel * 0.3f
        val nostalgiaHarmonic = sin(phase * 0.3).toFloat() * nostalgiaLevel * 0.1f
        return sample * nostalgiaFilter + nostalgiaHarmonic
    }
    
    private fun applyMelancholy(sample: Float): Float {
        val melancholyFilter = 1.0f - melancholy * 0.2f
        val melancholyHarmonic = sin(phase * 0.2).toFloat() * melancholy * 0.08f
        return sample * melancholyFilter + melancholyHarmonic
    }
    
    private fun applyDigitalAge(sample: Float): Float {
        val digitalFilter = 1.0f - digitalAge * 0.25f
        val digitalHarmonic = sin(phase * 0.1).toFloat() * digitalAge * 0.06f
        return sample * digitalFilter + digitalHarmonic
    }
    
    private fun applyFragility(sample: Float): Float {
        val fragileMod = (Math.random() - 0.5).toFloat() * fragility * 0.02f
        val fragileTremolo = sin(phase * 0.05).toFloat() * fragility * 0.04f
        return sample * (1.0f + fragileMod + fragileTremolo)
    }
    
    private fun applyDistance(sample: Float): Float {
        val distanceFilter = 1.0f - distance * 0.4f
        val distanceReverb = sin(phase * 0.01).toFloat() * distance * 0.05f
        return sample * distanceFilter + distanceReverb
    }
    
    private fun applyWarmth(sample: Float): Float {
        val warmthBoost = 1.0f + warmth * 0.2f
        val warmthHarmonic = sin(phase * 0.1).toFloat() * warmth * 0.06f
        return sample * warmthBoost + warmthHarmonic
    }
    
    override fun reset() {
        phase = 0.0
        filterBuffer.clear()
    }
    
    override fun clone(): AudioEffect {
        val clone = NostalgiaFilterEffect()
        // Set parameters
        return clone
    }
}

/**
 * Digital Dream Effect
 * 
 * Creates a dreamy, ethereal sound with digital processing
 * that embodies the Mizuiro aesthetic of digital nostalgia.
 */
class DigitalDreamEffect(
    override val id: String = "digital_dream_${System.currentTimeMillis()}"
) : AudioEffect {
    
    override val name = "Digital Dream"
    override val description = "Dreamy digital processing with ethereal vibes"
    override val category = EffectCategory.SPECIAL
    override var isEnabled = true
    
    // Parameters
    private var dreaminess = 0.7f // 0.0 = realistic, 1.0 = dreamy
    private var ethereal = 0.6f // 0.0 = grounded, 1.0 = ethereal
    private var digitalGlow = 0.5f // 0.0 = none, 1.0 = glowing
    private var nostalgia = 0.4f // 0.0 = present, 1.0 = nostalgic
    private var fragility = 0.3f // 0.0 = stable, 1.0 = fragile
    private var beauty = 0.8f // 0.0 = plain, 1.0 = beautiful
    
    // Internal state
    private var phase = 0.0
    private var dreamPhase = 0.0
    private var sampleRate = 44100
    
    override val parameters: Map<String, EffectParameter>
        get() = mapOf(
            "dreaminess" to EffectParameter(
                name = "Dreaminess",
                value = dreaminess,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.7f,
                unit = ParameterUnit.PERCENT,
                description = "Dreamy, surreal quality"
            ),
            "ethereal" to EffectParameter(
                name = "Ethereal",
                value = ethereal,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.6f,
                unit = ParameterUnit.PERCENT,
                description = "Ethereal, otherworldly sound"
            ),
            "digital_glow" to EffectParameter(
                name = "Digital Glow",
                value = digitalGlow,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.5f,
                unit = ParameterUnit.PERCENT,
                description = "Digital glow and shimmer"
            ),
            "nostalgia" to EffectParameter(
                name = "Nostalgia",
                value = nostalgia,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.4f,
                unit = ParameterUnit.PERCENT,
                description = "Nostalgic, wistful quality"
            ),
            "fragility" to EffectParameter(
                name = "Fragility",
                value = fragility,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.3f,
                unit = ParameterUnit.PERCENT,
                description = "Fragile, delicate beauty"
            ),
            "beauty" to EffectParameter(
                name = "Beauty",
                value = beauty,
                minValue = 0.0f,
                maxValue = 1.0f,
                defaultValue = 0.8f,
                unit = ParameterUnit.PERCENT,
                description = "Beautiful, aesthetic quality"
            )
        )
    
    override fun process(input: FloatArray, sampleRate: Int): FloatArray {
        this.sampleRate = sampleRate
        val output = FloatArray(input.size)
        
        for (i in input.indices) {
            // Apply dreaminess
            val dreamy = applyDreaminess(input[i])
            
            // Apply ethereal processing
            val ethereal = applyEthereal(dreamy)
            
            // Apply digital glow
            val glowing = applyDigitalGlow(ethereal)
            
            // Apply nostalgia
            val nostalgic = applyNostalgia(glowing)
            
            // Apply fragility
            val fragile = applyFragility(nostalgic)
            
            // Apply beauty
            val beautiful = applyBeauty(fragile)
            
            output[i] = beautiful
        }
        
        return output
    }
    
    private fun applyDreaminess(sample: Float): Float {
        val dreamFilter = 1.0f + dreaminess * 0.3f
        val dreamHarmonic = sin(phase * 0.5).toFloat() * dreaminess * 0.1f
        return sample * dreamFilter + dreamHarmonic
    }
    
    private fun applyEthereal(sample: Float): Float {
        val etherealFilter = 1.0f + ethereal * 0.2f
        val etherealHarmonic = sin(phase * 0.3).toFloat() * ethereal * 0.08f
        return sample * etherealFilter + etherealHarmonic
    }
    
    private fun applyDigitalGlow(sample: Float): Float {
        dreamPhase += 2.0 * PI * 2.0 / sampleRate // 2 Hz glow
        if (dreamPhase >= 2.0 * PI) {
            dreamPhase -= 2.0 * PI
        }
        
        val glowMod = sin(dreamPhase).toFloat() * digitalGlow * 0.1f
        val glowHarmonic = sin(dreamPhase * 2.0).toFloat() * digitalGlow * 0.05f
        return sample + glowMod + glowHarmonic
    }
    
    private fun applyNostalgia(sample: Float): Float {
        val nostalgiaFilter = 1.0f - nostalgia * 0.15f
        val nostalgiaHarmonic = sin(phase * 0.1).toFloat() * nostalgia * 0.06f
        return sample * nostalgiaFilter + nostalgiaHarmonic
    }
    
    private fun applyFragility(sample: Float): Float {
        val fragileMod = (Math.random() - 0.5).toFloat() * fragility * 0.01f
        val fragileTremolo = sin(phase * 0.02).toFloat() * fragility * 0.03f
        return sample * (1.0f + fragileMod + fragileTremolo)
    }
    
    private fun applyBeauty(sample: Float): Float {
        val beautyBoost = 1.0f + beauty * 0.25f
        val beautyHarmonic = sin(phase * 0.2).toFloat() * beauty * 0.08f
        return sample * beautyBoost + beautyHarmonic
    }
    
    override fun reset() {
        phase = 0.0
        dreamPhase = 0.0
    }
    
    override fun clone(): AudioEffect {
        val clone = DigitalDreamEffect()
        // Set parameters
        return clone
    }
}
