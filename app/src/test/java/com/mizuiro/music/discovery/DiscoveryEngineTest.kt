package com.mizuiro.music.discovery

import com.mizuiro.music.data.model.*
import com.mizuiro.music.discovery.engine.DiscoveryEngine
import com.mizuiro.music.discovery.engine.DiscoveryMode
import com.mizuiro.music.discovery.engine.UserPreferences
import com.mizuiro.music.library.manager.LibraryManager
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for DiscoveryEngine
 */
class DiscoveryEngineTest {
    
    private lateinit var libraryManager: LibraryManager
    private lateinit var discoveryEngine: DiscoveryEngine
    
    private val sampleTracks = listOf(
        Track(
            id = "1",
            title = "Test Track 1",
            artist = "Test Artist 1",
            album = "Test Album 1",
            duration = 180000L,
            filePath = "/test/path1",
            dateAdded = System.currentTimeMillis(),
            playCount = 5,
            isLiked = true,
            mood = TrackMood.HAPPY,
            genre = "Pop",
            energy = 0.8f,
            valence = 0.9f,
            tempo = 120f,
            danceability = 0.7f,
            acousticness = 0.3f,
            instrumentalness = 0.1f
        ),
        Track(
            id = "2",
            title = "Test Track 2",
            artist = "Test Artist 2",
            album = "Test Album 2",
            duration = 200000L,
            filePath = "/test/path2",
            dateAdded = System.currentTimeMillis(),
            playCount = 3,
            isLiked = false,
            mood = TrackMood.SAD,
            genre = "Rock",
            energy = 0.6f,
            valence = 0.3f,
            tempo = 100f,
            danceability = 0.5f,
            acousticness = 0.8f,
            instrumentalness = 0.2f
        ),
        Track(
            id = "3",
            title = "Test Track 3",
            artist = "Test Artist 1",
            album = "Test Album 3",
            duration = 160000L,
            filePath = "/test/path3",
            dateAdded = System.currentTimeMillis(),
            playCount = 8,
            isLiked = true,
            mood = TrackMood.ENERGETIC,
            genre = "Electronic",
            energy = 0.9f,
            valence = 0.7f,
            tempo = 140f,
            danceability = 0.9f,
            acousticness = 0.1f,
            instrumentalness = 0.8f
        )
    )
    
    private val sampleArtists = listOf(
        Artist(
            id = "artist1",
            name = "Test Artist 1",
            trackIds = listOf("1", "3"),
            albumIds = listOf("album1", "album3"),
            genre = "Pop",
            isLiked = true
        ),
        Artist(
            id = "artist2",
            name = "Test Artist 2",
            trackIds = listOf("2"),
            albumIds = listOf("album2"),
            genre = "Rock",
            isLiked = false
        )
    )
    
    private val sampleAlbums = listOf(
        Album(
            id = "album1",
            title = "Test Album 1",
            artist = "Test Artist 1",
            trackIds = listOf("1"),
            year = 2023,
            genre = "Pop",
            isLiked = true
        ),
        Album(
            id = "album2",
            title = "Test Album 2",
            artist = "Test Artist 2",
            trackIds = listOf("2"),
            year = 2022,
            genre = "Rock",
            isLiked = false
        )
    )
    
    @Before
    fun setup() {
        libraryManager = mockk<LibraryManager>()
        
        // Mock StateFlow properties
        every { libraryManager.tracks } returns MutableStateFlow(sampleTracks)
        every { libraryManager.artists } returns MutableStateFlow(sampleArtists)
        every { libraryManager.albums } returns MutableStateFlow(sampleAlbums)
        every { libraryManager.likedTracks } returns MutableStateFlow(sampleTracks.filter { it.isLiked })
        every { libraryManager.recentlyPlayed } returns MutableStateFlow(sampleTracks.take(2))
        
        discoveryEngine = DiscoveryEngine(libraryManager)
    }
    
    @Test
    fun `getMoodRecommendations returns tracks for specific mood`() = runTest {
        // Given
        val mood = TrackMood.HAPPY
        
        // When
        val recommendations = discoveryEngine.getMoodRecommendations(mood, 10)
        
        // Then
        assertTrue("Should return recommendations", recommendations.isNotEmpty())
        assertTrue("All recommendations should match the mood", 
            recommendations.all { it.mood == mood })
    }
    
    @Test
    fun `getPersonalizedRecommendations returns tracks based on user preferences`() = runTest {
        // When
        val recommendations = discoveryEngine.getPersonalizedRecommendations(10)
        
        // Then
        assertTrue("Should return recommendations", recommendations.isNotEmpty())
        assertTrue("Should not include already liked tracks", 
            recommendations.none { it.isLiked })
    }
    
    @Test
    fun `getSimilarTracks returns tracks similar to target track`() = runTest {
        // Given
        val targetTrack = sampleTracks[0]
        
        // When
        val similarTracks = discoveryEngine.getSimilarTracks(targetTrack.id, 5)
        
        // Then
        assertTrue("Should return similar tracks", similarTracks.isNotEmpty())
        assertTrue("Should not include the target track", 
            similarTracks.none { it.id == targetTrack.id })
    }
    
    @Test
    fun `getTrendingTracks returns popular tracks`() = runTest {
        // When
        val trendingTracks = discoveryEngine.getTrendingTracks(10)
        
        // Then
        assertTrue("Should return trending tracks", trendingTracks.isNotEmpty())
        // Should be sorted by popularity (play count)
        val playCounts = trendingTracks.map { it.playCount }
        assertEquals("Should be sorted by play count", 
            playCounts.sortedDescending(), playCounts)
    }
    
    @Test
    fun `getNewReleases returns recently added tracks`() = runTest {
        // When
        val newReleases = discoveryEngine.getNewReleases(10)
        
        // Then
        assertTrue("Should return new releases", newReleases.isNotEmpty())
        // Should be sorted by date added (most recent first)
        val dates = newReleases.map { it.dateAdded }
        assertEquals("Should be sorted by date added", 
            dates.sortedDescending(), dates)
    }
    
    @Test
    fun `getGenreExploration returns tracks for specific genre`() = runTest {
        // Given
        val genre = "Pop"
        
        // When
        val genreTracks = discoveryEngine.getGenreExploration(genre, 10)
        
        // Then
        assertTrue("Should return genre tracks", genreTracks.isNotEmpty())
        assertTrue("All tracks should match the genre", 
            genreTracks.all { it.genre == genre })
    }
    
    @Test
    fun `getSimilarArtists returns artists similar to target artist`() = runTest {
        // Given
        val targetArtist = sampleArtists[0]
        
        // When
        val similarArtists = discoveryEngine.getSimilarArtists(targetArtist.id, 5)
        
        // Then
        assertTrue("Should return similar artists", similarArtists.isNotEmpty())
        assertTrue("Should not include the target artist", 
            similarArtists.none { it.id == targetArtist.id })
    }
    
    @Test
    fun `createPersonalizedPlaylist creates playlist with specified criteria`() = runTest {
        // Given
        val name = "Test Playlist"
        val description = "Test Description"
        val mood = TrackMood.HAPPY
        val genre = "Pop"
        val energyRange = 0.7f to 1.0f
        val valenceRange = 0.8f to 1.0f
        
        // When
        val playlist = discoveryEngine.createPersonalizedPlaylist(
            name = name,
            description = description,
            mood = mood,
            genre = genre,
            energyRange = energyRange,
            valenceRange = valenceRange,
            limit = 20
        )
        
        // Then
        assertEquals("Should have correct name", name, playlist.name)
        assertEquals("Should have correct description", description, playlist.description)
        assertEquals("Should have correct mood", mood, playlist.mood)
        assertEquals("Should have correct genre", genre, playlist.genre)
        assertEquals("Should have correct energy range", energyRange, playlist.energyRange)
        assertEquals("Should have correct valence range", valenceRange, playlist.valenceRange)
        assertTrue("Should be personalized", playlist.isPersonalized)
        assertTrue("Should have tracks", playlist.trackCount > 0)
    }
    
    @Test
    fun `analyzeUserMoodPreferences returns mood preferences`() = runTest {
        // When
        val moodPreferences = discoveryEngine.analyzeUserMoodPreferences()
        
        // Then
        assertTrue("Should return mood preferences", moodPreferences.isNotEmpty())
        assertTrue("Should include all moods", 
            moodPreferences.keys.containsAll(TrackMood.values().toList()))
        assertTrue("All preferences should be between 0 and 1", 
            moodPreferences.values.all { it in 0f..1f })
    }
    
    @Test
    fun `getDiscoveryInsights returns user insights`() = runTest {
        // When
        val insights = discoveryEngine.getDiscoveryInsights()
        
        // Then
        assertTrue("Should have total tracks discovered", insights.totalTracksDiscovered >= 0)
        assertTrue("Should have average energy between 0 and 1", 
            insights.averageEnergy in 0f..1f)
        assertTrue("Should have average valence between 0 and 1", 
            insights.averageValence in 0f..1f)
        assertTrue("Should have discovery score between 0 and 1", 
            insights.discoveryScore in 0f..1f)
        assertTrue("Should have last updated timestamp", insights.lastUpdated > 0)
    }
    
    @Test
    fun `updateUserPreferences updates preferences and refreshes recommendations`() = runTest {
        // Given
        val newPreferences = UserPreferences(
            favoriteMoods = listOf(TrackMood.HAPPY, TrackMood.ENERGETIC),
            favoriteGenres = listOf("Pop", "Electronic"),
            energyPreference = 0.7f to 1.0f,
            valencePreference = 0.6f to 1.0f,
            discoveryMode = DiscoveryMode.EXPLORATORY,
            includeExplicit = false,
            languagePreference = "en"
        )
        
        // When
        discoveryEngine.updateUserPreferences(newPreferences)
        
        // Then
        val currentPreferences = discoveryEngine.userPreferences.value
        assertEquals("Should update favorite moods", 
            newPreferences.favoriteMoods, currentPreferences.favoriteMoods)
        assertEquals("Should update favorite genres", 
            newPreferences.favoriteGenres, currentPreferences.favoriteGenres)
        assertEquals("Should update energy preference", 
            newPreferences.energyPreference, currentPreferences.energyPreference)
        assertEquals("Should update valence preference", 
            newPreferences.valencePreference, currentPreferences.valencePreference)
        assertEquals("Should update discovery mode", 
            newPreferences.discoveryMode, currentPreferences.discoveryMode)
        assertEquals("Should update explicit content setting", 
            newPreferences.includeExplicit, currentPreferences.includeExplicit)
        assertEquals("Should update language preference", 
            newPreferences.languagePreference, currentPreferences.languagePreference)
    }
    
    @Test
    fun `refreshRecommendations updates all recommendation collections`() = runTest {
        // When
        discoveryEngine.refreshRecommendations()
        
        // Then
        val moodRecommendations = discoveryEngine.moodRecommendations.value
        val trendingTracks = discoveryEngine.trendingTracks.value
        val newReleases = discoveryEngine.newReleases.value
        val genreExploration = discoveryEngine.genreExploration.value
        val similarArtists = discoveryEngine.similarArtists.value
        
        assertTrue("Should update mood recommendations", moodRecommendations.isNotEmpty())
        assertTrue("Should update trending tracks", trendingTracks.isNotEmpty())
        assertTrue("Should update new releases", newReleases.isNotEmpty())
        assertTrue("Should update genre exploration", genreExploration.isNotEmpty())
        assertTrue("Should update similar artists", similarArtists.isNotEmpty())
    }
    
    @Test
    fun `discovery state is properly initialized`() = runTest {
        // When
        val discoveryState = discoveryEngine.discoveryState.value
        
        // Then
        assertFalse("Should not be loading initially", discoveryState.isLoading)
        assertFalse("Should not be refreshing initially", discoveryState.isRefreshing)
        assertEquals("Should have zero last refresh initially", 0L, discoveryState.lastRefresh)
        assertNull("Should have no error initially", discoveryState.error)
    }
}
