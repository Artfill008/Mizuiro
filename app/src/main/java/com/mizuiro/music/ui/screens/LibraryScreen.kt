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
import com.mizuiro.music.library.manager.LibraryManager
import com.mizuiro.music.library.manager.LibraryStats
import com.mizuiro.music.ui.components.buttons.MizuiroButton
import com.mizuiro.music.ui.components.buttons.MizuiroIconButton
import com.mizuiro.music.ui.components.decorations.KawaiiMascot
import com.mizuiro.music.ui.components.decorations.MascotState
import com.mizuiro.music.ui.components.decorations.WobblyDivider
import com.mizuiro.music.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Library Screen
 * 
 * Main library interface showing:
 * - Recently played tracks
 * - Frequently played tracks
 * - Liked tracks
 * - Playlists
 * - Artists and albums
 * - Library statistics
 */
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    libraryManager: LibraryManager,
    onTrackSelected: (Track) -> Unit = {},
    onPlaylistSelected: (Playlist) -> Unit = {},
    onArtistSelected: (Artist) -> Unit = {},
    onAlbumSelected: (Album) -> Unit = {},
    onCreatePlaylist: () -> Unit = {},
    onSearch: () -> Unit = {}
) {
    val libraryStats by libraryManager.libraryStats.collectAsState()
    val recentlyPlayed by libraryManager.recentlyPlayed.collectAsState()
    val frequentlyPlayed by libraryManager.frequentlyPlayed.collectAsState()
    val likedTracks by libraryManager.likedTracks.collectAsState()
    val playlists by libraryManager.playlists.collectAsState()
    val artists by libraryManager.artists.collectAsState()
    val albums by libraryManager.albums.collectAsState()
    
    var selectedTab by remember { mutableStateOf("recent") }
    
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
                        text = "║        MY LIBRARY            ║",
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
                        state = MascotState.Happy,
                        size = 64.dp
                    )
                    
                    Text(
                        text = "Your Music Collection (◠‿◠)",
                        style = H1,
                        color = FadedBlack,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Organize and discover your favorite tracks",
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
            
            // Library statistics
            item {
                LibraryStatsCard(stats = libraryStats)
            }
            
            // Tab navigation
            item {
                LibraryTabNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
            
            // Content based on selected tab
            when (selectedTab) {
                "recent" -> {
                    item {
                        RecentlyPlayedSection(
                            tracks = recentlyPlayed,
                            onTrackSelected = onTrackSelected
                        )
                    }
                }
                "frequent" -> {
                    item {
                        FrequentlyPlayedSection(
                            tracks = frequentlyPlayed,
                            onTrackSelected = onTrackSelected
                        )
                    }
                }
                "liked" -> {
                    item {
                        LikedTracksSection(
                            tracks = likedTracks,
                            onTrackSelected = onTrackSelected
                        )
                    }
                }
                "playlists" -> {
                    item {
                        PlaylistsSection(
                            playlists = playlists,
                            onPlaylistSelected = onPlaylistSelected,
                            onCreatePlaylist = onCreatePlaylist
                        )
                    }
                }
                "artists" -> {
                    item {
                        ArtistsSection(
                            artists = artists,
                            onArtistSelected = onArtistSelected
                        )
                    }
                }
                "albums" -> {
                    item {
                        AlbumsSection(
                            albums = albums,
                            onAlbumSelected = onAlbumSelected
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
fun LibraryStatsCard(stats: LibraryStats) {
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
                text = "Library Statistics",
                style = H2,
                color = FadedBlack
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Tracks",
                    value = stats.totalTracks.toString(),
                    color = MizuiroBase
                )
                StatItem(
                    label = "Artists",
                    value = stats.totalArtists.toString(),
                    color = SteelBlue
                )
                StatItem(
                    label = "Albums",
                    value = stats.totalAlbums.toString(),
                    color = BlushPink
                )
                StatItem(
                    label = "Playlists",
                    value = stats.totalPlaylists.toString(),
                    color = CyberSilver
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Liked",
                    value = stats.likedTracks.toString(),
                    color = MizuiroBase
                )
                StatItem(
                    label = "Recent",
                    value = stats.recentlyPlayed.toString(),
                    color = SteelBlue
                )
                StatItem(
                    label = "Duration",
                    value = formatDuration(stats.totalDuration),
                    color = BlushPink
                )
                StatItem(
                    label = "Size",
                    value = formatSize(stats.totalSize),
                    color = CyberSilver
                )
            }
        }
    }
}

@Composable
fun StatItem(
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
fun LibraryTabNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf(
        "recent" to "Recent",
        "frequent" to "Frequent",
        "liked" to "Liked",
        "playlists" to "Playlists",
        "artists" to "Artists",
        "albums" to "Albums"
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
fun RecentlyPlayedSection(
    tracks: List<Track>,
    onTrackSelected: (Track) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Recently Played",
            style = H2,
            color = FadedBlack
        )
        
        if (tracks.isEmpty()) {
            EmptyState(
                message = "No recently played tracks",
                icon = "🎵"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tracks.take(10)) { track ->
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
fun FrequentlyPlayedSection(
    tracks: List<Track>,
    onTrackSelected: (Track) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Frequently Played",
            style = H2,
            color = FadedBlack
        )
        
        if (tracks.isEmpty()) {
            EmptyState(
                message = "No frequently played tracks",
                icon = "🔥"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tracks.take(10)) { track ->
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
fun LikedTracksSection(
    tracks: List<Track>,
    onTrackSelected: (Track) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Liked Tracks",
            style = H2,
            color = FadedBlack
        )
        
        if (tracks.isEmpty()) {
            EmptyState(
                message = "No liked tracks yet",
                icon = "❤️"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tracks.take(10)) { track ->
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
fun PlaylistsSection(
    playlists: List<Playlist>,
    onPlaylistSelected: (Playlist) -> Unit,
    onCreatePlaylist: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Playlists",
                style = H2,
                color = FadedBlack
            )
            
            MizuiroButton(
                text = "Create",
                onClick = onCreatePlaylist,
                secondary = true,
                fullWidth = false
            )
        }
        
        if (playlists.isEmpty()) {
            EmptyState(
                message = "No playlists yet",
                icon = "📝"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(playlists) { playlist ->
                    PlaylistCard(
                        playlist = playlist,
                        onClick = { onPlaylistSelected(playlist) }
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistsSection(
    artists: List<Artist>,
    onArtistSelected: (Artist) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Artists",
            style = H2,
            color = FadedBlack
        )
        
        if (artists.isEmpty()) {
            EmptyState(
                message = "No artists in library",
                icon = "🎤"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(artists.take(10)) { artist ->
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
fun AlbumsSection(
    albums: List<Album>,
    onAlbumSelected: (Album) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Albums",
            style = H2,
            color = FadedBlack
        )
        
        if (albums.isEmpty()) {
            EmptyState(
                message = "No albums in library",
                icon = "💿"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(albums.take(10)) { album ->
                    AlbumCard(
                        album = album,
                        onClick = { onAlbumSelected(album) }
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
fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(100.dp),
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
                
                if (playlist.isPublic) {
                    Text(
                        text = "🌐",
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
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (artist.isLiked) {
                Text(
                    text = "❤️",
                    style = Caption
                )
            }
        }
    }
}

@Composable
fun AlbumCard(
    album: Album,
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
                text = album.title,
                style = H2,
                color = FadedBlack,
                maxLines = 1
            )
            
            Text(
                text = album.artist,
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
                    text = "${album.trackCount} tracks",
                    style = Caption,
                    color = SteelBlue.copy(alpha = 0.7f)
                )
                
                if (album.isLiked) {
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

private fun formatSize(sizeBytes: Long): String {
    return when {
        sizeBytes >= 1024 * 1024 * 1024 -> "${sizeBytes / (1024 * 1024 * 1024)}GB"
        sizeBytes >= 1024 * 1024 -> "${sizeBytes / (1024 * 1024)}MB"
        sizeBytes >= 1024 -> "${sizeBytes / 1024}KB"
        else -> "${sizeBytes}B"
    }
}
