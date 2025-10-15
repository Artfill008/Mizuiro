package com.mizuiro.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mizuiro.music.data.model.Track
import com.mizuiro.music.data.model.TrackSource
import com.mizuiro.music.ui.components.buttons.MizuiroButton
import com.mizuiro.music.ui.components.buttons.MizuiroIconButton
import com.mizuiro.music.ui.components.decorations.KawaiiMascot
import com.mizuiro.music.ui.components.decorations.MascotState
import com.mizuiro.music.ui.components.decorations.WobblyDivider
import com.mizuiro.music.ui.theme.*

/**
 * Player Screen
 * 
 * The main music player interface with Mizuiro aesthetic.
 * Features custom controls, wobbly decorations, and kawaii elements.
 */
@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    track: Track? = null,
    isPlaying: Boolean = false,
    currentPosition: Long = 0L,
    duration: Long = 0L,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onSeekTo: (Long) -> Unit = {},
    onShuffle: () -> Unit = {},
    onRepeat: () -> Unit = {},
    onLike: () -> Unit = {},
    onQueue: () -> Unit = {}
) {
    // Sample track for demonstration
    val sampleTrack = track ?: Track(
        id = "sample_1",
        title = "Mizuiro Dreams",
        artist = "Aesthetic Vibes",
        album = "Digital Nostalgia",
        duration = 240000L, // 4 minutes
        thumbnailUrl = null,
        audioUrl = null,
        localPath = null,
        source = TrackSource.YOUTUBE_MUSIC,
        youtubeId = "sample123",
        isLiked = false,
        playCount = 0,
        lastPlayed = 0L,
        dateAdded = System.currentTimeMillis(),
        genre = "Ambient",
        year = 2024,
        bitrate = 320,
        fileSize = 1024000L,
        isDownloaded = false,
        isOffline = false,
        mood = TrackMood.DREAMY,
        energy = 0.3f,
        valence = 0.7f,
        danceability = 0.4f,
        tempo = 85f,
        key = 0,
        mode = 1,
        acousticness = 0.8f,
        instrumentalness = 0.9f,
        liveness = 0.1f,
        speechiness = 0.05f,
        popularity = 0.6f,
        explicit = false,
        language = "en",
        country = "US",
        tags = listOf("ambient", "dreamy", "nostalgic"),
        similarTracks = emptyList(),
        audioFeatures = null
    )
    
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with ASCII art
            Text(
                text = "╔════════════════════════════════╗",
                style = VT323Style,
                color = CyberSilver
            )
            Text(
                text = "║        MIZUIRO PLAYER         ║",
                style = VT323Style,
                color = MizuiroBase
            )
            Text(
                text = "╚════════════════════════════════╝",
                style = VT323Style,
                color = CyberSilver
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Kawaii mascot based on playback state
            val mascotState = when {
                isPlaying -> MascotState.Dancing
                track != null -> MascotState.Happy
                else -> MascotState.Idle
            }
            
            KawaiiMascot(
                state = mascotState,
                size = 120.dp
            )
            
            // Track information
            Text(
                text = sampleTrack.title,
                style = H1,
                color = FadedBlack,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = sampleTrack.artist,
                style = H2,
                color = SteelBlue,
                textAlign = TextAlign.Center
            )
            
            if (sampleTrack.album != null) {
                Text(
                    text = sampleTrack.album,
                    style = Body,
                    color = SteelBlue.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
            
            // Wobbly divider
            WobblyDivider()
            
            // Progress bar (custom implementation)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Time display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        style = Caption,
                        color = SteelBlue
                    )
                    Text(
                        text = formatTime(duration),
                        style = Caption,
                        color = SteelBlue
                    )
                }
                
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(IcyGrey)
                ) {
                    val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
                    
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(MizuiroBase, MizuiroLight)
                                )
                            )
                    )
                }
            }
            
            // Control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle button
                MizuiroIconButton(
                    icon = {
                        Text(
                            text = "🔀",
                            style = H2
                        )
                    },
                    onClick = onShuffle,
                    secondary = true
                )
                
                // Previous button
                MizuiroIconButton(
                    icon = {
                        Text(
                            text = "⏮",
                            style = H2
                        )
                    },
                    onClick = onPrevious,
                    secondary = true
                )
                
                // Play/Pause button
                MizuiroButton(
                    text = if (isPlaying) "⏸" else "▶",
                    onClick = onPlayPause,
                    fullWidth = false
                )
                
                // Next button
                MizuiroIconButton(
                    icon = {
                        Text(
                            text = "⏭",
                            style = H2
                        )
                    },
                    onClick = onNext,
                    secondary = true
                )
                
                // Repeat button
                MizuiroIconButton(
                    icon = {
                        Text(
                            text = "🔁",
                            style = H2
                        )
                    },
                    onClick = onRepeat,
                    secondary = true
                )
            }
            
            // Additional controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button
                MizuiroButton(
                    text = if (sampleTrack.isLiked) "❤️" else "🤍",
                    onClick = onLike,
                    secondary = true,
                    fullWidth = false
                )
                
                // Queue button
                MizuiroButton(
                    text = "📋",
                    onClick = onQueue,
                    secondary = true,
                    fullWidth = false
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Track metadata
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Source: ${sampleTrack.source.name}",
                    style = Caption,
                    color = SteelBlue.copy(alpha = 0.7f)
                )
                
                if (sampleTrack.genre != null) {
                    Text(
                        text = "Genre: ${sampleTrack.genre}",
                        style = Caption,
                        color = SteelBlue.copy(alpha = 0.7f)
                    )
                }
                
                if (sampleTrack.year != null) {
                    Text(
                        text = "Year: ${sampleTrack.year}",
                        style = Caption,
                        color = SteelBlue.copy(alpha = 0.7f)
                    )
                }
                
                if (sampleTrack.mood != null) {
                    Text(
                        text = "Mood: ${sampleTrack.mood.name}",
                        style = Caption,
                        color = SteelBlue.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Decorative elements
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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

/**
 * Format time in milliseconds to MM:SS format
 */
private fun formatTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
