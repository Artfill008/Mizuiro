package com.mizuiro.music.discovery

import com.mizuiro.music.data.model.*
import com.mizuiro.music.discovery.algorithms.*
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Recommendation Algorithms
 */
class RecommendationAlgorithmsTest {
    
    private lateinit var collaborativeFilter: CollaborativeFilteringAlgorithm
    private lateinit var contentBasedFilter: ContentBasedFilteringAlgorithm
    private lateinit var moodAnalyzer: MoodAnalysisAlgorithm
    private lateinit var trendAnalyzer: TrendAnalysisAlgorithm
    
    private val sampleTracks = listOf(
        Track(
            id = "1",
            title = "Happy Pop Song",
            artist = "Pop Artist",
            album = "Pop Album",
            duration = 180000L,
            filePath = "/test/path1",
            dateAdded = System.currentTimeMillis(),
            playCount = 10,
            isLiked = true,
            mood = TrackMood.HAPPY,
            genre = "Pop",
            energy = 0.9f,
            valence = 0.9f,
            tempo = 120f,
            danceability = 0.8f,
            acousticness = 0.2f,
            instrumentalness = 0.1f
        ),
        Track(
            id = "2",
            title = "Sad Rock Song",
            artist = "Rock Artist",
            album = "Rock Album",
            duration = 200000L,
            filePath = "/test/path2",
            dateAdded = System.currentTimeMillis(),
            playCount = 5,
            isLiked = false,
            mood = TrackMood.SAD,
            genre = "Rock",
            energy = 0.3f,
            valence = 0.2f,
            tempo = 80f,
            danceability = 0.3f,
            acousticness = 0.9f,
            instrumentalness = 0.3f
        ),
        Track(
            id = "3",
            title = "Energetic Electronic",
            artist = "Electronic Artist",
            album = "Electronic Album",
            duration = 160000L,
            filePath = "/test/path3",
            dateAdded = System.currentTimeMillis(),
            playCount = 15,
            isLiked = true,
            mood = TrackMood.ENERGETIC,
            genre = "Electronic",
            energy = 0.95f,
            valence = 0.7f,
            tempo = 140f,
            danceability = 0.95f,
            acousticness = 0.1f,
            instrumentalness = 0.9f
        ),
        Track(
            id = "4",
            title = "Calm Jazz",
            artist = "Jazz Artist",
            album = "Jazz Album",
            duration = 240000L,
            filePath = "/test/path4",
            dateAdded = System.currentTimeMillis(),
            playCount = 3,
            isLiked = false,
            mood = TrackMood.CALM,
            genre = "Jazz",
            energy = 0.4f,
            valence = 0.6f,
            tempo = 90f,
            danceability = 0.4f,
            acousticness = 0.8f,
            instrumentalness = 0.7f
        )
    )
    
    private val sampleArtists = listOf(
        Artist(
            id = "artist1",
            name = "Pop Artist",
            trackIds = listOf("1"),
            albumIds = listOf("album1"),
            genre = "Pop",
            isLiked = true
        ),
        Artist(
            id = "artist2",
            name = "Rock Artist",
            trackIds = listOf("2"),
            albumIds = listOf("album2"),
            genre = "Rock",
            isLiked = false
        ),
        Artist(
            id = "artist3",
            name = "Electronic Artist",
            trackIds = listOf("3"),
            albumIds = listOf("album3"),
            genre = "Electronic",
            isLiked = true
        ),
        Artist(
            id = "artist4",
            name = "Jazz Artist",
            trackIds = listOf("4"),
            albumIds = listOf("album4"),
            genre = "Jazz",
            isLiked = false
        )
    )
    
    @Before
    fun setup() {
        collaborativeFilter = CollaborativeFilteringAlgorithm()
        contentBasedFilter = ContentBasedFilteringAlgorithm()
        moodAnalyzer = MoodAnalysisAlgorithm()
        trendAnalyzer = TrendAnalysisAlgorithm()
    }
    
    // Collaborative Filtering Tests
    
    @Test
    fun `collaborative filtering returns personalized recommendations`() {
        // Given
        val userTracks = sampleTracks.filter { it.isLiked }
        val recentlyPlayed = sampleTracks.take(2)
        
        // When
        val recommendations = collaborativeFilter.getPersonalizedRecommendations(
            allTracks = sampleTracks,
            userTracks = userTracks,
            recentlyPlayed = recentlyPlayed,
            limit = 5
        )
        
        // Then
        assertTrue("Should return recommendations", recommendations.isNotEmpty())
        assertTrue("Should not include liked tracks", 
            recommendations.none { it.isLiked })
        assertTrue("Should not exceed limit", recommendations.size <= 5)
    }
    
    @Test
    fun `collaborative filtering handles empty user tracks`() {
        // Given
        val userTracks = emptyList<Track>()
        val recentlyPlayed = emptyList<Track>()
        
        // When
        val recommendations = collaborativeFilter.getPersonalizedRecommendations(
            allTracks = sampleTracks,
            userTracks = userTracks,
            recentlyPlayed = recentlyPlayed,
            limit = 5
        )
        
        // Then
        assertTrue("Should return popular tracks when no user data", recommendations.isNotEmpty())
        assertTrue("Should not exceed limit", recommendations.size <= 5)
    }
    
    // Content-Based Filtering Tests
    
    @Test
    fun `content-based filtering returns mood recommendations`() {
        // Given
        val targetMood = TrackMood.HAPPY
        val userTracks = sampleTracks.filter { it.isLiked }
        
        // When
        val recommendations = contentBasedFilter.getMoodRecommendations(
            targetMood = targetMood,
            allTracks = sampleTracks,
            userTracks = userTracks,
            limit = 5
        )
        
        // Then
        assertTrue("Should return recommendations", recommendations.isNotEmpty())
        assertTrue("All recommendations should match mood", 
            recommendations.all { it.mood == targetMood })
        assertTrue("Should not include liked tracks", 
            recommendations.none { it.isLiked })
    }
    
    @Test
    fun `content-based filtering returns similar tracks`() {
        // Given
        val targetTrack = sampleTracks[0] // Happy Pop Song
        
        // When
        val similarTracks = contentBasedFilter.getSimilarTracks(
            targetTrack = targetTrack,
            allTracks = sampleTracks,
            limit = 3
        )
        
        // Then
        assertTrue("Should return similar tracks", similarTracks.isNotEmpty())
        assertTrue("Should not include target track", 
            similarTracks.none { it.id == targetTrack.id })
        assertTrue("Should not exceed limit", similarTracks.size <= 3)
    }
    
    @Test
    fun `content-based filtering returns genre recommendations`() {
        // Given
        val genre = "Pop"
        val userTracks = sampleTracks.filter { it.isLiked }
        
        // When
        val recommendations = contentBasedFilter.getGenreRecommendations(
            genre = genre,
            allTracks = sampleTracks,
            userTracks = userTracks,
            limit = 5
        )
        
        // Then
        assertTrue("Should return recommendations", recommendations.isNotEmpty())
        assertTrue("All recommendations should match genre", 
            recommendations.all { it.genre == genre })
        assertTrue("Should not include liked tracks", 
            recommendations.none { it.isLiked })
    }
    
    @Test
    fun `content-based filtering returns similar artists`() {
        // Given
        val targetArtist = sampleArtists[0] // Pop Artist
        
        // When
        val similarArtists = contentBasedFilter.getSimilarArtists(
            targetArtist = targetArtist,
            allArtists = sampleArtists,
            allTracks = sampleTracks,
            limit = 3
        )
        
        // Then
        assertTrue("Should return similar artists", similarArtists.isNotEmpty())
        assertTrue("Should not include target artist", 
            similarArtists.none { it.id == targetArtist.id })
        assertTrue("Should not exceed limit", similarArtists.size <= 3)
    }
    
    @Test
    fun `content-based filtering returns personalized tracks with criteria`() {
        // Given
        val userTracks = sampleTracks.filter { it.isLiked }
        val mood = TrackMood.HAPPY
        val genre = "Pop"
        val energyRange = 0.8f to 1.0f
        val valenceRange = 0.8f to 1.0f
        
        // When
        val recommendations = contentBasedFilter.getPersonalizedTracks(
            allTracks = sampleTracks,
            userTracks = userTracks,
            mood = mood,
            genre = genre,
            energyRange = energyRange,
            valenceRange = valenceRange,
            limit = 5
        )
        
        // Then
        assertTrue("Should return recommendations", recommendations.isNotEmpty())
        assertTrue("All recommendations should match mood", 
            recommendations.all { it.mood == mood })
        assertTrue("All recommendations should match genre", 
            recommendations.all { it.genre == genre })
        assertTrue("All recommendations should be in energy range", 
            recommendations.all { it.energy in energyRange.first..energyRange.second })
        assertTrue("All recommendations should be in valence range", 
            recommendations.all { it.valence in valenceRange.first..valenceRange.second })
    }
    
    @Test
    fun `content-based filtering returns favorite genre`() {
        // Given
        val userTracks = sampleTracks.filter { it.isLiked }
        
        // When
        val favoriteGenre = contentBasedFilter.getFavoriteGenre(userTracks)
        
        // Then
        assertNotNull("Should return favorite genre", favoriteGenre)
        assertTrue("Should be a valid genre", 
            sampleTracks.mapNotNull { it.genre }.contains(favoriteGenre))
    }
    
    // Mood Analysis Tests
    
    @Test
    fun `mood analyzer returns user preferences`() {
        // Given
        val likedTracks = sampleTracks.filter { it.isLiked }
        val recentlyPlayed = sampleTracks.take(2)
        
        // When
        val moodPreferences = moodAnalyzer.analyzeUserPreferences(
            likedTracks = likedTracks,
            recentlyPlayed = recentlyPlayed
        )
        
        // Then
        assertTrue("Should return mood preferences", moodPreferences.isNotEmpty())
        assertTrue("Should include all moods", 
            moodPreferences.keys.containsAll(TrackMood.values().toList()))
        assertTrue("All preferences should be between 0 and 1", 
            moodPreferences.values.all { it in 0f..1f })
    }
    
    @Test
    fun `mood analyzer returns favorite mood`() {
        // Given
        val userTracks = sampleTracks.filter { it.isLiked }
        
        // When
        val favoriteMood = moodAnalyzer.getFavoriteMood(userTracks)
        
        // Then
        assertNotNull("Should return favorite mood", favoriteMood)
        assertTrue("Should be a valid mood", 
            TrackMood.values().contains(favoriteMood))
    }
    
    @Test
    fun `mood analyzer handles empty tracks`() {
        // Given
        val emptyTracks = emptyList<Track>()
        
        // When
        val moodPreferences = moodAnalyzer.analyzeUserPreferences(
            likedTracks = emptyTracks,
            recentlyPlayed = emptyTracks
        )
        val favoriteMood = moodAnalyzer.getFavoriteMood(emptyTracks)
        
        // Then
        assertTrue("Should return empty preferences for empty tracks", moodPreferences.isEmpty())
        assertNull("Should return null favorite mood for empty tracks", favoriteMood)
    }
    
    // Trend Analysis Tests
    
    @Test
    fun `trend analyzer returns trending tracks`() {
        // Given
        val recentlyPlayed = sampleTracks.take(2)
        
        // When
        val trendingTracks = trendAnalyzer.getTrendingTracks(
            allTracks = sampleTracks,
            recentlyPlayed = recentlyPlayed,
            limit = 5
        )
        
        // Then
        assertTrue("Should return trending tracks", trendingTracks.isNotEmpty())
        assertTrue("Should not exceed limit", trendingTracks.size <= 5)
    }
    
    @Test
    fun `trend analyzer handles empty tracks`() {
        // Given
        val emptyTracks = emptyList<Track>()
        val emptyRecentlyPlayed = emptyList<Track>()
        
        // When
        val trendingTracks = trendAnalyzer.getTrendingTracks(
            allTracks = emptyTracks,
            recentlyPlayed = emptyRecentlyPlayed,
            limit = 5
        )
        
        // Then
        assertTrue("Should return empty list for empty tracks", trendingTracks.isEmpty())
    }
    
    // Profile Similarity Tests
    
    @Test
    fun `profile similarity calculation works correctly`() {
        // Given
        val profile1 = TrackProfile(
            energy = 0.8f,
            valence = 0.7f,
            tempo = 120f,
            danceability = 0.6f,
            acousticness = 0.3f,
            instrumentalness = 0.2f
        )
        val profile2 = TrackProfile(
            energy = 0.9f,
            valence = 0.8f,
            tempo = 130f,
            danceability = 0.7f,
            acousticness = 0.2f,
            instrumentalness = 0.1f
        )
        
        // When
        val similarity = contentBasedFilter.calculateProfileSimilarity(profile1, profile2)
        
        // Then
        assertTrue("Similarity should be between 0 and 1", similarity in 0f..1f)
        assertTrue("Similar profiles should have high similarity", similarity > 0.5f)
    }
    
    @Test
    fun `profile similarity calculation handles identical profiles`() {
        // Given
        val profile = TrackProfile(
            energy = 0.8f,
            valence = 0.7f,
            tempo = 120f,
            danceability = 0.6f,
            acousticness = 0.3f,
            instrumentalness = 0.2f
        )
        
        // When
        val similarity = contentBasedFilter.calculateProfileSimilarity(profile, profile)
        
        // Then
        assertEquals("Identical profiles should have similarity of 1", 1f, similarity, 0.01f)
    }
    
    @Test
    fun `profile similarity calculation handles opposite profiles`() {
        // Given
        val profile1 = TrackProfile(
            energy = 0.1f,
            valence = 0.1f,
            tempo = 60f,
            danceability = 0.1f,
            acousticness = 0.9f,
            instrumentalness = 0.9f
        )
        val profile2 = TrackProfile(
            energy = 0.9f,
            valence = 0.9f,
            tempo = 180f,
            danceability = 0.9f,
            acousticness = 0.1f,
            instrumentalness = 0.1f
        )
        
        // When
        val similarity = contentBasedFilter.calculateProfileSimilarity(profile1, profile2)
        
        // Then
        assertTrue("Opposite profiles should have low similarity", similarity < 0.5f)
    }
}
