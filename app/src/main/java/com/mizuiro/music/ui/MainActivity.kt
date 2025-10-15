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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mizuiro.music.ui.components.decorations.KawaiiMascot
import com.mizuiro.music.ui.components.decorations.MascotState
import com.mizuiro.music.ui.components.decorations.WobblyDivider
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

/**
 * Main Activity for Mizuiro Music
 * 
 * The entry point of the application that sets up the Mizuiro theme
 * and hosts the main UI components.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            MizuiroTheme {
                // Main app content
                MizuiroMusicApp()
            }
        }
    }
}

@Composable
fun MizuiroMusicApp() {
    // This will be implemented with the main app structure
    // For now, showing a placeholder with the Mizuiro aesthetic
    
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
        
        PlaceholderScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
fun PlaceholderScreen(
    modifier: Modifier = Modifier
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
                text = "Coming Soon...",
                style = H2,
                color = FadedBlack
            )
            
            Text(
                text = "This is where the magic happens",
                style = Caption,
                color = SteelBlue,
                textAlign = TextAlign.Center
            )
            
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
