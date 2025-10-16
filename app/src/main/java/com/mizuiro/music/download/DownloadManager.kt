package com.mizuiro.music.download

import android.content.Context
import android.net.Uri
import androidx.work.*
import com.mizuiro.music.data.model.Track
import com.mizuiro.music.data.model.Album
import com.mizuiro.music.data.model.Playlist
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Smart Download Manager
 * 
 * Handles intelligent downloading of music for offline listening
 * with Mizuiro aesthetic and user experience
 */
@Singleton
class DownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadRepository: DownloadRepository,
    private val workManager: WorkManager
) {
    
    private val _downloadState = MutableStateFlow(DownloadState())
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()
    
    private val _downloadQueue = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloadQueue: StateFlow<List<DownloadItem>> = _downloadQueue.asStateFlow()
    
    private val _downloadedTracks = MutableStateFlow<List<Track>>(emptyList())
    val downloadedTracks: StateFlow<List<Track>> = _downloadedTracks.asStateFlow()
    
    private val _downloadProgress = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, DownloadProgress>> = _downloadProgress.asStateFlow()
    
    init {
        // Load existing downloads
        loadDownloadedTracks()
        loadDownloadQueue()
    }
    
    /**
     * Smart download recommendations based on user behavior
     */
    suspend fun getSmartDownloadRecommendations(): List<DownloadRecommendation> {
        val recentlyPlayed = downloadRepository.getRecentlyPlayedTracks()
        val likedTracks = downloadRepository.getLikedTracks()
        val userPreferences = downloadRepository.getUserPreferences()
        
        val recommendations = mutableListOf<DownloadRecommendation>()
        
        // High priority: Recently played and liked tracks
        recentlyPlayed.take(10).forEach { track ->
            if (!isDownloaded(track.id) && !isInQueue(track.id)) {
                recommendations.add(
                    DownloadRecommendation(
                        track = track,
                        priority = DownloadPriority.HIGH,
                        reason = "Recently played",
                        estimatedSize = estimateTrackSize(track)
                    )
                )
            }
        }
        
        // Medium priority: Liked tracks from favorite artists
        likedTracks.take(20).forEach { track ->
            if (!isDownloaded(track.id) && !isInQueue(track.id)) {
                recommendations.add(
                    DownloadRecommendation(
                        track = track,
                        priority = DownloadPriority.MEDIUM,
                        reason = "Liked track",
                        estimatedSize = estimateTrackSize(track)
                    )
                )
            }
        }
        
        // Low priority: Similar tracks based on preferences
        val similarTracks = downloadRepository.getSimilarTracks(userPreferences.favoriteGenres)
        similarTracks.take(15).forEach { track ->
            if (!isDownloaded(track.id) && !isInQueue(track.id)) {
                recommendations.add(
                    DownloadRecommendation(
                        track = track,
                        priority = DownloadPriority.LOW,
                        reason = "Similar to your taste",
                        estimatedSize = estimateTrackSize(track)
                    )
                )
            }
        }
        
        return recommendations.sortedBy { it.priority.ordinal }
    }
    
    /**
     * Download a single track
     */
    suspend fun downloadTrack(track: Track, priority: DownloadPriority = DownloadPriority.MEDIUM) {
        if (isDownloaded(track.id) || isInQueue(track.id)) {
            return
        }
        
        val downloadItem = DownloadItem(
            id = track.id,
            track = track,
            priority = priority,
            status = DownloadStatus.PENDING,
            dateAdded = System.currentTimeMillis(),
            estimatedSize = estimateTrackSize(track)
        )
        
        addToQueue(downloadItem)
        startDownload(downloadItem)
    }
    
    /**
     * Download an entire album
     */
    suspend fun downloadAlbum(album: Album, priority: DownloadPriority = DownloadPriority.MEDIUM) {
        val tracks = downloadRepository.getAlbumTracks(album.id)
        tracks.forEach { track ->
            downloadTrack(track, priority)
        }
    }
    
    /**
     * Download a playlist
     */
    suspend fun downloadPlaylist(playlist: Playlist, priority: DownloadPriority = DownloadPriority.MEDIUM) {
        val tracks = downloadRepository.getPlaylistTracks(playlist.id)
        tracks.forEach { track ->
            downloadTrack(track, priority)
        }
    }
    
    /**
     * Smart download based on user patterns
     */
    suspend fun smartDownload() {
        val recommendations = getSmartDownloadRecommendations()
        val availableSpace = getAvailableStorageSpace()
        val currentDownloads = _downloadQueue.value.sumOf { it.estimatedSize }
        
        var remainingSpace = availableSpace - currentDownloads
        var downloadedCount = 0
        val maxDownloads = 50 // Limit to prevent overwhelming the device
        
        for (recommendation in recommendations) {
            if (downloadedCount >= maxDownloads || remainingSpace < recommendation.estimatedSize) {
                break
            }
            
            downloadTrack(recommendation.track, recommendation.priority)
            remainingSpace -= recommendation.estimatedSize
            downloadedCount++
        }
    }
    
    /**
     * Start download process
     */
    private suspend fun startDownload(downloadItem: DownloadItem) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val downloadWork = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(
                workDataOf(
                    "track_id" to downloadItem.id,
                    "track_title" to downloadItem.track.title,
                    "track_artist" to downloadItem.track.artist,
                    "download_url" to downloadItem.track.filePath,
                    "priority" to downloadItem.priority.ordinal
                )
            )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
            .build()
        
        workManager.enqueue(downloadWork)
        
        updateDownloadStatus(downloadItem.id, DownloadStatus.DOWNLOADING)
    }
    
    /**
     * Pause download
     */
    suspend fun pauseDownload(trackId: String) {
        workManager.cancelAllWorkByTag(trackId)
        updateDownloadStatus(trackId, DownloadStatus.PAUSED)
    }
    
    /**
     * Resume download
     */
    suspend fun resumeDownload(trackId: String) {
        val downloadItem = _downloadQueue.value.find { it.id == trackId }
        downloadItem?.let { startDownload(it) }
    }
    
    /**
     * Cancel download
     */
    suspend fun cancelDownload(trackId: String) {
        workManager.cancelAllWorkByTag(trackId)
        removeFromQueue(trackId)
        updateDownloadStatus(trackId, DownloadStatus.CANCELLED)
    }
    
    /**
     * Delete downloaded track
     */
    suspend fun deleteDownloadedTrack(trackId: String) {
        val track = _downloadedTracks.value.find { it.id == trackId }
        track?.let {
            val file = File(it.filePath)
            if (file.exists()) {
                file.delete()
            }
            downloadRepository.removeDownloadedTrack(trackId)
            loadDownloadedTracks()
        }
    }
    
    /**
     * Clear all downloads
     */
    suspend fun clearAllDownloads() {
        _downloadedTracks.value.forEach { track ->
            val file = File(track.filePath)
            if (file.exists()) {
                file.delete()
            }
        }
        downloadRepository.clearAllDownloads()
        loadDownloadedTracks()
    }
    
    /**
     * Get download statistics
     */
    suspend fun getDownloadStats(): DownloadStats {
        val downloadedTracks = _downloadedTracks.value
        val totalSize = downloadedTracks.sumOf { estimateTrackSize(it) }
        val totalTracks = downloadedTracks.size
        val queueSize = _downloadQueue.value.size
        val availableSpace = getAvailableStorageSpace()
        
        return DownloadStats(
            totalDownloadedTracks = totalTracks,
            totalDownloadedSize = totalSize,
            queueSize = queueSize,
            availableSpace = availableSpace,
            lastDownloadTime = downloadedTracks.maxOfOrNull { it.dateAdded } ?: 0L
        )
    }
    
    /**
     * Check if track is downloaded
     */
    fun isDownloaded(trackId: String): Boolean {
        return _downloadedTracks.value.any { it.id == trackId }
    }
    
    /**
     * Check if track is in download queue
     */
    fun isInQueue(trackId: String): Boolean {
        return _downloadQueue.value.any { it.id == trackId }
    }
    
    /**
     * Get offline tracks
     */
    fun getOfflineTracks(): List<Track> {
        return _downloadedTracks.value
    }
    
    /**
     * Search offline tracks
     */
    fun searchOfflineTracks(query: String): List<Track> {
        return _downloadedTracks.value.filter { track ->
            track.title.contains(query, ignoreCase = true) ||
            track.artist.contains(query, ignoreCase = true) ||
            track.album.contains(query, ignoreCase = true)
        }
    }
    
    // Private helper methods
    
    private suspend fun addToQueue(downloadItem: DownloadItem) {
        val currentQueue = _downloadQueue.value.toMutableList()
        currentQueue.add(downloadItem)
        _downloadQueue.value = currentQueue.sortedBy { it.priority.ordinal }
    }
    
    private suspend fun removeFromQueue(trackId: String) {
        val currentQueue = _downloadQueue.value.toMutableList()
        currentQueue.removeAll { it.id == trackId }
        _downloadQueue.value = currentQueue
    }
    
    private suspend fun updateDownloadStatus(trackId: String, status: DownloadStatus) {
        val currentQueue = _downloadQueue.value.toMutableList()
        val index = currentQueue.indexOfFirst { it.id == trackId }
        if (index != -1) {
            currentQueue[index] = currentQueue[index].copy(status = status)
            _downloadQueue.value = currentQueue
        }
    }
    
    private suspend fun loadDownloadedTracks() {
        val tracks = downloadRepository.getDownloadedTracks()
        _downloadedTracks.value = tracks
    }
    
    private suspend fun loadDownloadQueue() {
        val queue = downloadRepository.getDownloadQueue()
        _downloadQueue.value = queue
    }
    
    private fun estimateTrackSize(track: Track): Long {
        // Estimate based on duration (roughly 1MB per minute for high quality)
        return (track.duration / 1000 / 60) * 1024 * 1024
    }
    
    private fun getAvailableStorageSpace(): Long {
        val downloadDir = File(context.getExternalFilesDir(null), "downloads")
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }
        return downloadDir.usableSpace
    }
    
    /**
     * Update download progress
     */
    fun updateProgress(trackId: String, progress: DownloadProgress) {
        val currentProgress = _downloadProgress.value.toMutableMap()
        currentProgress[trackId] = progress
        _downloadProgress.value = currentProgress
    }
    
    /**
     * Complete download
     */
    suspend fun completeDownload(trackId: String, localPath: String) {
        val downloadItem = _downloadQueue.value.find { it.id == trackId }
        downloadItem?.let { item ->
            val downloadedTrack = item.track.copy(filePath = localPath)
            downloadRepository.addDownloadedTrack(downloadedTrack)
            removeFromQueue(trackId)
            loadDownloadedTracks()
        }
    }
}

/**
 * Download state data class
 */
data class DownloadState(
    val isDownloading: Boolean = false,
    val totalDownloads: Int = 0,
    val completedDownloads: Int = 0,
    val failedDownloads: Int = 0,
    val totalSize: Long = 0L,
    val downloadedSize: Long = 0L
)

/**
 * Download item data class
 */
data class DownloadItem(
    val id: String,
    val track: Track,
    val priority: DownloadPriority,
    val status: DownloadStatus,
    val dateAdded: Long,
    val estimatedSize: Long,
    val downloadedSize: Long = 0L,
    val progress: Float = 0f
)

/**
 * Download progress data class
 */
data class DownloadProgress(
    val trackId: String,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val progress: Float,
    val speed: Long, // bytes per second
    val timeRemaining: Long // milliseconds
)

/**
 * Download recommendation data class
 */
data class DownloadRecommendation(
    val track: Track,
    val priority: DownloadPriority,
    val reason: String,
    val estimatedSize: Long
)

/**
 * Download statistics data class
 */
data class DownloadStats(
    val totalDownloadedTracks: Int,
    val totalDownloadedSize: Long,
    val queueSize: Int,
    val availableSpace: Long,
    val lastDownloadTime: Long
)

/**
 * Download priority enum
 */
enum class DownloadPriority {
    LOW, MEDIUM, HIGH, URGENT
}

/**
 * Download status enum
 */
enum class DownloadStatus {
    PENDING, DOWNLOADING, PAUSED, COMPLETED, FAILED, CANCELLED
}
