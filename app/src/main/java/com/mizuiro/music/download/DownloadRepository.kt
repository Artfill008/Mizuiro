package com.mizuiro.music.download

import com.mizuiro.music.data.model.Track
import com.mizuiro.music.data.model.Album
import com.mizuiro.music.data.model.Playlist
import com.mizuiro.music.library.manager.LibraryManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Download Repository
 * 
 * Manages download data and integrates with the library system
 */
@Singleton
class DownloadRepository @Inject constructor(
    private val libraryManager: LibraryManager
) {
    
    /**
     * Get downloaded tracks
     */
    suspend fun getDownloadedTracks(): List<Track> {
        return libraryManager.tracks.value.filter { track ->
            track.filePath.startsWith("/") && // Local file path
            !track.filePath.contains("http") // Not a URL
        }
    }
    
    /**
     * Get recently played tracks
     */
    suspend fun getRecentlyPlayedTracks(): List<Track> {
        return libraryManager.recentlyPlayed.value
    }
    
    /**
     * Get liked tracks
     */
    suspend fun getLikedTracks(): List<Track> {
        return libraryManager.likedTracks.value
    }
    
    /**
     * Get user preferences
     */
    suspend fun getUserPreferences(): UserPreferences {
        // This would typically come from a preferences store
        return UserPreferences(
            favoriteGenres = listOf("Pop", "Electronic", "Rock"),
            favoriteArtists = listOf(),
            downloadQuality = DownloadQuality.HIGH,
            autoDownload = true,
            maxDownloadSize = 1024 * 1024 * 1024, // 1GB
            wifiOnly = false
        )
    }
    
    /**
     * Get similar tracks based on genres
     */
    suspend fun getSimilarTracks(genres: List<String>): List<Track> {
        return libraryManager.tracks.value.filter { track ->
            track.genre in genres
        }
    }
    
    /**
     * Get album tracks
     */
    suspend fun getAlbumTracks(albumId: String): List<Track> {
        val album = libraryManager.albums.value.find { it.id == albumId }
        return album?.trackIds?.mapNotNull { trackId ->
            libraryManager.tracks.value.find { it.id == trackId }
        } ?: emptyList()
    }
    
    /**
     * Get playlist tracks
     */
    suspend fun getPlaylistTracks(playlistId: String): List<Track> {
        val playlist = libraryManager.playlists.value.find { it.id == playlistId }
        return playlist?.trackIds?.mapNotNull { trackId ->
            libraryManager.tracks.value.find { it.id == trackId }
        } ?: emptyList()
    }
    
    /**
     * Add downloaded track
     */
    suspend fun addDownloadedTrack(track: Track) {
        // This would typically update a local database
        // For now, we'll just update the library manager
        val currentTracks = libraryManager.tracks.value.toMutableList()
        val existingIndex = currentTracks.indexOfFirst { it.id == track.id }
        
        if (existingIndex != -1) {
            currentTracks[existingIndex] = track
        } else {
            currentTracks.add(track)
        }
        
        // Update the library manager (this would be done through a proper data source)
        // libraryManager.updateTracks(currentTracks)
    }
    
    /**
     * Remove downloaded track
     */
    suspend fun removeDownloadedTrack(trackId: String) {
        // This would typically update a local database
        // For now, we'll just update the library manager
        val currentTracks = libraryManager.tracks.value.toMutableList()
        currentTracks.removeAll { it.id == trackId }
        
        // Update the library manager (this would be done through a proper data source)
        // libraryManager.updateTracks(currentTracks)
    }
    
    /**
     * Clear all downloads
     */
    suspend fun clearAllDownloads() {
        // This would typically clear a local database
        // For now, we'll just update the library manager
        val currentTracks = libraryManager.tracks.value.toMutableList()
        currentTracks.removeAll { track ->
            track.filePath.startsWith("/") && // Local file path
            !track.filePath.contains("http") // Not a URL
        }
        
        // Update the library manager (this would be done through a proper data source)
        // libraryManager.updateTracks(currentTracks)
    }
    
    /**
     * Get download queue
     */
    suspend fun getDownloadQueue(): List<DownloadItem> {
        // This would typically come from a local database
        // For now, return empty list
        return emptyList()
    }
    
    /**
     * Save download queue
     */
    suspend fun saveDownloadQueue(queue: List<DownloadItem>) {
        // This would typically save to a local database
        // For now, do nothing
    }
    
    /**
     * Get download statistics
     */
    suspend fun getDownloadStatistics(): DownloadStatistics {
        val downloadedTracks = getDownloadedTracks()
        val totalSize = downloadedTracks.sumOf { estimateTrackSize(it) }
        val totalTracks = downloadedTracks.size
        
        return DownloadStatistics(
            totalDownloadedTracks = totalTracks,
            totalDownloadedSize = totalSize,
            averageTrackSize = if (totalTracks > 0) totalSize / totalTracks else 0L,
            mostDownloadedGenre = getMostDownloadedGenre(downloadedTracks),
            mostDownloadedArtist = getMostDownloadedArtist(downloadedTracks),
            downloadHistory = getDownloadHistory(downloadedTracks)
        )
    }
    
    /**
     * Get download history
     */
    suspend fun getDownloadHistory(tracks: List<Track>): List<DownloadHistoryItem> {
        return tracks.map { track ->
            DownloadHistoryItem(
                trackId = track.id,
                trackTitle = track.title,
                artist = track.artist,
                album = track.album,
                downloadDate = track.dateAdded,
                fileSize = estimateTrackSize(track),
                filePath = track.filePath
            )
        }.sortedByDescending { it.downloadDate }
    }
    
    /**
     * Get most downloaded genre
     */
    private fun getMostDownloadedGenre(tracks: List<Track>): String? {
        return tracks.groupBy { it.genre }
            .maxByOrNull { it.value.size }
            ?.key
    }
    
    /**
     * Get most downloaded artist
     */
    private fun getMostDownloadedArtist(tracks: List<Track>): String? {
        return tracks.groupBy { it.artist }
            .maxByOrNull { it.value.size }
            ?.key
    }
    
    /**
     * Estimate track size
     */
    private fun estimateTrackSize(track: Track): Long {
        // Estimate based on duration (roughly 1MB per minute for high quality)
        return (track.duration / 1000 / 60) * 1024 * 1024
    }
}

/**
 * User preferences data class
 */
data class UserPreferences(
    val favoriteGenres: List<String>,
    val favoriteArtists: List<String>,
    val downloadQuality: DownloadQuality,
    val autoDownload: Boolean,
    val maxDownloadSize: Long,
    val wifiOnly: Boolean
)

/**
 * Download quality enum
 */
enum class DownloadQuality {
    LOW, MEDIUM, HIGH, LOSSLESS
}

/**
 * Download statistics data class
 */
data class DownloadStatistics(
    val totalDownloadedTracks: Int,
    val totalDownloadedSize: Long,
    val averageTrackSize: Long,
    val mostDownloadedGenre: String?,
    val mostDownloadedArtist: String?,
    val downloadHistory: List<DownloadHistoryItem>
)

/**
 * Download history item data class
 */
data class DownloadHistoryItem(
    val trackId: String,
    val trackTitle: String,
    val artist: String,
    val album: String,
    val downloadDate: Long,
    val fileSize: Long,
    val filePath: String
)
