package com.mizuiro.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mizuiro.music.data.model.*
import com.mizuiro.music.library.manager.LibraryManager
import com.mizuiro.music.ui.components.buttons.MizuiroButton
import com.mizuiro.music.ui.components.buttons.MizuiroIconButton
import com.mizuiro.music.ui.components.decorations.KawaiiMascot
import com.mizuiro.music.ui.components.decorations.MascotState
import com.mizuiro.music.ui.components.decorations.WobblyDivider
import com.mizuiro.music.ui.theme.*

/**
 * Playlist Creation Screen
 * 
 * Interface for creating and editing playlists with:
 * - Playlist metadata editing
 * - Track selection and organization
 * - Smart playlist rules
 * - Playlist sharing options
 */
@Composable
fun PlaylistCreationScreen(
    modifier: Modifier = Modifier,
    libraryManager: LibraryManager,
    onBack: () -> Unit = {},
    onSave: (Playlist) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var playlistName by remember { mutableStateOf("") }
    var playlistDescription by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }
    var selectedTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var isSmartPlaylist by remember { mutableStateOf(false) }
    var smartRules by remember { mutableStateOf<List<SmartRule>>(emptyList()) }
    
    val allTracks by libraryManager.tracks.collectAsState()
    val availableTags = listOf(
        "Chill", "Energetic", "Sad", "Happy", "Nostalgic",
        "Kawaii", "Nightcore", "Vocaloid", "J-Pop", "Electronic",
        "Ambient", "Rock", "Pop", "Classical", "Jazz"
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
                        text = "║     CREATE PLAYLIST          ║",
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
                        state = MascotState.Working,
                        size = 64.dp
                    )
                    
                    Text(
                        text = "Create Your Perfect Playlist! (◠‿◠)",
                        style = H1,
                        color = FadedBlack,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Organize your music with style",
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
            
            // Playlist type selection
            item {
                PlaylistTypeSelection(
                    isSmartPlaylist = isSmartPlaylist,
                    onTypeChanged = { isSmartPlaylist = it }
                )
            }
            
            // Playlist metadata
            item {
                PlaylistMetadataSection(
                    name = playlistName,
                    onNameChanged = { playlistName = it },
                    description = playlistDescription,
                    onDescriptionChanged = { playlistDescription = it },
                    isPublic = isPublic,
                    onPublicChanged = { isPublic = it }
                )
            }
            
            // Tags selection
            item {
                TagsSelectionSection(
                    selectedTags = selectedTags,
                    availableTags = availableTags,
                    onTagsChanged = { selectedTags = it }
                )
            }
            
            // Smart playlist rules (if enabled)
            if (isSmartPlaylist) {
                item {
                    SmartPlaylistRulesSection(
                        rules = smartRules,
                        onRulesChanged = { smartRules = it }
                    )
                }
            } else {
                // Track selection
                item {
                    TrackSelectionSection(
                        allTracks = allTracks,
                        selectedTracks = selectedTracks,
                        onTracksChanged = { selectedTracks = it }
                    )
                }
            }
            
            // Action buttons
            item {
                ActionButtonsSection(
                    onSave = {
                        val playlist = if (isSmartPlaylist) {
                            // Create smart playlist
                            libraryManager.createSmartPlaylist(
                                name = playlistName,
                                description = playlistDescription,
                                rules = smartRules,
                                isPublic = isPublic
                            )
                        } else {
                            // Create regular playlist
                            libraryManager.createPlaylist(
                                name = playlistName,
                                description = playlistDescription,
                                isPublic = isPublic,
                                tags = selectedTags
                            )
                        }
                        onSave(playlist)
                    },
                    onCancel = onCancel,
                    canSave = playlistName.isNotBlank()
                )
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
fun PlaylistTypeSelection(
    isSmartPlaylist: Boolean,
    onTypeChanged: (Boolean) -> Unit
) {
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
                text = "Playlist Type",
                style = H2,
                color = FadedBlack
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MizuiroButton(
                    text = "Regular Playlist",
                    onClick = { onTypeChanged(false) },
                    secondary = isSmartPlaylist,
                    fullWidth = false
                )
                
                MizuiroButton(
                    text = "Smart Playlist",
                    onClick = { onTypeChanged(true) },
                    secondary = !isSmartPlaylist,
                    fullWidth = false
                )
            }
            
            Text(
                text = if (isSmartPlaylist) {
                    "Smart playlists automatically update based on rules"
                } else {
                    "Regular playlists with manually selected tracks"
                },
                style = Caption,
                color = SteelBlue
            )
        }
    }
}

@Composable
fun PlaylistMetadataSection(
    name: String,
    onNameChanged: (String) -> Unit,
    description: String,
    onDescriptionChanged: (String) -> Unit,
    isPublic: Boolean,
    onPublicChanged: (Boolean) -> Unit
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
            Text(
                text = "Playlist Details",
                style = H2,
                color = FadedBlack
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = onNameChanged,
                label = { Text("Playlist Name", color = SteelBlue) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MizuiroBase,
                    unfocusedBorderColor = SteelBlue.copy(alpha = 0.5f),
                    focusedTextColor = FadedBlack,
                    unfocusedTextColor = FadedBlack
                )
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChanged,
                label = { Text("Description (Optional)", color = SteelBlue) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MizuiroBase,
                    unfocusedBorderColor = SteelBlue.copy(alpha = 0.5f),
                    focusedTextColor = FadedBlack,
                    unfocusedTextColor = FadedBlack
                )
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = isPublic,
                    onCheckedChange = onPublicChanged,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MizuiroBase,
                        uncheckedColor = SteelBlue
                    )
                )
                Text(
                    text = "Make playlist public",
                    style = Body,
                    color = FadedBlack
                )
            }
        }
    }
}

@Composable
fun TagsSelectionSection(
    selectedTags: List<String>,
    availableTags: List<String>,
    onTagsChanged: (List<String>) -> Unit
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
            Text(
                text = "Tags",
                style = H2,
                color = FadedBlack
            )
            
            Text(
                text = "Add tags to help organize your playlist",
                style = Caption,
                color = SteelBlue
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableTags) { tag ->
                    val isSelected = selectedTags.contains(tag)
                    MizuiroButton(
                        text = if (isSelected) "✓ $tag" else tag,
                        onClick = {
                            if (isSelected) {
                                onTagsChanged(selectedTags - tag)
                            } else {
                                onTagsChanged(selectedTags + tag)
                            }
                        },
                        secondary = !isSelected,
                        fullWidth = false
                    )
                }
            }
        }
    }
}

@Composable
fun SmartPlaylistRulesSection(
    rules: List<SmartRule>,
    onRulesChanged: (List<SmartRule>) -> Unit
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
            Text(
                text = "Smart Playlist Rules",
                style = H2,
                color = FadedBlack
            )
            
            Text(
                text = "Define rules to automatically include tracks",
                style = Caption,
                color = SteelBlue
            )
            
            if (rules.isEmpty()) {
                EmptyState(
                    message = "No rules added yet",
                    icon = "📋"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rules.size) { index ->
                        SmartRuleCard(
                            rule = rules[index],
                            onEdit = { /* TODO: Edit rule */ },
                            onDelete = { onRulesChanged(rules - rules[index]) }
                        )
                    }
                }
            }
            
            MizuiroButton(
                text = "Add Rule",
                onClick = { /* TODO: Add new rule */ },
                secondary = true,
                fullWidth = false
            )
        }
    }
}

@Composable
fun TrackSelectionSection(
    allTracks: List<Track>,
    selectedTracks: List<Track>,
    onTracksChanged: (List<Track>) -> Unit
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
            Text(
                text = "Select Tracks",
                style = H2,
                color = FadedBlack
            )
            
            Text(
                text = "Choose tracks for your playlist",
                style = Caption,
                color = SteelBlue
            )
            
            if (allTracks.isEmpty()) {
                EmptyState(
                    message = "No tracks available",
                    icon = "🎵"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allTracks.take(20)) { track ->
                        TrackSelectionCard(
                            track = track,
                            isSelected = selectedTracks.contains(track),
                            onToggle = {
                                if (selectedTracks.contains(track)) {
                                    onTracksChanged(selectedTracks - track)
                                } else {
                                    onTracksChanged(selectedTracks + track)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmartRuleCard(
    rule: SmartRule,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = IcyGrey.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = rule.field,
                    style = H2,
                    color = FadedBlack
                )
                
                Text(
                    text = "${rule.operator} ${rule.value}",
                    style = Caption,
                    color = SteelBlue
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MizuiroIconButton(
                    icon = "✏️",
                    onClick = onEdit,
                    size = 32.dp
                )
                
                MizuiroIconButton(
                    icon = "🗑️",
                    onClick = onDelete,
                    size = 32.dp
                )
            }
        }
    }
}

@Composable
fun TrackSelectionCard(
    track: Track,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MizuiroBase.copy(alpha = 0.3f) else CoolWhite
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = track.title,
                    style = H2,
                    color = FadedBlack
                )
                
                Text(
                    text = track.artist,
                    style = Caption,
                    color = SteelBlue
                )
            }
            
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MizuiroBase,
                    uncheckedColor = SteelBlue
                )
            )
        }
    }
}

@Composable
fun ActionButtonsSection(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    canSave: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = IcyGrey.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MizuiroButton(
                text = "Cancel",
                onClick = onCancel,
                secondary = true,
                fullWidth = false
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            MizuiroButton(
                text = "Save Playlist",
                onClick = onSave,
                fullWidth = false,
                enabled = canSave
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
