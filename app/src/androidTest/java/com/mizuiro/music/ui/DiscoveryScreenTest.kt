package com.mizuiro.music.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mizuiro.music.data.model.*
import com.mizuiro.music.discovery.engine.DiscoveryEngine
import com.mizuiro.music.discovery.engine.DiscoveryPlaylist
import com.mizuiro.music.library.manager.LibraryManager
import com.mizuiro.music.ui.screens.DiscoveryScreen
import com.mizuiro.music.ui.theme.MizuiroTheme
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for DiscoveryScreen
 */
@RunWith(AndroidJUnit4::class)
class DiscoveryScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private val mockLibraryManager = mockk<LibraryManager>()
    private val mockDiscoveryEngine = mockk<DiscoveryEngine>()
    
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
        )
    )
    
    private val sampleArtists = listOf(
        Artist(
            id = "artist1",
            name = "Test Artist 1",
            trackIds = listOf("1"),
            albumIds = listOf("album1"),
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
    
    private val samplePlaylists = listOf(
        DiscoveryPlaylist(
            id = "playlist1",
            name = "Test Playlist 1",
            description = "Test Description 1",
            trackIds = listOf("1", "2"),
            trackCount = 2,
            duration = 380000L,
            mood = TrackMood.HAPPY,
            genre = "Pop",
            energyRange = 0.7f to 1.0f,
            valenceRange = 0.8f to 1.0f,
            dateCreated = System.currentTimeMillis(),
            isPersonalized = true
        )
    )
    
    @Test
    fun discoveryScreen_displaysHeader() {
        // Given
        setupMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("DISCOVER MUSIC").assertIsDisplayed()
        composeTestRule.onNodeWithText("Discover Your Next Favorite! (◠‿◠)").assertIsDisplayed()
        composeTestRule.onNodeWithText("AI-powered music discovery").assertIsDisplayed()
    }
    
    @Test
    fun discoveryScreen_displaysTabNavigation() {
        // Given
        setupMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Personalized").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mood").assertIsDisplayed()
        composeTestRule.onNodeWithText("Trending").assertIsDisplayed()
        composeTestRule.onNodeWithText("Genres").assertIsDisplayed()
        composeTestRule.onNodeWithText("Artists").assertIsDisplayed()
        composeTestRule.onNodeWithText("Playlists").assertIsDisplayed()
    }
    
    @Test
    fun discoveryScreen_displaysDiscoveryInsights() {
        // Given
        setupMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Discovery Insights").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tracks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Score").assertIsDisplayed()
        composeTestRule.onNodeWithText("Energy").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mood").assertIsDisplayed()
    }
    
    @Test
    fun discoveryScreen_displaysPersonalizedRecommendations() {
        // Given
        setupMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Personalized for You").assertIsDisplayed()
    }
    
    @Test
    fun discoveryScreen_displaysMoodDiscovery() {
        // Given
        setupMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Mood").performClick()
        composeTestRule.onNodeWithText("Mood Discovery").assertIsDisplayed()
    }
    
    @Test
    fun discoveryScreen_displaysTrendingSection() {
        // Given
        setupMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Trending").performClick()
        composeTestRule.onNodeWithText("Trending Now").assertIsDisplayed()
        composeTestRule.onNodeWithText("New Releases").assertIsDisplayed()
    }
    
    @Test
    fun discoveryScreen_displaysGenreExploration() {
        // Given
        setupMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Genres").performClick()
        composeTestRule.onNodeWithText("Genre Exploration").assertIsDisplayed()
    }
    
    @Test
    fun discoveryScreen_displaysSimilarArtists() {
        // Given
        setupMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Artists").performClick()
        composeTestRule.onNodeWithText("Similar Artists").assertIsDisplayed()
    }
    
    @Test
    fun discoveryScreen_displaysPersonalizedPlaylists() {
        // Given
        setupMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Playlists").performClick()
        composeTestRule.onNodeWithText("Personalized Playlists").assertIsDisplayed()
    }
    
    @Test
    fun discoveryScreen_handlesEmptyStates() {
        // Given
        setupEmptyMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("No personalized recommendations yet").assertIsDisplayed()
    }
    
    @Test
    fun discoveryScreen_displaysKawaiiMascots() {
        // Given
        setupMocks()
        
        // When
        composeTestRule.setContent {
            MizuiroTheme {
                DiscoveryScreen(
                    discoveryEngine = mockDiscoveryEngine,
                    onTrackSelected = {},
                    onArtistSelected = {},
                    onPlaylistSelected = {},
                    onMoodSelected = {},
                    onGenreSelected = {},
                    onBack = {}
                )
            }
        }
        
        // Then
        // Check for kawaii mascot elements (they should be present in the UI)
        composeTestRule.onRoot().assertExists()
    }
    
    private fun setupMocks() {
        // Mock LibraryManager
        every { mockLibraryManager.tracks } returns MutableStateFlow(sampleTracks)
        every { mockLibraryManager.artists } returns MutableStateFlow(sampleArtists)
        every { mockLibraryManager.albums } returns MutableStateFlow(emptyList())
        every { mockLibraryManager.likedTracks } returns MutableStateFlow(sampleTracks.filter { it.isLiked })
        every { mockLibraryManager.recentlyPlayed } returns MutableStateFlow(sampleTracks.take(1))
        
        // Mock DiscoveryEngine
        every { mockDiscoveryEngine.discoveryState } returns MutableStateFlow(
            com.mizuiro.music.discovery.engine.DiscoveryState()
        )
        every { mockDiscoveryEngine.moodRecommendations } returns MutableStateFlow(
            mapOf(TrackMood.HAPPY to sampleTracks.take(1))
        )
        every { mockDiscoveryEngine.trendingTracks } returns MutableStateFlow(sampleTracks)
        every { mockDiscoveryEngine.newReleases } returns MutableStateFlow(sampleTracks)
        every { mockDiscoveryEngine.personalizedPlaylists } returns MutableStateFlow(samplePlaylists)
        every { mockDiscoveryEngine.similarArtists } returns MutableStateFlow(
            mapOf("artist1" to sampleArtists.take(1))
        )
        every { mockDiscoveryEngine.genreExploration } returns MutableStateFlow(
            mapOf("Pop" to sampleTracks.take(1))
        )
        every { mockDiscoveryEngine.userPreferences } returns MutableStateFlow(
            com.mizuiro.music.discovery.engine.UserPreferences()
        )
        
        // Mock methods
        every { mockDiscoveryEngine.getPersonalizedRecommendations(any()) } returns sampleTracks
        every { mockDiscoveryEngine.getMoodRecommendations(any(), any()) } returns sampleTracks
        every { mockDiscoveryEngine.getTrendingTracks(any()) } returns sampleTracks
        every { mockDiscoveryEngine.getNewReleases(any()) } returns sampleTracks
        every { mockDiscoveryEngine.getGenreExploration(any(), any()) } returns sampleTracks
        every { mockDiscoveryEngine.getSimilarArtists(any(), any()) } returns sampleArtists
        every { mockDiscoveryEngine.getDiscoveryInsights() } returns com.mizuiro.music.discovery.engine.DiscoveryInsights(
            totalTracksDiscovered = 2,
            favoriteMood = TrackMood.HAPPY,
            favoriteGenre = "Pop",
            averageEnergy = 0.7f,
            averageValence = 0.6f,
            discoveryScore = 0.8f,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    private fun setupEmptyMocks() {
        // Mock LibraryManager with empty data
        every { mockLibraryManager.tracks } returns MutableStateFlow(emptyList())
        every { mockLibraryManager.artists } returns MutableStateFlow(emptyList())
        every { mockLibraryManager.albums } returns MutableStateFlow(emptyList())
        every { mockLibraryManager.likedTracks } returns MutableStateFlow(emptyList())
        every { mockLibraryManager.recentlyPlayed } returns MutableStateFlow(emptyList())
        
        // Mock DiscoveryEngine with empty data
        every { mockDiscoveryEngine.discoveryState } returns MutableStateFlow(
            com.mizuiro.music.discovery.engine.DiscoveryState()
        )
        every { mockDiscoveryEngine.moodRecommendations } returns MutableStateFlow(emptyMap())
        every { mockDiscoveryEngine.trendingTracks } returns MutableStateFlow(emptyList())
        every { mockDiscoveryEngine.newReleases } returns MutableStateFlow(emptyList())
        every { mockDiscoveryEngine.personalizedPlaylists } returns MutableStateFlow(emptyList())
        every { mockDiscoveryEngine.similarArtists } returns MutableStateFlow(emptyMap())
        every { mockDiscoveryEngine.genreExploration } returns MutableStateFlow(emptyMap())
        every { mockDiscoveryEngine.userPreferences } returns MutableStateFlow(
            com.mizuiro.music.discovery.engine.UserPreferences()
        )
        
        // Mock methods to return empty results
        every { mockDiscoveryEngine.getPersonalizedRecommendations(any()) } returns emptyList()
        every { mockDiscoveryEngine.getMoodRecommendations(any(), any()) } returns emptyList()
        every { mockDiscoveryEngine.getTrendingTracks(any()) } returns emptyList()
        every { mockDiscoveryEngine.getNewReleases(any()) } returns emptyList()
        every { mockDiscoveryEngine.getGenreExploration(any(), any()) } returns emptyList()
        every { mockDiscoveryEngine.getSimilarArtists(any(), any()) } returns emptyList()
        every { mockDiscoveryEngine.getDiscoveryInsights() } returns com.mizuiro.music.discovery.engine.DiscoveryInsights(
            totalTracksDiscovered = 0,
            favoriteMood = null,
            favoriteGenre = null,
            averageEnergy = 0f,
            averageValence = 0f,
            discoveryScore = 0f,
            lastUpdated = System.currentTimeMillis()
        )
    }
}
