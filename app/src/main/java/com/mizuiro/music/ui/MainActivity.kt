package com.mizuiro.music.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mizuiro.music.audio.effects.AudioEffect
import com.mizuiro.music.audio.effects.EffectChain
import com.mizuiro.music.audio.effects.EffectPreset
import com.mizuiro.music.data.model.*
import com.mizuiro.music.discovery.engine.DiscoveryEngine
import com.mizuiro.music.discovery.engine.DiscoveryPlaylist
import com.mizuiro.music.library.manager.LibraryManager
import com.mizuiro.music.ui.components.buttons.MizuiroButton
import com.mizuiro.music.ui.components.decorations.KawaiiMascot
import com.mizuiro.music.ui.components.decorations.MascotState
import com.mizuiro.music.ui.components.decorations.WobblyDivider
import com.mizuiro.music.ui.screens.AudioEffectsLabScreen
import com.mizuiro.music.ui.screens.DiscoveryScreen
import com.mizuiro.music.ui.screens.LibraryScreen
import com.mizuiro.music.ui.screens.PlaylistCreationScreen
import com.mizuiro.music.ui.screens.PlayerScreen
import com.mizuiro.music.ui.theme.Body
import com.mizuiro.music.ui.theme.CoolWhite
import com.mizuiro.music.ui.theme.CyberSilver
import com.mizuiro.music.ui.theme.FadedBlack
import com.mizuiro.music.ui.theme.H1
import com.mizuiro.music.ui.theme.H2
import com.mizuiro.music.ui.theme.MizuiroBase
import com.mizuiro.music.ui.theme.MizuiroTheme
import com.mizuiro.music.ui.theme.SteelBlue
import com.mizuiro.music.ui.theme.VT323Style
import com.mizuiro.music.ui.theme.Caption
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main Activity for Mizuiro Music
 * 
 * The entry point of the application that sets up the Mizuiro theme
 * and hosts the main UI components.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var libraryManager: LibraryManager
    
    @Inject
    lateinit var discoveryEngine: DiscoveryEngine
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            MizuiroTheme {
                // Main app content
                MizuiroMusicApp(
                    libraryManager = libraryManager,
                    discoveryEngine = discoveryEngine
                )
            }
        }
    }
}

@Composable
fun MizuiroMusicApp(
    libraryManager: LibraryManager,
    discoveryEngine: DiscoveryEngine
) {
    // This will be implemented with the main app structure
    // For now, showing a placeholder with the Mizuiro aesthetic
    
    var currentScreen by remember { mutableStateOf("home") }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(240000L) } // 4 minutes
    
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        // Main content will go here
        // This is where we'll add:
        // - Navigation
        // - Home screen
        // - Player screen
        // - Library screen
        // - Search screen
        // - Profile screen
        
        when (currentScreen) {
            "home" -> {
                PlaceholderScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onShowPlayer = { currentScreen = "player" },
                    onShowEffectsLab = { currentScreen = "effects" },
                    onShowLibrary = { currentScreen = "library" },
                    onShowDiscovery = { currentScreen = "discovery" }
                )
            }
            "player" -> {
                PlayerScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    duration = duration,
                    onPlayPause = { isPlaying = !isPlaying },
                    onNext = { /* TODO: Implement next track */ },
                    onPrevious = { /* TODO: Implement previous track */ },
                    onSeekTo = { position -> currentPosition = position },
                    onShuffle = { /* TODO: Implement shuffle */ },
                    onRepeat = { /* TODO: Implement repeat */ },
                    onLike = { /* TODO: Implement like */ },
                    onQueue = { /* TODO: Implement queue */ }
                )
            }
            "effects" -> {
                AudioEffectsLabScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onBack = { currentScreen = "home" },
                    onEffectSelected = { effect -> 
                        // TODO: Handle effect selection
                    },
                    onPresetSelected = { preset -> 
                        // TODO: Handle preset selection
                    },
                    onChainSelected = { chain -> 
                        // TODO: Handle chain selection
                    }
                )
            }
            "library" -> {
                LibraryScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    libraryManager = libraryManager,
                    onTrackSelected = { track -> 
                        // TODO: Handle track selection
                    },
                    onPlaylistSelected = { playlist -> 
                        // TODO: Handle playlist selection
                    },
                    onArtistSelected = { artist -> 
                        // TODO: Handle artist selection
                    },
                    onAlbumSelected = { album -> 
                        // TODO: Handle album selection
                    },
                    onCreatePlaylist = { currentScreen = "create_playlist" },
                    onSearch = { /* TODO: Implement search */ }
                )
            }
            "discovery" -> {
                DiscoveryScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    discoveryEngine = discoveryEngine,
                    onTrackSelected = { track -> 
                        // TODO: Handle track selection
                    },
                    onArtistSelected = { artist -> 
                        // TODO: Handle artist selection
                    },
                    onPlaylistSelected = { playlist -> 
                        // TODO: Handle playlist selection
                    },
                    onMoodSelected = { mood -> 
                        // TODO: Handle mood selection
                    },
                    onGenreSelected = { genre -> 
                        // TODO: Handle genre selection
                    },
                    onBack = { currentScreen = "home" }
                )
            }
            "create_playlist" -> {
                PlaylistCreationScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    libraryManager = libraryManager,
                    onBack = { currentScreen = "library" },
                    onSave = { playlist -> 
                        // TODO: Handle playlist save
                        currentScreen = "library"
                    },
                    onCancel = { currentScreen = "library" }
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(
    modifier: Modifier = Modifier,
    onShowPlayer: () -> Unit = {},
    onShowEffectsLab: () -> Unit = {},
    onShowLibrary: () -> Unit = {},
    onShowDiscovery: () -> Unit = {}
) {
    // Temporary placeholder screen
    // This will be replaced with the actual app structure
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CoolWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Kawaii mascot
            KawaiiMascot(
                state = MascotState.Happy,
                size = 96.dp
            )
            
            // Welcome message
            Text(
                text = "╔════════════════════════════════╗",
                style = VT323Style,
                color = CyberSilver
            )
            Text(
                text = "║   MIZUIRO MUSIC (水色)         ║",
                style = VT323Style,
                color = MizuiroBase
            )
            Text(
                text = "╚════════════════════════════════╝",
                style = VT323Style,
                color = CyberSilver
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Welcome to Mizuiro Music! (◠‿◠)",
                style = H1,
                color = FadedBlack,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Where Music Meets Aesthetic",
                style = Body,
                color = SteelBlue,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            WobblyDivider()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Phase 5 Complete!",
                style = H2,
                color = FadedBlack
            )
            
            Text(
                text = "Discovery Engine & AI Recommendations ready",
                style = Caption,
                color = SteelBlue,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Navigation buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MizuiroButton(
                    text = "🎵 Player",
                    onClick = onShowPlayer,
                    fullWidth = false
                )
                
                MizuiroButton(
                    text = "🎛️ Effects Lab",
                    onClick = onShowEffectsLab,
                    secondary = true,
                    fullWidth = false
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MizuiroButton(
                    text = "📚 Library",
                    onClick = onShowLibrary,
                    secondary = true,
                    fullWidth = false
                )
                
                MizuiroButton(
                    text = "🔍 Discovery",
                    onClick = onShowDiscovery,
                    secondary = true,
                    fullWidth = false
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Decorative elements
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(3) {
                    KawaiiMascot(
                        state = MascotState.Dancing,
                        size = 32.dp
                    )
                }
            }
        }
    }
}
