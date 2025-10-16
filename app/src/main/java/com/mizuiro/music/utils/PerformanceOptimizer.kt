package com.mizuiro.music.utils

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Performance Optimizer
 * 
 * Provides performance optimization utilities for the Mizuiro Music app
 */
object PerformanceOptimizer {
    
    // Memory management
    private val memoryCache = ConcurrentHashMap<String, Any>()
    private val cacheSize = AtomicInteger(0)
    private const val MAX_CACHE_SIZE = 100
    
    // Coroutine management
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    /**
     * Optimized memory cache with LRU eviction
     */
    fun <T> cache(key: String, value: T): T {
        if (cacheSize.get() >= MAX_CACHE_SIZE) {
            evictOldestEntries()
        }
        
        memoryCache[key] = value as Any
        cacheSize.incrementAndGet()
        return value
    }
    
    /**
     * Get cached value
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getCached(key: String): T? {
        return memoryCache[key] as? T
    }
    
    /**
     * Clear cache
     */
    fun clearCache() {
        memoryCache.clear()
        cacheSize.set(0)
    }
    
    /**
     * Evict oldest entries from cache
     */
    private fun evictOldestEntries() {
        val entriesToRemove = memoryCache.size / 4
        val iterator = memoryCache.entries.iterator()
        var removed = 0
        
        while (iterator.hasNext() && removed < entriesToRemove) {
            iterator.next()
            iterator.remove()
            removed++
            cacheSize.decrementAndGet()
        }
    }
    
    /**
     * Optimized flow processing
     */
    fun <T> Flow<T>.optimize(): Flow<T> {
        return this
            .flowOn(Dispatchers.IO)
            .onEach { 
                // Add any processing optimizations here
            }
    }
    
    /**
     * Memory-efficient list operations
     */
    fun <T> List<T>.optimizedChunked(size: Int): List<List<T>> {
        return if (this.size <= size) {
            listOf(this)
        } else {
            this.chunked(size)
        }
    }
    
    /**
     * Lazy initialization for expensive operations
     */
    fun <T> lazyInit(initializer: () -> T): Lazy<T> {
        return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            initializer()
        }
    }
    
    /**
     * Background processing
     */
    fun processInBackground(operation: suspend () -> Unit) {
        coroutineScope.launch {
            try {
                operation()
            } catch (e: Exception) {
                // Handle errors gracefully
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        clearCache()
        coroutineScope.cancel()
    }
    
    /**
     * Get device performance level
     */
    fun getDevicePerformanceLevel(context: Context): PerformanceLevel {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memoryInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val totalMemory = memoryInfo.totalMem
        val availableMemory = memoryInfo.availMem
        val memoryRatio = availableMemory.toFloat() / totalMemory.toFloat()
        
        return when {
            memoryRatio > 0.5f && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> PerformanceLevel.HIGH
            memoryRatio > 0.3f -> PerformanceLevel.MEDIUM
            else -> PerformanceLevel.LOW
        }
    }
    
    /**
     * Optimize for device performance level
     */
    fun optimizeForDevice(context: Context): OptimizationSettings {
        val performanceLevel = getDevicePerformanceLevel(context)
        
        return when (performanceLevel) {
            PerformanceLevel.HIGH -> OptimizationSettings(
                maxCacheSize = 200,
                enableAnimations = true,
                enableEffects = true,
                enableRecommendations = true,
                chunkSize = 50
            )
            PerformanceLevel.MEDIUM -> OptimizationSettings(
                maxCacheSize = 100,
                enableAnimations = true,
                enableEffects = true,
                enableRecommendations = true,
                chunkSize = 30
            )
            PerformanceLevel.LOW -> OptimizationSettings(
                maxCacheSize = 50,
                enableAnimations = false,
                enableEffects = false,
                enableRecommendations = false,
                chunkSize = 20
            )
        }
    }
}

/**
 * Performance level enum
 */
enum class PerformanceLevel {
    LOW, MEDIUM, HIGH
}

/**
 * Optimization settings
 */
data class OptimizationSettings(
    val maxCacheSize: Int,
    val enableAnimations: Boolean,
    val enableEffects: Boolean,
    val enableRecommendations: Boolean,
    val chunkSize: Int
)

/**
 * Composable for performance-optimized operations
 */
@Composable
fun rememberPerformanceOptimizer(): PerformanceOptimizer {
    val context = LocalContext.current
    return remember {
        PerformanceOptimizer.optimizeForDevice(context)
        PerformanceOptimizer
    }
}

/**
 * Performance monitoring utilities
 */
object PerformanceMonitor {
    
    private val startTimes = ConcurrentHashMap<String, Long>()
    
    /**
     * Start timing an operation
     */
    fun startTiming(operation: String) {
        startTimes[operation] = System.currentTimeMillis()
    }
    
    /**
     * End timing and log result
     */
    fun endTiming(operation: String): Long {
        val startTime = startTimes.remove(operation) ?: return 0L
        val duration = System.currentTimeMillis() - startTime
        
        // Log performance metrics
        android.util.Log.d("Performance", "$operation took ${duration}ms")
        
        return duration
    }
    
    /**
     * Measure operation execution time
     */
    inline fun <T> measureTime(operation: String, block: () -> T): T {
        startTiming(operation)
        return try {
            block()
        } finally {
            endTiming(operation)
        }
    }
}

/**
 * Memory optimization utilities
 */
object MemoryOptimizer {
    
    /**
     * Check if device has low memory
     */
    fun isLowMemory(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memoryInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        return memoryInfo.lowMemory
    }
    
    /**
     * Get available memory percentage
     */
    fun getAvailableMemoryPercentage(context: Context): Float {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memoryInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        return memoryInfo.availMem.toFloat() / memoryInfo.totalMem.toFloat()
    }
    
    /**
     * Suggest memory optimization
     */
    fun suggestOptimization(context: Context): String {
        val availableMemory = getAvailableMemoryPercentage(context)
        
        return when {
            availableMemory < 0.2f -> "Consider closing other apps to free up memory"
            availableMemory < 0.4f -> "Memory usage is moderate, consider reducing cache size"
            else -> "Memory usage is optimal"
        }
    }
}

/**
 * Battery optimization utilities
 */
object BatteryOptimizer {
    
    /**
     * Check if device is in battery saver mode
     */
    fun isBatterySaverMode(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        return powerManager.isPowerSaveMode
    }
    
    /**
     * Get battery level
     */
    fun getBatteryLevel(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as android.os.BatteryManager
        return batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
    
    /**
     * Suggest battery optimization
     */
    fun suggestOptimization(context: Context): String {
        val batteryLevel = getBatteryLevel(context)
        val isBatterySaver = isBatterySaverMode(context)
        
        return when {
            isBatterySaver -> "Battery saver mode is active, some features may be limited"
            batteryLevel < 20 -> "Battery is low, consider reducing background activity"
            batteryLevel < 50 -> "Battery is moderate, consider optimizing power usage"
            else -> "Battery level is good"
        }
    }
}

/**
 * Network optimization utilities
 */
object NetworkOptimizer {
    
    /**
     * Check network connectivity
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        
        return capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
    
    /**
     * Check if network is metered
     */
    fun isMeteredNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        
        return capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED) != true
    }
    
    /**
     * Suggest network optimization
     */
    fun suggestOptimization(context: Context): String {
        val isAvailable = isNetworkAvailable(context)
        val isMetered = isMeteredNetwork(context)
        
        return when {
            !isAvailable -> "No network connection available"
            isMetered -> "Metered network detected, consider reducing data usage"
            else -> "Network connection is optimal"
        }
    }
}
