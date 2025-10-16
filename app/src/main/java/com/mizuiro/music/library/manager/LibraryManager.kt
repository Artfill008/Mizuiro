package com.mizuiro.music.library.manager

import com.mizuiro.music.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Library Manager
 * 
 * Central manager for all library operations including:
 * - Track organization and categorization
 * - Playlist management
 * - Artist and album organization
 * - Smart library features
 * - Library statistics and analytics
 */
@Singleton
class LibraryManager @Inject constructor() {
    
    // Library state
    private val _libraryState = MutableStateFlow(LibraryState())
    val libraryState: StateFlow<LibraryState> = _libraryState.asStateFlow()
    
    // Track collections
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()
    
    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()
    
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()
    
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()
    
    // Smart collections
    private val _smartPlaylists = MutableStateFlow<List<SmartPlaylist>>(emptyList())
    val smartPlaylists: StateFlow<List<SmartPlaylist>> = _smartPlaylists.asStateFlow()
    
    private val _recentlyPlayed = MutableStateFlow<List<Track>>(emptyList())
    val recentlyPlayed: StateFlow<List<Track>> = _recentlyPlayed.asStateFlow()
    
    private val _frequentlyPlayed = MutableStateFlow<List<Track>>(emptyList())
    val frequentlyPlayed: StateFlow<List<Track>> = _frequentlyPlayed.asStateFlow()
    
    private val _likedTracks = MutableStateFlow<List<Track>>(emptyList())
    val likedTracks: StateFlow<List<Track>> = _likedTracks.asStateFlow()
    
    // Library statistics
    private val _libraryStats = MutableStateFlow(LibraryStats())
    val libraryStats: StateFlow<LibraryStats> = _libraryStats.asStateFlow()
    
    /**
     * Add track to library
     */
    fun addTrack(track: Track) {
        val currentTracks = _tracks.value.toMutableList()
        if (!currentTracks.any { it.id == track.id }) {
            currentTracks.add(track)
            _tracks.value = currentTracks
            updateLibraryStats()
        }
    }
    
    /**
     * Remove track from library
     */
    fun removeTrack(trackId: String) {
        val currentTracks = _tracks.value.toMutableList()
        currentTracks.removeAll { it.id == trackId }
        _tracks.value = currentTracks
        updateLibraryStats()
    }
    
    /**
     * Create new playlist
     */
    fun createPlaylist(
        name: String,
        description: String? = null,
        isPublic: Boolean = false,
        tags: List<String> = emptyList()
    ): Playlist {
        val playlist = Playlist(
            id = "playlist_${System.currentTimeMillis()}",
            name = name,
            description = description,
            source = PlaylistSource.USER_CREATED,
            isPublic = isPublic,
            tags = tags,
            dateCreated = System.currentTimeMillis()
        )
        
        val currentPlaylists = _playlists.value.toMutableList()
        currentPlaylists.add(playlist)
        _playlists.value = currentPlaylists
        
        return playlist
    }
    
    /**
     * Add track to playlist
     */
    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        val currentPlaylists = _playlists.value.toMutableList()
        val playlistIndex = currentPlaylists.indexOfFirst { it.id == playlistId }
        
        if (playlistIndex != -1) {
            val playlist = currentPlaylists[playlistIndex]
            val updatedTrackIds = playlist.trackIds.toMutableList()
            if (!updatedTrackIds.contains(trackId)) {
                updatedTrackIds.add(trackId)
                currentPlaylists[playlistIndex] = playlist.copy(
                    trackIds = updatedTrackIds,
                    trackCount = updatedTrackIds.size,
                    dateModified = System.currentTimeMillis()
                )
                _playlists.value = currentPlaylists
            }
        }
    }
    
    /**
     * Remove track from playlist
     */
    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        val currentPlaylists = _playlists.value.toMutableList()
        val playlistIndex = currentPlaylists.indexOfFirst { it.id == playlistId }
        
        if (playlistIndex != -1) {
            val playlist = currentPlaylists[playlistIndex]
            val updatedTrackIds = playlist.trackIds.toMutableList()
            updatedTrackIds.remove(trackId)
            currentPlaylists[playlistIndex] = playlist.copy(
                trackIds = updatedTrackIds,
                trackCount = updatedTrackIds.size,
                dateModified = System.currentTimeMillis()
            )
            _playlists.value = currentPlaylists
        }
    }
    
    /**
     * Delete playlist
     */
    fun deletePlaylist(playlistId: String) {
        val currentPlaylists = _playlists.value.toMutableList()
        currentPlaylists.removeAll { it.id == playlistId }
        _playlists.value = currentPlaylists
    }
    
    /**
     * Like track
     */
    fun likeTrack(trackId: String) {
        val currentTracks = _tracks.value.toMutableList()
        val trackIndex = currentTracks.indexOfFirst { it.id == trackId }
        
        if (trackIndex != -1) {
            val track = currentTracks[trackIndex]
            currentTracks[trackIndex] = track.copy(isLiked = true)
            _tracks.value = currentTracks
            
            // Add to liked tracks
            val currentLiked = _likedTracks.value.toMutableList()
            if (!currentLiked.any { it.id == trackId }) {
                currentLiked.add(track.copy(isLiked = true))
                _likedTracks.value = currentLiked
            }
        }
    }
    
    /**
     * Unlike track
     */
    fun unlikeTrack(trackId: String) {
        val currentTracks = _tracks.value.toMutableList()
        val trackIndex = currentTracks.indexOfFirst { it.id == trackId }
        
        if (trackIndex != -1) {
            val track = currentTracks[trackIndex]
            currentTracks[trackIndex] = track.copy(isLiked = false)
            _tracks.value = currentTracks
            
            // Remove from liked tracks
            val currentLiked = _likedTracks.value.toMutableList()
            currentLiked.removeAll { it.id == trackId }
            _likedTracks.value = currentLiked
        }
    }
    
    /**
     * Update track play count
     */
    fun updateTrackPlayCount(trackId: String) {
        val currentTracks = _tracks.value.toMutableList()
        val trackIndex = currentTracks.indexOfFirst { it.id == trackId }
        
        if (trackIndex != -1) {
            val track = currentTracks[trackIndex]
            val updatedTrack = track.copy(
                playCount = track.playCount + 1,
                lastPlayed = System.currentTimeMillis()
            )
            currentTracks[trackIndex] = updatedTrack
            _tracks.value = currentTracks
            
            // Update recently played
            updateRecentlyPlayed(updatedTrack)
            updateFrequentlyPlayed()
        }
    }
    
    /**
     * Search library
     */
    fun searchLibrary(query: String): LibrarySearchResults {
        val tracks = _tracks.value.filter { track ->
            track.title.contains(query, ignoreCase = true) ||
            track.artist.contains(query, ignoreCase = true) ||
            track.album?.contains(query, ignoreCase = true) == true
        }
        
        val artists = _artists.value.filter { artist ->
            artist.name.contains(query, ignoreCase = true)
        }
        
        val albums = _albums.value.filter { album ->
            album.title.contains(query, ignoreCase = true) ||
            album.artist.contains(query, ignoreCase = true)
        }
        
        val playlists = _playlists.value.filter { playlist ->
            playlist.name.contains(query, ignoreCase = true) ||
            playlist.description?.contains(query, ignoreCase = true) == true
        }
        
        return LibrarySearchResults(
            tracks = tracks,
            artists = artists,
            albums = albums,
            playlists = playlists
        )
    }
    
    /**
     * Get tracks by mood
     */
    fun getTracksByMood(mood: TrackMood): List<Track> {
        return _tracks.value.filter { it.mood == mood }
    }
    
    /**
     * Get tracks by genre
     */
    fun getTracksByGenre(genre: String): List<Track> {
        return _tracks.value.filter { it.genre?.contains(genre, ignoreCase = true) == true }
    }
    
    /**
     * Get tracks by energy level
     */
    fun getTracksByEnergy(minEnergy: Float, maxEnergy: Float): List<Track> {
        return _tracks.value.filter { track ->
            track.energy in minEnergy..maxEnergy
        }
    }
    
    /**
     * Get tracks by valence
     */
    fun getTracksByValence(minValence: Float, maxValence: Float): List<Track> {
        return _tracks.value.filter { track ->
            track.valence in minValence..maxValence
        }
    }
    
    /**
     * Get similar tracks
     */
    fun getSimilarTracks(trackId: String, limit: Int = 10): List<Track> {
        val track = _tracks.value.find { it.id == trackId } ?: return emptyList()
        
        return _tracks.value
            .filter { it.id != trackId }
            .sortedByDescending { otherTrack ->
                calculateSimilarity(track, otherTrack)
            }
            .take(limit)
    }
    
    /**
     * Create smart playlist
     */
    fun createSmartPlaylist(
        name: String,
        description: String? = null,
        rules: List<SmartRule>,
        isPublic: Boolean = false
    ): SmartPlaylist {
        val smartPlaylist = SmartPlaylist(
            id = "smart_playlist_${System.currentTimeMillis()}",
            name = name,
            description = description,
            rules = rules,
            isPublic = isPublic,
            dateCreated = System.currentTimeMillis()
        )
        
        val currentSmartPlaylists = _smartPlaylists.value.toMutableList()
        currentSmartPlaylists.add(smartPlaylist)
        _smartPlaylists.value = currentSmartPlaylists
        
        return smartPlaylist
    }
    
    /**
     * Update recently played tracks
     */
    private fun updateRecentlyPlayed(track: Track) {
        val currentRecent = _recentlyPlayed.value.toMutableList()
        currentRecent.removeAll { it.id == track.id }
        currentRecent.add(0, track)
        
        // Keep only last 50 tracks
        if (currentRecent.size > 50) {
            currentRecent.removeAt(currentRecent.size - 1)
        }
        
        _recentlyPlayed.value = currentRecent
    }
    
    /**
     * Update frequently played tracks
     */
    private fun updateFrequentlyPlayed() {
        val frequentlyPlayed = _tracks.value
            .filter { it.playCount > 0 }
            .sortedByDescending { it.playCount }
            .take(50)
        
        _frequentlyPlayed.value = frequentlyPlayed
    }
    
    /**
     * Update library statistics
     */
    private fun updateLibraryStats() {
        val tracks = _tracks.value
        val artists = _artists.value
        val albums = _albums.value
        val playlists = _playlists.value
        
        val totalDuration = tracks.sumOf { it.duration }
        val totalSize = tracks.sumOf { it.fileSize ?: 0L }
        val likedCount = tracks.count { it.isLiked }
        val recentlyPlayedCount = _recentlyPlayed.value.size
        
        val stats = LibraryStats(
            totalTracks = tracks.size,
            totalArtists = artists.size,
            totalAlbums = albums.size,
            totalPlaylists = playlists.size,
            totalDuration = totalDuration,
            totalSize = totalSize,
            likedTracks = likedCount,
            recentlyPlayed = recentlyPlayedCount,
            lastUpdated = System.currentTimeMillis()
        )
        
        _libraryStats.value = stats
    }
    
    /**
     * Calculate similarity between tracks
     */
    private fun calculateSimilarity(track1: Track, track2: Track): Float {
        var similarity = 0.0f
        
        // Genre similarity
        if (track1.genre == track2.genre) {
            similarity += 0.3f
        }
        
        // Mood similarity
        if (track1.mood == track2.mood) {
            similarity += 0.2f
        }
        
        // Energy similarity
        val energyDiff = kotlin.math.abs(track1.energy - track2.energy)
        similarity += (1.0f - energyDiff) * 0.2f
        
        // Valence similarity
        val valenceDiff = kotlin.math.abs(track1.valence - track2.valence)
        similarity += (1.0f - valenceDiff) * 0.2f
        
        // Tempo similarity
        val tempoDiff = kotlin.math.abs(track1.tempo - track2.tempo)
        similarity += (1.0f - (tempoDiff / 200.0f).coerceAtMost(1.0f)) * 0.1f
        
        return similarity
    }
}

/**
 * Library state
 */
data class LibraryState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val lastSync: Long = 0L,
    val error: String? = null
)

/**
 * Library statistics
 */
data class LibraryStats(
    val totalTracks: Int = 0,
    val totalArtists: Int = 0,
    val totalAlbums: Int = 0,
    val totalPlaylists: Int = 0,
    val totalDuration: Long = 0L,
    val totalSize: Long = 0L,
    val likedTracks: Int = 0,
    val recentlyPlayed: Int = 0,
    val lastUpdated: Long = 0L
)

/**
 * Library search results
 */
data class LibrarySearchResults(
    val tracks: List<Track>,
    val artists: List<Artist>,
    val albums: List<Album>,
    val playlists: List<Playlist>
)

/**
 * Smart playlist
 */
data class SmartPlaylist(
    val id: String,
    val name: String,
    val description: String? = null,
    val rules: List<SmartRule>,
    val isPublic: Boolean = false,
    val isEnabled: Boolean = true,
    val dateCreated: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis(),
    val trackCount: Int = 0,
    val duration: Long = 0L
)
