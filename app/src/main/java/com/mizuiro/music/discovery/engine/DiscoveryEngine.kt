package com.mizuiro.music.discovery.engine

import com.mizuiro.music.data.model.*
import com.mizuiro.music.library.manager.LibraryManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Discovery Engine
 * 
 * Advanced music discovery and recommendation system featuring:
 * - Mood-based discovery
 * - Collaborative filtering
 * - Content-based recommendations
 * - Trend analysis
 * - Personalized playlists
 * - Music exploration features
 */
@Singleton
class DiscoveryEngine @Inject constructor(
    private val libraryManager: LibraryManager
) {
    
    // Discovery state
    private val _discoveryState = MutableStateFlow(DiscoveryState())
    val discoveryState: StateFlow<DiscoveryState> = _discoveryState.asStateFlow()
    
    // Recommendation collections
    private val _moodRecommendations = MutableStateFlow<Map<TrackMood, List<Track>>>(emptyMap())
    val moodRecommendations: StateFlow<Map<TrackMood, List<Track>>> = _moodRecommendations.asStateFlow()
    
    private val _trendingTracks = MutableStateFlow<List<Track>>(emptyList())
    val trendingTracks: StateFlow<List<Track>> = _trendingTracks.asStateFlow()
    
    private val _newReleases = MutableStateFlow<List<Track>>(emptyList())
    val newReleases: StateFlow<List<Track>> = _newReleases.asStateFlow()
    
    private val _personalizedPlaylists = MutableStateFlow<List<DiscoveryPlaylist>>(emptyList())
    val personalizedPlaylists: StateFlow<List<DiscoveryPlaylist>> = _personalizedPlaylists.asStateFlow()
    
    private val _similarArtists = MutableStateFlow<Map<String, List<Artist>>>(emptyMap())
    val similarArtists: StateFlow<Map<String, List<Artist>>> = _similarArtists.asStateFlow()
    
    private val _genreExploration = MutableStateFlow<Map<String, List<Track>>>(emptyMap())
    val genreExploration: StateFlow<Map<String, List<Track>>> = _genreExploration.asStateFlow()
    
    // User preferences
    private val _userPreferences = MutableStateFlow(UserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()
    
    // Discovery algorithms
    private val collaborativeFilter = CollaborativeFilteringAlgorithm()
    private val contentBasedFilter = ContentBasedFilteringAlgorithm()
    private val moodAnalyzer = MoodAnalysisAlgorithm()
    private val trendAnalyzer = TrendAnalysisAlgorithm()
    
    /**
     * Get mood-based recommendations
     */
    fun getMoodRecommendations(mood: TrackMood, limit: Int = 20): List<Track> {
        val allTracks = libraryManager.tracks.value
        val userTracks = libraryManager.likedTracks.value
        
        return contentBasedFilter.getMoodRecommendations(
            targetMood = mood,
            allTracks = allTracks,
            userTracks = userTracks,
            limit = limit
        )
    }
    
    /**
     * Get personalized recommendations
     */
    fun getPersonalizedRecommendations(limit: Int = 20): List<Track> {
        val allTracks = libraryManager.tracks.value
        val userTracks = libraryManager.likedTracks.value
        val recentlyPlayed = libraryManager.recentlyPlayed.value
        
        return collaborativeFilter.getPersonalizedRecommendations(
            allTracks = allTracks,
            userTracks = userTracks,
            recentlyPlayed = recentlyPlayed,
            limit = limit
        )
    }
    
    /**
     * Get similar tracks
     */
    fun getSimilarTracks(trackId: String, limit: Int = 10): List<Track> {
        val track = libraryManager.tracks.value.find { it.id == trackId }
        if (track == null) return emptyList()
        
        val allTracks = libraryManager.tracks.value
        return contentBasedFilter.getSimilarTracks(
            targetTrack = track,
            allTracks = allTracks,
            limit = limit
        )
    }
    
    /**
     * Get trending tracks
     */
    fun getTrendingTracks(limit: Int = 20): List<Track> {
        val allTracks = libraryManager.tracks.value
        val recentlyPlayed = libraryManager.recentlyPlayed.value
        
        return trendAnalyzer.getTrendingTracks(
            allTracks = allTracks,
            recentlyPlayed = recentlyPlayed,
            limit = limit
        )
    }
    
    /**
     * Get new releases
     */
    fun getNewReleases(limit: Int = 20): List<Track> {
        val allTracks = libraryManager.tracks.value
        val currentTime = System.currentTimeMillis()
        val oneWeekAgo = currentTime - (7 * 24 * 60 * 60 * 1000L)
        
        return allTracks
            .filter { it.dateAdded > oneWeekAgo }
            .sortedByDescending { it.dateAdded }
            .take(limit)
    }
    
    /**
     * Get genre exploration
     */
    fun getGenreExploration(genre: String, limit: Int = 20): List<Track> {
        val allTracks = libraryManager.tracks.value
        val userTracks = libraryManager.likedTracks.value
        
        return contentBasedFilter.getGenreRecommendations(
            genre = genre,
            allTracks = allTracks,
            userTracks = userTracks,
            limit = limit
        )
    }
    
    /**
     * Get similar artists
     */
    fun getSimilarArtists(artistId: String, limit: Int = 10): List<Artist> {
        val artist = libraryManager.artists.value.find { it.id == artistId }
        if (artist == null) return emptyList()
        
        val allArtists = libraryManager.artists.value
        val allTracks = libraryManager.tracks.value
        
        return contentBasedFilter.getSimilarArtists(
            targetArtist = artist,
            allArtists = allArtists,
            allTracks = allTracks,
            limit = limit
        )
    }
    
    /**
     * Create personalized playlist
     */
    fun createPersonalizedPlaylist(
        name: String,
        description: String? = null,
        mood: TrackMood? = null,
        genre: String? = null,
        energyRange: Pair<Float, Float>? = null,
        valenceRange: Pair<Float, Float>? = null,
        limit: Int = 50
    ): DiscoveryPlaylist {
        val allTracks = libraryManager.tracks.value
        val userTracks = libraryManager.likedTracks.value
        
        val tracks = contentBasedFilter.getPersonalizedTracks(
            allTracks = allTracks,
            userTracks = userTracks,
            mood = mood,
            genre = genre,
            energyRange = energyRange,
            valenceRange = valenceRange,
            limit = limit
        )
        
        val playlist = DiscoveryPlaylist(
            id = "discovery_playlist_${System.currentTimeMillis()}",
            name = name,
            description = description,
            trackIds = tracks.map { it.id },
            trackCount = tracks.size,
            duration = tracks.sumOf { it.duration },
            mood = mood,
            genre = genre,
            energyRange = energyRange,
            valenceRange = valenceRange,
            dateCreated = System.currentTimeMillis(),
            isPersonalized = true
        )
        
        val currentPlaylists = _personalizedPlaylists.value.toMutableList()
        currentPlaylists.add(playlist)
        _personalizedPlaylists.value = currentPlaylists
        
        return playlist
    }
    
    /**
     * Analyze user mood preferences
     */
    fun analyzeUserMoodPreferences(): Map<TrackMood, Float> {
        val userTracks = libraryManager.likedTracks.value
        val recentlyPlayed = libraryManager.recentlyPlayed.value
        
        return moodAnalyzer.analyzeUserPreferences(
            likedTracks = userTracks,
            recentlyPlayed = recentlyPlayed
        )
    }
    
    /**
     * Get discovery insights
     */
    fun getDiscoveryInsights(): DiscoveryInsights {
        val userTracks = libraryManager.likedTracks.value
        val recentlyPlayed = libraryManager.recentlyPlayed.value
        val allTracks = libraryManager.tracks.value
        
        return DiscoveryInsights(
            totalTracksDiscovered = userTracks.size,
            favoriteMood = moodAnalyzer.getFavoriteMood(userTracks),
            favoriteGenre = contentBasedFilter.getFavoriteGenre(userTracks),
            averageEnergy = userTracks.map { it.energy }.average().toFloat(),
            averageValence = userTracks.map { it.valence }.average().toFloat(),
            discoveryScore = calculateDiscoveryScore(userTracks, allTracks),
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Update user preferences
     */
    fun updateUserPreferences(preferences: UserPreferences) {
        _userPreferences.value = preferences
        refreshRecommendations()
    }
    
    /**
     * Refresh all recommendations
     */
    fun refreshRecommendations() {
        val allTracks = libraryManager.tracks.value
        val userTracks = libraryManager.likedTracks.value
        val recentlyPlayed = libraryManager.recentlyPlayed.value
        
        // Update mood recommendations
        val moodRecs = TrackMood.values().associateWith { mood ->
            getMoodRecommendations(mood, 20)
        }
        _moodRecommendations.value = moodRecs
        
        // Update trending tracks
        _trendingTracks.value = getTrendingTracks(20)
        
        // Update new releases
        _newReleases.value = getNewReleases(20)
        
        // Update genre exploration
        val genres = allTracks.mapNotNull { it.genre }.distinct()
        val genreRecs = genres.associateWith { genre ->
            getGenreExploration(genre, 15)
        }
        _genreExploration.value = genreRecs
        
        // Update similar artists
        val artistRecs = libraryManager.artists.value.associateWith { artist ->
            getSimilarArtists(artist.id, 5)
        }
        _similarArtists.value = artistRecs
    }
    
    /**
     * Calculate discovery score
     */
    private fun calculateDiscoveryScore(userTracks: List<Track>, allTracks: List<Track>): Float {
        if (allTracks.isEmpty()) return 0f
        
        val diversityScore = calculateDiversityScore(userTracks)
        val explorationScore = calculateExplorationScore(userTracks, allTracks)
        val engagementScore = calculateEngagementScore(userTracks)
        
        return (diversityScore + explorationScore + engagementScore) / 3f
    }
    
    /**
     * Calculate diversity score
     */
    private fun calculateDiversityScore(tracks: List<Track>): Float {
        if (tracks.isEmpty()) return 0f
        
        val genres = tracks.mapNotNull { it.genre }.distinct().size
        val moods = tracks.map { it.mood }.distinct().size
        val artists = tracks.map { it.artist }.distinct().size
        
        val maxGenres = 10f
        val maxMoods = TrackMood.values().size.toFloat()
        val maxArtists = 50f
        
        val genreScore = (genres / maxGenres).coerceAtMost(1f)
        val moodScore = (moods / maxMoods).coerceAtMost(1f)
        val artistScore = (artists / maxArtists).coerceAtMost(1f)
        
        return (genreScore + moodScore + artistScore) / 3f
    }
    
    /**
     * Calculate exploration score
     */
    private fun calculateExplorationScore(userTracks: List<Track>, allTracks: List<Track>): Float {
        if (allTracks.isEmpty()) return 0f
        
        val userGenres = userTracks.mapNotNull { it.genre }.distinct()
        val allGenres = allTracks.mapNotNull { it.genre }.distinct()
        
        val exploredGenres = userGenres.size
        val totalGenres = allGenres.size
        
        return if (totalGenres > 0) {
            (exploredGenres / totalGenres.toFloat()).coerceAtMost(1f)
        } else 0f
    }
    
    /**
     * Calculate engagement score
     */
    private fun calculateEngagementScore(tracks: List<Track>): Float {
        if (tracks.isEmpty()) return 0f
        
        val totalPlayCount = tracks.sumOf { it.playCount }
        val averagePlayCount = totalPlayCount / tracks.size.toFloat()
        
        return (averagePlayCount / 10f).coerceAtMost(1f) // Normalize to 0-1
    }
}

/**
 * Discovery state
 */
data class DiscoveryState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val lastRefresh: Long = 0L,
    val error: String? = null
)

/**
 * Discovery playlist
 */
data class DiscoveryPlaylist(
    val id: String,
    val name: String,
    val description: String? = null,
    val trackIds: List<String>,
    val trackCount: Int,
    val duration: Long,
    val mood: TrackMood? = null,
    val genre: String? = null,
    val energyRange: Pair<Float, Float>? = null,
    val valenceRange: Pair<Float, Float>? = null,
    val dateCreated: Long,
    val isPersonalized: Boolean = false
)

/**
 * User preferences
 */
data class UserPreferences(
    val favoriteMoods: List<TrackMood> = emptyList(),
    val favoriteGenres: List<String> = emptyList(),
    val energyPreference: Pair<Float, Float> = 0.0f to 1.0f,
    val valencePreference: Pair<Float, Float> = 0.0f to 1.0f,
    val discoveryMode: DiscoveryMode = DiscoveryMode.BALANCED,
    val includeExplicit: Boolean = true,
    val languagePreference: String = "en"
)

/**
 * Discovery mode
 */
enum class DiscoveryMode {
    CONSERVATIVE,    // Similar to what user already likes
    BALANCED,        // Mix of similar and new content
    EXPLORATORY      // Focus on discovering new content
}

/**
 * Discovery insights
 */
data class DiscoveryInsights(
    val totalTracksDiscovered: Int,
    val favoriteMood: TrackMood?,
    val favoriteGenre: String?,
    val averageEnergy: Float,
    val averageValence: Float,
    val discoveryScore: Float,
    val lastUpdated: Long
)
