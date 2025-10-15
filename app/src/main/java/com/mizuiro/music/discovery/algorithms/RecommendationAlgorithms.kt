package com.mizuiro.music.discovery.algorithms

import com.mizuiro.music.data.model.*
import kotlin.math.*

/**
 * Collaborative Filtering Algorithm
 * 
 * Implements user-based collaborative filtering for music recommendations
 */
class CollaborativeFilteringAlgorithm {
    
    /**
     * Get personalized recommendations based on user behavior
     */
    fun getPersonalizedRecommendations(
        allTracks: List<Track>,
        userTracks: List<Track>,
        recentlyPlayed: List<Track>,
        limit: Int = 20
    ): List<Track> {
        if (userTracks.isEmpty()) {
            // If no user tracks, return popular tracks
            return getPopularTracks(allTracks, limit)
        }
        
        // Calculate user profile
        val userProfile = calculateUserProfile(userTracks, recentlyPlayed)
        
        // Find similar users (tracks with similar characteristics)
        val similarTracks = findSimilarTracks(allTracks, userProfile, userTracks)
        
        // Score and rank tracks
        val scoredTracks = similarTracks.map { track ->
            val score = calculateCollaborativeScore(track, userProfile, userTracks)
            track to score
        }.sortedByDescending { it.second }
        
        return scoredTracks.take(limit).map { it.first }
    }
    
    /**
     * Calculate user profile based on liked and recently played tracks
     */
    private fun calculateUserProfile(
        likedTracks: List<Track>,
        recentlyPlayed: List<Track>
    ): UserProfile {
        val allTracks = likedTracks + recentlyPlayed
        
        if (allTracks.isEmpty()) {
            return UserProfile()
        }
        
        // Calculate average characteristics
        val avgEnergy = allTracks.map { it.energy }.average()
        val avgValence = allTracks.map { it.valence }.average()
        val avgTempo = allTracks.map { it.tempo }.average()
        val avgDanceability = allTracks.map { it.danceability }.average()
        val avgAcousticness = allTracks.map { it.acousticness }.average()
        val avgInstrumentalness = allTracks.map { it.instrumentalness }.average()
        
        // Calculate genre preferences
        val genreCounts = allTracks.mapNotNull { it.genre }
            .groupingBy { it }
            .eachCount()
        val totalTracks = allTracks.size
        val genrePreferences = genreCounts.mapValues { (_, count) ->
            count.toFloat() / totalTracks
        }
        
        // Calculate mood preferences
        val moodCounts = allTracks.map { it.mood }
            .groupingBy { it }
            .eachCount()
        val moodPreferences = moodCounts.mapValues { (_, count) ->
            count.toFloat() / totalTracks
        }
        
        return UserProfile(
            avgEnergy = avgEnergy.toFloat(),
            avgValence = avgValence.toFloat(),
            avgTempo = avgTempo.toFloat(),
            avgDanceability = avgDanceability.toFloat(),
            avgAcousticness = avgAcousticness.toFloat(),
            avgInstrumentalness = avgInstrumentalness.toFloat(),
            genrePreferences = genrePreferences,
            moodPreferences = moodPreferences
        )
    }
    
    /**
     * Find tracks similar to user profile
     */
    private fun findSimilarTracks(
        allTracks: List<Track>,
        userProfile: UserProfile,
        userTracks: List<Track>
    ): List<Track> {
        val userTrackIds = userTracks.map { it.id }.toSet()
        
        return allTracks.filter { track ->
            !userTrackIds.contains(track.id) && isTrackRelevant(track, userProfile)
        }
    }
    
    /**
     * Check if track is relevant to user profile
     */
    private fun isTrackRelevant(track: Track, userProfile: UserProfile): Boolean {
        // Check genre preference
        val genreScore = track.genre?.let { genre ->
            userProfile.genrePreferences[genre] ?: 0f
        } ?: 0f
        
        // Check mood preference
        val moodScore = userProfile.moodPreferences[track.mood] ?: 0f
        
        // Check energy similarity
        val energyDiff = abs(track.energy - userProfile.avgEnergy)
        val energyScore = 1f - (energyDiff / 2f).coerceAtMost(1f)
        
        // Check valence similarity
        val valenceDiff = abs(track.valence - userProfile.avgValence)
        val valenceScore = 1f - (valenceDiff / 2f).coerceAtMost(1f)
        
        // Combined relevance score
        val relevanceScore = (genreScore * 0.3f) + (moodScore * 0.3f) + 
                           (energyScore * 0.2f) + (valenceScore * 0.2f)
        
        return relevanceScore > 0.3f
    }
    
    /**
     * Calculate collaborative filtering score
     */
    private fun calculateCollaborativeScore(
        track: Track,
        userProfile: UserProfile,
        userTracks: List<Track>
    ): Float {
        var score = 0f
        
        // Genre preference score
        val genreScore = track.genre?.let { genre ->
            userProfile.genrePreferences[genre] ?: 0f
        } ?: 0f
        score += genreScore * 0.3f
        
        // Mood preference score
        val moodScore = userProfile.moodPreferences[track.mood] ?: 0f
        score += moodScore * 0.3f
        
        // Audio feature similarity
        val energySimilarity = 1f - abs(track.energy - userProfile.avgEnergy) / 2f
        val valenceSimilarity = 1f - abs(track.valence - userProfile.avgValence) / 2f
        val tempoSimilarity = 1f - abs(track.tempo - userProfile.avgTempo) / 200f
        
        score += energySimilarity * 0.15f
        score += valenceSimilarity * 0.15f
        score += tempoSimilarity * 0.1f
        
        // Popularity boost
        val popularityScore = calculatePopularityScore(track, userTracks)
        score += popularityScore * 0.1f
        
        return score.coerceIn(0f, 1f)
    }
    
    /**
     * Calculate popularity score
     */
    private fun calculatePopularityScore(track: Track, userTracks: List<Track>): Float {
        val userGenres = userTracks.mapNotNull { it.genre }.distinct()
        val trackGenre = track.genre
        
        return if (trackGenre in userGenres) {
            // Boost if genre matches user preferences
            0.8f
        } else {
            // Lower score for unfamiliar genres
            0.3f
        }
    }
    
    /**
     * Get popular tracks when no user data is available
     */
    private fun getPopularTracks(allTracks: List<Track>, limit: Int): List<Track> {
        return allTracks
            .sortedByDescending { it.playCount }
            .take(limit)
    }
}

/**
 * Content-Based Filtering Algorithm
 * 
 * Implements content-based filtering using track audio features
 */
class ContentBasedFilteringAlgorithm {
    
    /**
     * Get mood-based recommendations
     */
    fun getMoodRecommendations(
        targetMood: TrackMood,
        allTracks: List<Track>,
        userTracks: List<Track>,
        limit: Int = 20
    ): List<Track> {
        val userTrackIds = userTracks.map { it.id }.toSet()
        
        val moodTracks = allTracks.filter { track ->
            !userTrackIds.contains(track.id) && track.mood == targetMood
        }
        
        // If user has tracks, find similar ones
        if (userTracks.isNotEmpty()) {
            val userMoodTracks = userTracks.filter { it.mood == targetMood }
            if (userMoodTracks.isNotEmpty()) {
                val avgProfile = calculateAverageProfile(userMoodTracks)
                return findSimilarTracks(moodTracks, avgProfile, limit)
            }
        }
        
        // Otherwise, return popular tracks in that mood
        return moodTracks
            .sortedByDescending { it.playCount }
            .take(limit)
    }
    
    /**
     * Get similar tracks based on audio features
     */
    fun getSimilarTracks(
        targetTrack: Track,
        allTracks: List<Track>,
        limit: Int = 10
    ): List<Track> {
        val scoredTracks = allTracks
            .filter { it.id != targetTrack.id }
            .map { track ->
                val similarity = calculateTrackSimilarity(targetTrack, track)
                track to similarity
            }
            .sortedByDescending { it.second }
        
        return scoredTracks.take(limit).map { it.first }
    }
    
    /**
     * Get genre recommendations
     */
    fun getGenreRecommendations(
        genre: String,
        allTracks: List<Track>,
        userTracks: List<Track>,
        limit: Int = 20
    ): List<Track> {
        val userTrackIds = userTracks.map { it.id }.toSet()
        
        val genreTracks = allTracks.filter { track ->
            !userTrackIds.contains(track.id) && track.genre == genre
        }
        
        // If user has tracks in this genre, find similar ones
        if (userTracks.isNotEmpty()) {
            val userGenreTracks = userTracks.filter { it.genre == genre }
            if (userGenreTracks.isNotEmpty()) {
                val avgProfile = calculateAverageProfile(userGenreTracks)
                return findSimilarTracks(genreTracks, avgProfile, limit)
            }
        }
        
        // Otherwise, return popular tracks in this genre
        return genreTracks
            .sortedByDescending { it.playCount }
            .take(limit)
    }
    
    /**
     * Get similar artists
     */
    fun getSimilarArtists(
        targetArtist: Artist,
        allArtists: List<Artist>,
        allTracks: List<Track>,
        limit: Int = 10
    ): List<Artist> {
        val targetTracks = allTracks.filter { it.artist == targetArtist.name }
        if (targetTracks.isEmpty()) return emptyList()
        
        val avgProfile = calculateAverageProfile(targetTracks)
        
        val scoredArtists = allArtists
            .filter { it.id != targetArtist.id }
            .map { artist ->
                val artistTracks = allTracks.filter { it.artist == artist.name }
                if (artistTracks.isEmpty()) {
                    artist to 0f
                } else {
                    val artistProfile = calculateAverageProfile(artistTracks)
                    val similarity = calculateProfileSimilarity(avgProfile, artistProfile)
                    artist to similarity
                }
            }
            .sortedByDescending { it.second }
        
        return scoredArtists.take(limit).map { it.first }
    }
    
    /**
     * Get personalized tracks based on criteria
     */
    fun getPersonalizedTracks(
        allTracks: List<Track>,
        userTracks: List<Track>,
        mood: TrackMood? = null,
        genre: String? = null,
        energyRange: Pair<Float, Float>? = null,
        valenceRange: Pair<Float, Float>? = null,
        limit: Int = 50
    ): List<Track> {
        val userTrackIds = userTracks.map { it.id }.toSet()
        
        var filteredTracks = allTracks.filter { track ->
            !userTrackIds.contains(track.id)
        }
        
        // Apply filters
        mood?.let { filteredTracks = filteredTracks.filter { it.mood == it } }
        genre?.let { filteredTracks = filteredTracks.filter { it.genre == it } }
        energyRange?.let { (min, max) ->
            filteredTracks = filteredTracks.filter { it.energy in min..max }
        }
        valenceRange?.let { (min, max) ->
            filteredTracks = filteredTracks.filter { it.valence in min..max }
        }
        
        // If user has tracks, find similar ones
        if (userTracks.isNotEmpty()) {
            val avgProfile = calculateAverageProfile(userTracks)
            return findSimilarTracks(filteredTracks, avgProfile, limit)
        }
        
        // Otherwise, return popular tracks
        return filteredTracks
            .sortedByDescending { it.playCount }
            .take(limit)
    }
    
    /**
     * Get favorite genre from user tracks
     */
    fun getFavoriteGenre(userTracks: List<Track>): String? {
        if (userTracks.isEmpty()) return null
        
        val genreCounts = userTracks.mapNotNull { it.genre }
            .groupingBy { it }
            .eachCount()
        
        return genreCounts.maxByOrNull { it.value }?.key
    }
    
    /**
     * Calculate average profile from tracks
     */
    private fun calculateAverageProfile(tracks: List<Track>): TrackProfile {
        if (tracks.isEmpty()) return TrackProfile()
        
        return TrackProfile(
            energy = tracks.map { it.energy }.average().toFloat(),
            valence = tracks.map { it.valence }.average().toFloat(),
            tempo = tracks.map { it.tempo }.average().toFloat(),
            danceability = tracks.map { it.danceability }.average().toFloat(),
            acousticness = tracks.map { it.acousticness }.average().toFloat(),
            instrumentalness = tracks.map { it.instrumentalness }.average().toFloat()
        )
    }
    
    /**
     * Find similar tracks based on profile
     */
    private fun findSimilarTracks(
        tracks: List<Track>,
        profile: TrackProfile,
        limit: Int
    ): List<Track> {
        val scoredTracks = tracks.map { track ->
            val similarity = calculateProfileSimilarity(profile, track.toProfile())
            track to similarity
        }.sortedByDescending { it.second }
        
        return scoredTracks.take(limit).map { it.first }
    }
    
    /**
     * Calculate track similarity
     */
    private fun calculateTrackSimilarity(track1: Track, track2: Track): Float {
        val profile1 = track1.toProfile()
        val profile2 = track2.toProfile()
        return calculateProfileSimilarity(profile1, profile2)
    }
    
    /**
     * Calculate profile similarity
     */
    private fun calculateProfileSimilarity(profile1: TrackProfile, profile2: TrackProfile): Float {
        val energySim = 1f - abs(profile1.energy - profile2.energy) / 2f
        val valenceSim = 1f - abs(profile1.valence - profile2.valence) / 2f
        val tempoSim = 1f - abs(profile1.tempo - profile2.tempo) / 200f
        val danceSim = 1f - abs(profile1.danceability - profile2.danceability) / 2f
        val acousticSim = 1f - abs(profile1.acousticness - profile2.acousticness) / 2f
        val instrumentalSim = 1f - abs(profile1.instrumentalness - profile2.instrumentalness) / 2f
        
        return (energySim + valenceSim + tempoSim + danceSim + acousticSim + instrumentalSim) / 6f
    }
}

/**
 * Mood Analysis Algorithm
 * 
 * Analyzes user mood preferences and patterns
 */
class MoodAnalysisAlgorithm {
    
    /**
     * Analyze user mood preferences
     */
    fun analyzeUserPreferences(
        likedTracks: List<Track>,
        recentlyPlayed: List<Track>
    ): Map<TrackMood, Float> {
        val allTracks = likedTracks + recentlyPlayed
        if (allTracks.isEmpty()) return emptyMap()
        
        val moodCounts = allTracks.map { it.mood }
            .groupingBy { it }
            .eachCount()
        
        val totalTracks = allTracks.size
        return moodCounts.mapValues { (_, count) ->
            count.toFloat() / totalTracks
        }
    }
    
    /**
     * Get favorite mood from user tracks
     */
    fun getFavoriteMood(userTracks: List<Track>): TrackMood? {
        if (userTracks.isEmpty()) return null
        
        val moodCounts = userTracks.map { it.mood }
            .groupingBy { it }
            .eachCount()
        
        return moodCounts.maxByOrNull { it.value }?.key
    }
}

/**
 * Trend Analysis Algorithm
 * 
 * Analyzes trending tracks and popular content
 */
class TrendAnalysisAlgorithm {
    
    /**
     * Get trending tracks based on recent activity
     */
    fun getTrendingTracks(
        allTracks: List<Track>,
        recentlyPlayed: List<Track>,
        limit: Int = 20
    ): List<Track> {
        val currentTime = System.currentTimeMillis()
        val oneWeekAgo = currentTime - (7 * 24 * 60 * 60 * 1000L)
        
        // Calculate trend score for each track
        val scoredTracks = allTracks.map { track ->
            val trendScore = calculateTrendScore(track, recentlyPlayed, oneWeekAgo)
            track to trendScore
        }.sortedByDescending { it.second }
        
        return scoredTracks.take(limit).map { it.first }
    }
    
    /**
     * Calculate trend score for a track
     */
    private fun calculateTrendScore(
        track: Track,
        recentlyPlayed: List<Track>,
        oneWeekAgo: Long
    ): Float {
        var score = 0f
        
        // Base popularity score
        score += track.playCount * 0.1f
        
        // Recent play boost
        val recentPlays = recentlyPlayed.count { it.id == track.id }
        score += recentPlays * 0.5f
        
        // Recency boost
        if (track.dateAdded > oneWeekAgo) {
            score += 0.3f
        }
        
        // Energy boost for trending content
        score += track.energy * 0.2f
        
        return score
    }
}

/**
 * User profile for collaborative filtering
 */
data class UserProfile(
    val avgEnergy: Float = 0f,
    val avgValence: Float = 0f,
    val avgTempo: Float = 0f,
    val avgDanceability: Float = 0f,
    val avgAcousticness: Float = 0f,
    val avgInstrumentalness: Float = 0f,
    val genrePreferences: Map<String, Float> = emptyMap(),
    val moodPreferences: Map<TrackMood, Float> = emptyMap()
)

/**
 * Track profile for content-based filtering
 */
data class TrackProfile(
    val energy: Float = 0f,
    val valence: Float = 0f,
    val tempo: Float = 0f,
    val danceability: Float = 0f,
    val acousticness: Float = 0f,
    val instrumentalness: Float = 0f
)

/**
 * Extension to convert Track to TrackProfile
 */
private fun Track.toProfile(): TrackProfile {
    return TrackProfile(
        energy = energy,
        valence = valence,
        tempo = tempo,
        danceability = danceability,
        acousticness = acousticness,
        instrumentalness = instrumentalness
    )
}
