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
 * Memory Lane Manager
 * 
 * Creates a visual timeline of your music journey
 * with beautiful Mizuiro aesthetic
 */
@Singleton
class MemoryLane @Inject constructor() {
    
    private val _memoryLaneState = MutableStateFlow(MemoryLaneState())
    val memoryLaneState: StateFlow<MemoryLaneState> = _memoryLaneState.asStateFlow()
    
    private val _memories = MutableStateFlow<List<MusicMemory>>(emptyList())
    val memories: StateFlow<List<MusicMemory>> = _memories.asStateFlow()
    
    /**
     * Add a new memory
     */
    fun addMemory(memory: MusicMemory) {
        val currentMemories = _memories.value.toMutableList()
        currentMemories.add(memory)
        _memories.value = currentMemories.sortedBy { it.timestamp }
        
        _memoryLaneState.value = _memoryLaneState.value.copy(
            totalMemories = currentMemories.size,
            lastMemory = memory
        )
    }
    
    /**
     * Get memories for a specific time period
     */
    fun getMemoriesForPeriod(startTime: Long, endTime: Long): List<MusicMemory> {
        return _memories.value.filter { 
            it.timestamp in startTime..endTime 
        }
    }
    
    /**
     * Get memories by emotion
     */
    fun getMemoriesByEmotion(emotion: MemoryEmotion): List<MusicMemory> {
        return _memories.value.filter { it.emotion == emotion }
    }
    
    /**
     * Get memory statistics
     */
    fun getMemoryStatistics(): MemoryStatistics {
        val memories = _memories.value
        return MemoryStatistics(
            totalMemories = memories.size,
            happyMemories = memories.count { it.emotion == MemoryEmotion.HAPPY },
            sadMemories = memories.count { it.emotion == MemoryEmotion.SAD },
            nostalgicMemories = memories.count { it.emotion == MemoryEmotion.NOSTALGIC },
            energeticMemories = memories.count { it.emotion == MemoryEmotion.ENERGETIC },
            calmMemories = memories.count { it.emotion == MemoryEmotion.CALM },
            averageEmotion = calculateAverageEmotion(memories)
        )
    }
    
    /**
     * Calculate average emotion
     */
    private fun calculateAverageEmotion(memories: List<MusicMemory>): MemoryEmotion {
        if (memories.isEmpty()) return MemoryEmotion.NEUTRAL
        
        val emotionCounts = memories.groupingBy { it.emotion }.eachCount()
        return emotionCounts.maxByOrNull { it.value }?.key ?: MemoryEmotion.NEUTRAL
    }
    
    /**
     * Get memory lane visualization data
     */
    fun getMemoryLaneData(): MemoryLaneData {
        val memories = _memories.value
        val statistics = getMemoryStatistics()
        
        return MemoryLaneData(
            memories = memories,
            statistics = statistics,
            timeline = createTimeline(memories),
            emotions = createEmotionMap(memories)
        )
    }
    
    /**
     * Create timeline visualization
     */
    private fun createTimeline(memories: List<MusicMemory>): List<TimelinePoint> {
        return memories.mapIndexed { index, memory ->
            TimelinePoint(
                x = index.toFloat() / memories.size.coerceAtLeast(1),
                y = getEmotionY(memory.emotion),
                memory = memory,
                size = getMemorySize(memory),
                color = getMemoryColor(memory.emotion)
            )
        }
    }
    
    /**
     * Create emotion map
     */
    private fun createEmotionMap(memories: List<MusicMemory>): Map<MemoryEmotion, Int> {
        return memories.groupingBy { it.emotion }.eachCount()
    }
    
    /**
     * Get emotion Y position
     */
    private fun getEmotionY(emotion: MemoryEmotion): Float {
        return when (emotion) {
            MemoryEmotion.HAPPY -> 0.2f
            MemoryEmotion.ENERGETIC -> 0.1f
            MemoryEmotion.NEUTRAL -> 0.5f
            MemoryEmotion.CALM -> 0.7f
            MemoryEmotion.NOSTALGIC -> 0.8f
            MemoryEmotion.SAD -> 0.9f
        }
    }
    
    /**
     * Get memory size based on importance
     */
    private fun getMemorySize(memory: MusicMemory): Float {
        return when (memory.importance) {
            MemoryImportance.LOW -> 0.5f
            MemoryImportance.MEDIUM -> 0.7f
            MemoryImportance.HIGH -> 1.0f
        }
    }
    
    /**
     * Get memory color based on emotion
     */
    private fun getMemoryColor(emotion: MemoryEmotion): Color {
        return when (emotion) {
            MemoryEmotion.HAPPY -> Color(0xFFFF69B4)
            MemoryEmotion.ENERGETIC -> Color(0xFFFF1493)
            MemoryEmotion.NEUTRAL -> Color(0xFF87CEEB)
            MemoryEmotion.CALM -> Color(0xFF4682B4)
            MemoryEmotion.NOSTALGIC -> Color(0xFF9370DB)
            MemoryEmotion.SAD -> Color(0xFF2F4F4F)
        }
    }
}

/**
 * Memory Lane Canvas Composable
 */
@Composable
fun MemoryLaneCanvas(
    modifier: Modifier = Modifier,
    memoryLaneData: MemoryLaneData,
    selectedMemory: MusicMemory? = null
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animationPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        drawMemoryLane(
            memoryLaneData = memoryLaneData,
            selectedMemory = selectedMemory,
            animationPhase = animationPhase
        )
    }
}

/**
 * Draw memory lane visualization
 */
private fun DrawScope.drawMemoryLane(
    memoryLaneData: MemoryLaneData,
    selectedMemory: MusicMemory?,
    animationPhase: Float
) {
    val centerY = size.height / 2
    val timelineY = centerY
    
    // Draw timeline
    drawLine(
        color = Color(0xFF87CEEB).copy(alpha = 0.6f),
        start = Offset(0f, timelineY),
        end = Offset(size.width, timelineY),
        strokeWidth = 3f
    )
    
    // Draw memories
    memoryLaneData.timeline.forEach { point ->
        val x = point.x * size.width
        val y = point.y * size.height
        
        // Draw memory point
        val isSelected = selectedMemory?.id == point.memory.id
        val pointSize = point.size * 20f * (if (isSelected) 1.5f else 1f)
        val pointColor = if (isSelected) {
            point.color.copy(alpha = 1f)
        } else {
            point.color.copy(alpha = 0.7f)
        }
        
        drawCircle(
            color = pointColor,
            radius = pointSize,
            center = Offset(x, y)
        )
        
        // Draw connection line to timeline
        drawLine(
            color = point.color.copy(alpha = 0.4f),
            start = Offset(x, y),
            end = Offset(x, timelineY),
            strokeWidth = 2f
        )
        
        // Draw memory glow effect
        if (isSelected) {
            drawCircle(
                color = point.color.copy(alpha = 0.3f),
                radius = pointSize * 2f,
                center = Offset(x, y)
            )
        }
    }
    
    // Draw emotion waves
    drawEmotionWaves(memoryLaneData.emotions, animationPhase)
}

/**
 * Draw emotion waves
 */
private fun DrawScope.drawEmotionWaves(
    emotions: Map<MemoryEmotion, Int>,
    animationPhase: Float
) {
    val centerY = size.height / 2
    val waveHeight = 30f
    
    emotions.forEach { (emotion, count) ->
        val color = getEmotionColor(emotion)
        val y = centerY + getEmotionY(emotion) * size.height - centerY
        
        val path = Path().apply {
            moveTo(0f, y)
            
            for (x in 0..size.width.toInt() step 4) {
                val waveY = y + kotlin.math.sin(
                    (x * 0.01f + animationPhase * 2 * kotlin.math.PI) * count
                ).toFloat() * waveHeight
                lineTo(x.toFloat(), waveY)
            }
        }
        
        drawPath(
            path = path,
            color = color.copy(alpha = 0.3f),
            style = Stroke(width = 2f)
        )
    }
}

/**
 * Get emotion color
 */
private fun getEmotionColor(emotion: MemoryEmotion): Color {
    return when (emotion) {
        MemoryEmotion.HAPPY -> Color(0xFFFF69B4)
        MemoryEmotion.ENERGETIC -> Color(0xFFFF1493)
        MemoryEmotion.NEUTRAL -> Color(0xFF87CEEB)
        MemoryEmotion.CALM -> Color(0xFF4682B4)
        MemoryEmotion.NOSTALGIC -> Color(0xFF9370DB)
        MemoryEmotion.SAD -> Color(0xFF2F4F4F)
    }
}

/**
 * Get emotion Y position
 */
private fun getEmotionY(emotion: MemoryEmotion): Float {
    return when (emotion) {
        MemoryEmotion.HAPPY -> 0.2f
        MemoryEmotion.ENERGETIC -> 0.1f
        MemoryEmotion.NEUTRAL -> 0.5f
        MemoryEmotion.CALM -> 0.7f
        MemoryEmotion.NOSTALGIC -> 0.8f
        MemoryEmotion.SAD -> 0.9f
    }
}

/**
 * Memory lane state data class
 */
data class MemoryLaneState(
    val totalMemories: Int = 0,
    val lastMemory: MusicMemory? = null,
    val isVisible: Boolean = true
)

/**
 * Music memory data class
 */
data class MusicMemory(
    val id: String,
    val trackTitle: String,
    val artist: String,
    val timestamp: Long,
    val emotion: MemoryEmotion,
    val importance: MemoryImportance,
    val description: String,
    val tags: List<String> = emptyList()
)

/**
 * Memory emotion enum
 */
enum class MemoryEmotion {
    HAPPY, SAD, NOSTALGIC, ENERGETIC, CALM, NEUTRAL
}

/**
 * Memory importance enum
 */
enum class MemoryImportance {
    LOW, MEDIUM, HIGH
}

/**
 * Memory statistics data class
 */
data class MemoryStatistics(
    val totalMemories: Int,
    val happyMemories: Int,
    val sadMemories: Int,
    val nostalgicMemories: Int,
    val energeticMemories: Int,
    val calmMemories: Int,
    val averageEmotion: MemoryEmotion
)

/**
 * Memory lane data class
 */
data class MemoryLaneData(
    val memories: List<MusicMemory>,
    val statistics: MemoryStatistics,
    val timeline: List<TimelinePoint>,
    val emotions: Map<MemoryEmotion, Int>
)

/**
 * Timeline point data class
 */
data class TimelinePoint(
    val x: Float,
    val y: Float,
    val memory: MusicMemory,
    val size: Float,
    val color: Color
)
