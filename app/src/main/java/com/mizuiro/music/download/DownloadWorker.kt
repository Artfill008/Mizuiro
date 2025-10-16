package com.mizuiro.music.download

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

/**
 * Download Worker
 * 
 * Handles background downloading of music tracks
 */
class DownloadWorker @Inject constructor(
    @ApplicationContext context: Context,
    params: WorkerParameters,
    private val downloadManager: DownloadManager
) : CoroutineWorker(context, params) {
    
    companion object {
        private const val TAG = "DownloadWorker"
        private const val BUFFER_SIZE = 8192
        private const val PROGRESS_UPDATE_INTERVAL = 1000L // 1 second
    }
    
    override suspend fun doWork(): Result {
        val trackId = inputData.getString("track_id") ?: return Result.failure()
        val trackTitle = inputData.getString("track_title") ?: ""
        val trackArtist = inputData.getString("track_artist") ?: ""
        val downloadUrl = inputData.getString("download_url") ?: return Result.failure()
        val priority = inputData.getInt("priority", DownloadPriority.MEDIUM.ordinal)
        
        Log.d(TAG, "Starting download for track: $trackTitle by $trackArtist")
        
        return try {
            downloadTrack(trackId, trackTitle, trackArtist, downloadUrl)
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Download failed for track: $trackTitle", e)
            downloadManager.updateProgress(trackId, DownloadProgress(
                trackId = trackId,
                downloadedBytes = 0L,
                totalBytes = 0L,
                progress = 0f,
                speed = 0L,
                timeRemaining = 0L
            ))
            Result.failure()
        }
    }
    
    private suspend fun downloadTrack(
        trackId: String,
        trackTitle: String,
        trackArtist: String,
        downloadUrl: String
    ) {
        val url = URL(downloadUrl)
        val connection = url.openConnection() as HttpURLConnection
        
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 30000
            connection.readTimeout = 60000
            connection.connect()
            
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("HTTP error: $responseCode")
            }
            
            val contentLength = connection.contentLength
            val inputStream: InputStream = connection.inputStream
            
            // Create download directory
            val downloadDir = File(applicationContext.getExternalFilesDir(null), "downloads")
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            
            // Create file
            val fileName = "${trackArtist}_${trackTitle}".replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val file = File(downloadDir, "$fileName.mp3")
            val outputStream = FileOutputStream(file)
            
            val buffer = ByteArray(BUFFER_SIZE)
            var downloadedBytes = 0L
            var bytesRead: Int
            var lastUpdateTime = System.currentTimeMillis()
            var lastDownloadedBytes = 0L
            
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                downloadedBytes += bytesRead
                
                // Update progress periodically
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUpdateTime >= PROGRESS_UPDATE_INTERVAL) {
                    val speed = (downloadedBytes - lastDownloadedBytes) * 1000 / (currentTime - lastUpdateTime)
                    val progress = if (contentLength > 0) downloadedBytes.toFloat() / contentLength else 0f
                    val timeRemaining = if (speed > 0) (contentLength - downloadedBytes) * 1000 / speed else 0L
                    
                    downloadManager.updateProgress(trackId, DownloadProgress(
                        trackId = trackId,
                        downloadedBytes = downloadedBytes,
                        totalBytes = contentLength.toLong(),
                        progress = progress,
                        speed = speed,
                        timeRemaining = timeRemaining
                    ))
                    
                    lastUpdateTime = currentTime
                    lastDownloadedBytes = downloadedBytes
                }
                
                // Check if work is cancelled
                if (isStopped) {
                    Log.d(TAG, "Download cancelled for track: $trackTitle")
                    return
                }
            }
            
            outputStream.close()
            inputStream.close()
            
            // Complete download
            downloadManager.completeDownload(trackId, file.absolutePath)
            
            Log.d(TAG, "Download completed for track: $trackTitle")
            
        } finally {
            connection.disconnect()
        }
    }
}

/**
 * Mock Download Worker for testing
 * 
 * This simulates downloads for development and testing
 */
class MockDownloadWorker @Inject constructor(
    @ApplicationContext context: Context,
    params: WorkerParameters,
    private val downloadManager: DownloadManager
) : CoroutineWorker(context, params) {
    
    companion object {
        private const val TAG = "MockDownloadWorker"
        private const val MOCK_DOWNLOAD_DURATION = 5000L // 5 seconds
        private const val MOCK_FILE_SIZE = 1024 * 1024 * 3 // 3MB
    }
    
    override suspend fun doWork(): Result {
        val trackId = inputData.getString("track_id") ?: return Result.failure()
        val trackTitle = inputData.getString("track_title") ?: ""
        val trackArtist = inputData.getString("track_artist") ?: ""
        
        Log.d(TAG, "Starting mock download for track: $trackTitle by $trackArtist")
        
        return try {
            mockDownloadTrack(trackId, trackTitle, trackArtist)
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Mock download failed for track: $trackTitle", e)
            Result.failure()
        }
    }
    
    private suspend fun mockDownloadTrack(
        trackId: String,
        trackTitle: String,
        trackArtist: String
    ) {
        val totalBytes = MOCK_FILE_SIZE
        val downloadedBytes = 0L
        val startTime = System.currentTimeMillis()
        
        // Simulate download progress
        while (downloadedBytes < totalBytes && !isStopped) {
            val elapsedTime = System.currentTimeMillis() - startTime
            val progress = (elapsedTime.toFloat() / MOCK_DOWNLOAD_DURATION).coerceAtMost(1f)
            val currentDownloadedBytes = (totalBytes * progress).toLong()
            val speed = if (elapsedTime > 0) currentDownloadedBytes * 1000 / elapsedTime else 0L
            val timeRemaining = if (speed > 0) (totalBytes - currentDownloadedBytes) * 1000 / speed else 0L
            
            downloadManager.updateProgress(trackId, DownloadProgress(
                trackId = trackId,
                downloadedBytes = currentDownloadedBytes,
                totalBytes = totalBytes,
                progress = progress,
                speed = speed,
                timeRemaining = timeRemaining
            ))
            
            delay(100) // Update every 100ms
        }
        
        if (!isStopped) {
            // Create mock file
            val downloadDir = File(applicationContext.getExternalFilesDir(null), "downloads")
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            
            val fileName = "${trackArtist}_${trackTitle}".replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val file = File(downloadDir, "$fileName.mp3")
            file.createNewFile()
            
            // Complete download
            downloadManager.completeDownload(trackId, file.absolutePath)
            
            Log.d(TAG, "Mock download completed for track: $trackTitle")
        }
    }
}
