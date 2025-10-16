package com.mizuiro.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mizuiro.music.data.model.*
import com.mizuiro.music.discovery.engine.DiscoveryEngine
import com.mizuiro.music.discovery.engine.DiscoveryInsights
import com.mizuiro.music.discovery.engine.DiscoveryPlaylist
import com.mizuiro.music.ui.components.buttons.MizuiroButton
import com.mizuiro.music.ui.components.buttons.MizuiroIconButton
import com.mizuiro.music.ui.components.decorations.KawaiiMascot
import com.mizuiro.music.ui.components.decorations.MascotState
import com.mizuiro.music.ui.components.decorations.WobblyDivider
import com.mizuiro.music.ui.theme.*

/**
 * Discovery Screen
 * 
 * Advanced music discovery interface featuring:
 * - Personalized recommendations
 * - Mood-based discovery
 * - Trending tracks
 * - Genre exploration
 * - Similar artists
 * - Discovery insights
 */
@Composable
fun DiscoveryScreen(
    modifier: Modifier = Modifier,
    discoveryEngine: DiscoveryEngine,
    onTrackSelected: (Track) -> Unit = {},
    onArtistSelected: (Artist) -> Unit = {},
    onPlaylistSelected: (DiscoveryPlaylist) -> Unit = {},
    onMoodSelected: (TrackMood) -> Unit = {},
    onGenreSelected: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val discoveryState by discoveryEngine.discoveryState.collectAsState()
    val moodRecommendations by discoveryEngine.moodRecommendations.collectAsState()
    val trendingTracks by discoveryEngine.trendingTracks.collectAsState()
    val newReleases by discoveryEngine.newReleases.collectAsState()
    val personalizedPlaylists by discoveryEngine.personalizedPlaylists.collectAsState()
    val similarArtists by discoveryEngine.similarArtists.collectAsState()
    val genreExploration by discoveryEngine.genreExploration.collectAsState()
    val userPreferences by discoveryEngine.userPreferences.collectAsState()
    
    var selectedTab by remember { mutableStateOf("personalized") }
    var discoveryInsights by remember { mutableStateOf<DiscoveryInsights?>(null) }
    
    // Load discovery insights
    LaunchedEffect(Unit) {
        discoveryInsights = discoveryEngine.getDiscoveryInsights()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CoolWhite,
                        IcyGrey.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ASCII art header
                    Text(
                        text = "╔════════════════════════════════╗",
                        style = VT323Style,
                        color = CyberSilver
                    )
                    Text(
                        text = "║      DISCOVER MUSIC           ║",
                        style = VT323Style,
                        color = MizuiroBase
                    )
                    Text(
                        text = "╚════════════════════════════════╝",
                        style = VT323Style,
                        color = CyberSilver
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Kawaii mascot
                    KawaiiMascot(
                        state = MascotState.Excited,
                        size = 64.dp
                    )
                    
                    Text(
                        text = "Discover Your Next Favorite! (◠‿◠)",
                        style = H1,
                        color = FadedBlack,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "AI-powered music discovery",
                        style = Body,
                        color = SteelBlue,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Wobbly divider
            item {
                WobblyDivider()
            }
            
            // Discovery insights
            discoveryInsights?.let { insights ->
                item {
                    DiscoveryInsightsCard(insights = insights)
                }
            }
            
            // Tab navigation
            item {
                DiscoveryTabNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
            
            // Content based on selected tab
            when (selectedTab) {
                "personalized" -> {
                    item {
                        PersonalizedRecommendationsSection(
                            discoveryEngine = discoveryEngine,
                            onTrackSelected = onTrackSelected
                        )
                    }
                }
                "mood" -> {
                    item {
                        MoodDiscoverySection(
                            moodRecommendations = moodRecommendations,
                            onMoodSelected = onMoodSelected,
                            onTrackSelected = onTrackSelected
                        )
                    }
                }
                "trending" -> {
                    item {
                        TrendingSection(
                            trendingTracks = trendingTracks,
                            newReleases = newReleases,
                            onTrackSelected = onTrackSelected
                        )
                    }
                }
                "genres" -> {
                    item {
                        GenreExplorationSection(
                            genreExploration = genreExploration,
                            onGenreSelected = onGenreSelected,
                            onTrackSelected = onTrackSelected
                        )
                    }
                }
                "artists" -> {
                    item {
                        SimilarArtistsSection(
                            similarArtists = similarArtists,
                            onArtistSelected = onArtistSelected
                        )
                    }
                }
                "playlists" -> {
                    item {
                        PersonalizedPlaylistsSection(
                            personalizedPlaylists = personalizedPlaylists,
                            onPlaylistSelected = onPlaylistSelected
                        )
                    }
                }
            }
            
            // Decorative elements
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(5) {
                        KawaiiMascot(
                            state = MascotState.Dancing,
                            size = 24.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiscoveryInsightsCard(insights: DiscoveryInsights) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = IcyGrey.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Discovery Insights",
                style = H2,
                color = FadedBlack
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InsightItem(
                    label = "Tracks",
                    value = insights.totalTracksDiscovered.toString(),
                    color = MizuiroBase
                )
                InsightItem(
                    label = "Score",
                    value = String.format("%.1f", insights.discoveryScore),
                    color = SteelBlue
                )
                InsightItem(
                    label = "Energy",
                    value = String.format("%.1f", insights.averageEnergy),
                    color = BlushPink
                )
                InsightItem(
                    label = "Mood",
                    value = insights.favoriteMood?.name ?: "N/A",
                    color = CyberSilver
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InsightItem(
                    label = "Genre",
                    value = insights.favoriteGenre ?: "N/A",
                    color = MizuiroBase
                )
                InsightItem(
                    label = "Valence",
                    value = String.format("%.1f", insights.averageValence),
                    color = SteelBlue
                )
                InsightItem(
                    label = "Updated",
                    value = formatTimeAgo(insights.lastUpdated),
                    color = BlushPink
                )
                InsightItem(
                    label = "Status",
                    value = "Active",
                    color = CyberSilver
                )
            }
        }
    }
}

@Composable
fun InsightItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = H2,
            color = color
        )
        Text(
            text = label,
            style = Caption,
            color = SteelBlue
        )
    }
}

@Composable
fun DiscoveryTabNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf(
        "personalized" to "Personalized",
        "mood" to "Mood",
        "trending" to "Trending",
        "genres" to "Genres",
        "artists" to "Artists",
        "playlists" to "Playlists"
    )
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tabs) { (id, name) ->
            MizuiroButton(
                text = name,
                onClick = { onTabSelected(id) },
                secondary = selectedTab != id,
                fullWidth = false
            )
        }
    }
}

@Composable
fun PersonalizedRecommendationsSection(
    discoveryEngine: DiscoveryEngine,
    onTrackSelected: (Track) -> Unit
) {
    var personalizedTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        personalizedTracks = discoveryEngine.getPersonalizedRecommendations(20)
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Personalized for You",
            style = H2,
            color = FadedBlack
        )
        
        if (personalizedTracks.isEmpty()) {
            EmptyState(
                message = "No personalized recommendations yet",
                icon = "🎵"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(personalizedTracks.take(10)) { track ->
                    TrackCard(
                        track = track,
                        onClick = { onTrackSelected(track) }
                    )
                }
            }
        }
    }
}

@Composable
fun MoodDiscoverySection(
    moodRecommendations: Map<TrackMood, List<Track>>,
    onMoodSelected: (TrackMood) -> Unit,
    onTrackSelected: (Track) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mood Discovery",
            style = H2,
            color = FadedBlack
        )
        
        if (moodRecommendations.isEmpty()) {
            EmptyState(
                message = "No mood recommendations available",
                icon = "🎭"
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(TrackMood.values().toList()) { mood ->
                    val tracks = moodRecommendations[mood] ?: emptyList()
                    if (tracks.isNotEmpty()) {
                        MoodCard(
                            mood = mood,
                            tracks = tracks,
                            onMoodSelected = onMoodSelected,
                            onTrackSelected = onTrackSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingSection(
    trendingTracks: List<Track>,
    newReleases: List<Track>,
    onTrackSelected: (Track) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Trending Now",
            style = H2,
            color = FadedBlack
        )
        
        if (trendingTracks.isEmpty()) {
            EmptyState(
                message = "No trending tracks",
                icon = "🔥"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trendingTracks.take(10)) { track ->
                    TrackCard(
                        track = track,
                        onClick = { onTrackSelected(track) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "New Releases",
            style = H2,
            color = FadedBlack
        )
        
        if (newReleases.isEmpty()) {
            EmptyState(
                message = "No new releases",
                icon = "🆕"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(newReleases.take(10)) { track ->
                    TrackCard(
                        track = track,
                        onClick = { onTrackSelected(track) }
                    )
                }
            }
        }
    }
}

@Composable
fun GenreExplorationSection(
    genreExploration: Map<String, List<Track>>,
    onGenreSelected: (String) -> Unit,
    onTrackSelected: (Track) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Genre Exploration",
            style = H2,
            color = FadedBlack
        )
        
        if (genreExploration.isEmpty()) {
            EmptyState(
                message = "No genres available",
                icon = "🎼"
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(genreExploration.keys.toList()) { genre ->
                    val tracks = genreExploration[genre] ?: emptyList()
                    if (tracks.isNotEmpty()) {
                        GenreCard(
                            genre = genre,
                            tracks = tracks,
                            onGenreSelected = onGenreSelected,
                            onTrackSelected = onTrackSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimilarArtistsSection(
    similarArtists: Map<String, List<Artist>>,
    onArtistSelected: (Artist) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Similar Artists",
            style = H2,
            color = FadedBlack
        )
        
        if (similarArtists.isEmpty()) {
            EmptyState(
                message = "No similar artists found",
                icon = "🎤"
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(similarArtists.entries.toList()) { (artistId, similar) ->
                    if (similar.isNotEmpty()) {
                        SimilarArtistsCard(
                            similarArtists = similar,
                            onArtistSelected = onArtistSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalizedPlaylistsSection(
    personalizedPlaylists: List<DiscoveryPlaylist>,
    onPlaylistSelected: (DiscoveryPlaylist) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Personalized Playlists",
            style = H2,
            color = FadedBlack
        )
        
        if (personalizedPlaylists.isEmpty()) {
            EmptyState(
                message = "No personalized playlists",
                icon = "📝"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(personalizedPlaylists) { playlist ->
                    DiscoveryPlaylistCard(
                        playlist = playlist,
                        onClick = { onPlaylistSelected(playlist) }
                    )
                }
            }
        }
    }
}

@Composable
fun MoodCard(
    mood: TrackMood,
    tracks: List<Track>,
    onMoodSelected: (TrackMood) -> Unit,
    onTrackSelected: (Track) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = CoolWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mood.name,
                    style = H2,
                    color = FadedBlack
                )
                
                MizuiroButton(
                    text = "Explore",
                    onClick = { onMoodSelected(mood) },
                    secondary = true,
                    fullWidth = false
                )
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tracks.take(5)) { track ->
                    TrackCard(
                        track = track,
                        onClick = { onTrackSelected(track) }
                    )
                }
            }
        }
    }
}

@Composable
fun GenreCard(
    genre: String,
    tracks: List<Track>,
    onGenreSelected: (String) -> Unit,
    onTrackSelected: (Track) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = CoolWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = genre,
                    style = H2,
                    color = FadedBlack
                )
                
                MizuiroButton(
                    text = "Explore",
                    onClick = { onGenreSelected(genre) },
                    secondary = true,
                    fullWidth = false
                )
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tracks.take(5)) { track ->
                    TrackCard(
                        track = track,
                        onClick = { onTrackSelected(track) }
                    )
                }
            }
        }
    }
}

@Composable
fun SimilarArtistsCard(
    similarArtists: List<Artist>,
    onArtistSelected: (Artist) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = CoolWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Similar Artists",
                style = H2,
                color = FadedBlack
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(similarArtists.take(5)) { artist ->
                    ArtistCard(
                        artist = artist,
                        onClick = { onArtistSelected(artist) }
                    )
                }
            }
        }
    }
}

@Composable
fun DiscoveryPlaylistCard(
    playlist: DiscoveryPlaylist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(120.dp),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = CoolWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = playlist.name,
                style = H2,
                color = FadedBlack,
                maxLines = 1
            )
            
            Text(
                text = playlist.description ?: "",
                style = Caption,
                color = SteelBlue,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${playlist.trackCount} tracks",
                    style = Caption,
                    color = SteelBlue.copy(alpha = 0.7f)
                )
                
                if (playlist.isPersonalized) {
                    Text(
                        text = "🤖",
                        style = Caption
                    )
                }
            }
        }
    }
}

@Composable
fun TrackCard(
    track: Track,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(80.dp),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = CoolWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = track.title,
                style = H2,
                color = FadedBlack,
                maxLines = 1
            )
            
            Text(
                text = track.artist,
                style = Caption,
                color = SteelBlue,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDuration(track.duration),
                    style = Caption,
                    color = SteelBlue.copy(alpha = 0.7f)
                )
                
                if (track.isLiked) {
                    Text(
                        text = "❤️",
                        style = Caption
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistCard(
    artist: Artist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(60.dp),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = CoolWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = artist.name,
                style = H2,
                color = FadedBlack,
                maxLines = 1
            )
            
            Text(
                text = "${artist.trackIds.size} tracks",
                style = Caption,
                color = SteelBlue
            )
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = icon,
            style = H1
        )
        
        Text(
            text = message,
            style = Body,
            color = SteelBlue,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatDuration(durationMs: Long): String {
    val minutes = durationMs / 60000
    val seconds = (durationMs % 60000) / 1000
    return String.format("%d:%02d", minutes, seconds)
}

private fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)
    
    return when {
        days > 0 -> "${days}d ago"
        hours > 0 -> "${hours}h ago"
        minutes > 0 -> "${minutes}m ago"
        else -> "Just now"
    }
}
