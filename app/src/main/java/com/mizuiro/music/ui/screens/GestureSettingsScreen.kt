package com.mizuiro.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mizuiro.music.gestures.*
import com.mizuiro.music.ui.components.*
import com.mizuiro.music.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Gesture Settings Screen
 * 
 * Beautiful gesture control settings with Mizuiro aesthetic
 */
@Composable
fun GestureSettingsScreen(
    modifier: Modifier = Modifier,
    gestureController: GestureController,
    onBack: () -> Unit = {}
) {
    val gestureSettings by gestureController.gestureSettings.collectAsState()
    val gestureState by gestureController.gestureState.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var showGestureDemo by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MizuiroBase)
            .padding(16.dp)
    ) {
        // Header with ASCII art
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "╔══════════════════════════════════════╗",
                style = H3,
                color = SteelBlue,
                fontFamily = VT323
            )
            Text(
                text = "║         GESTURE CONTROLS             ║",
                style = H3,
                color = SteelBlue,
                fontFamily = VT323
            )
            Text(
                text = "╚══════════════════════════════════════╝",
                style = H3,
                color = SteelBlue,
                fontFamily = VT323
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Control with Gestures (◠‿◠)",
                style = H2,
                color = FadedBlack,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Swipe, tap, and gesture your way through music",
                style = Caption,
                color = SteelBlue,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Kawaii Mascot
        KawaiiMascot(
            state = if (gestureSettings.enabled) "gesturing" else "idle",
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Gesture Statistics Card
        GestureStatsCard(
            gestureState = gestureState,
            onDemo = { showGestureDemo = true }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Navigation
        GestureTabNavigation(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Content
        when (selectedTab) {
            0 -> BasicGesturesSection(
                gestureController = gestureController,
                gestureSettings = gestureSettings
            )
            1 -> AdvancedGesturesSection(
                gestureController = gestureController,
                gestureSettings = gestureSettings
            )
            2 -> GestureCustomizationSection(
                gestureController = gestureController,
                gestureSettings = gestureSettings
            )
        }
    }
    
    // Gesture Demo Dialog
    if (showGestureDemo) {
        GestureDemoDialog(
            gestureController = gestureController,
            onDismiss = { showGestureDemo = false }
        )
    }
}

@Composable
private fun GestureStatsCard(
    gestureState: GestureState,
    onDemo: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CoolWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Gesture Statistics",
                style = H3,
                color = FadedBlack,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Gestures Used",
                    value = "${gestureState.gestureCount}",
                    color = MizuiroBase
                )
                
                StatItem(
                    label = "Status",
                    value = if (gestureState.isEnabled) "Enabled" else "Disabled",
                    color = if (gestureState.isEnabled) BlushPink else SteelBlue
                )
                
                StatItem(
                    label = "Last Gesture",
                    value = if (gestureState.lastGesture.isNotEmpty()) "✓" else "—",
                    color = if (gestureState.lastGesture.isNotEmpty()) BlushPink else IcyGrey
                )
            }
            
            if (gestureState.lastGesture.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = gestureState.lastGesture,
                    style = Caption,
                    color = SteelBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MizuiroButton(
                text = "🎭 Try Gestures",
                onClick = onDemo,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = H2,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = Caption,
            color = FadedBlack
        )
    }
}

@Composable
private fun GestureTabNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Basic", "Advanced", "Customize")
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, tab ->
            MizuiroButton(
                text = tab,
                onClick = { onTabSelected(index) },
                secondary = selectedTab != index,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BasicGesturesSection(
    gestureController: GestureController,
    gestureSettings: GestureSettings
) {
    val scope = rememberCoroutineScope()
    
    Column {
        Text(
            text = "Basic Gestures",
            style = H3,
            color = FadedBlack,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Simple gestures for everyday music control",
            style = Caption,
            color = SteelBlue
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Enable/Disable Toggle
        GestureToggleCard(
            title = "Enable Gestures",
            description = "Turn gesture controls on or off",
            isEnabled = gestureSettings.enabled,
            onToggle = { enabled ->
                scope.launch {
                    gestureController.setGesturesEnabled(enabled)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Basic Gesture Actions
        val basicGestures = listOf(
            GestureAction.SWIPE_UP to "Volume Up",
            GestureAction.SWIPE_DOWN to "Volume Down",
            GestureAction.SWIPE_LEFT to "Previous Track",
            GestureAction.SWIPE_RIGHT to "Next Track",
            GestureAction.DOUBLE_TAP to "Play/Pause",
            GestureAction.LONG_PRESS to "Show Queue"
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(basicGestures) { (action, description) ->
                GestureActionCard(
                    action = action,
                    description = description,
                    currentAction = getCurrentAction(gestureSettings, action),
                    onActionSelected = { newAction ->
                        scope.launch {
                            updateGestureAction(gestureController, action, newAction)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdvancedGesturesSection(
    gestureController: GestureController,
    gestureSettings: GestureSettings
) {
    val scope = rememberCoroutineScope()
    
    Column {
        Text(
            text = "Advanced Gestures",
            style = H3,
            color = FadedBlack,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Two-finger gestures and advanced controls",
            style = Caption,
            color = SteelBlue
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Sensitivity Setting
        GestureSensitivityCard(
            currentSensitivity = gestureSettings.sensitivity,
            onSensitivityChanged = { sensitivity ->
                scope.launch {
                    val newSettings = gestureSettings.copy(sensitivity = sensitivity)
                    gestureController.updateSettings(newSettings)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Advanced Gesture Actions
        val advancedGestures = listOf(
            GestureAction.TWO_FINGER_SWIPE_UP to "Show Library",
            GestureAction.TWO_FINGER_SWIPE_DOWN to "Show Discovery",
            GestureAction.TWO_FINGER_SWIPE_LEFT to "Show Effects",
            GestureAction.TWO_FINGER_SWIPE_RIGHT to "Show Downloads",
            GestureAction.PINCH_ZOOM to "Zoom Artwork",
            GestureAction.ROTATE to "Rotate Artwork"
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(advancedGestures) { (action, description) ->
                GestureActionCard(
                    action = action,
                    description = description,
                    currentAction = getCurrentAction(gestureSettings, action),
                    onActionSelected = { newAction ->
                        scope.launch {
                            updateGestureAction(gestureController, action, newAction)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun GestureCustomizationSection(
    gestureController: GestureController,
    gestureSettings: GestureSettings
) {
    val scope = rememberCoroutineScope()
    
    Column {
        Text(
            text = "Customization",
            style = H3,
            color = FadedBlack,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Customize feedback and gesture behavior",
            style = Caption,
            color = SteelBlue
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Feedback Settings
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                GestureToggleCard(
                    title = "Haptic Feedback",
                    description = "Vibrate when gestures are detected",
                    isEnabled = gestureSettings.hapticFeedback,
                    onToggle = { enabled ->
                        scope.launch {
                            val newSettings = gestureSettings.copy(hapticFeedback = enabled)
                            gestureController.updateSettings(newSettings)
                        }
                    }
                )
            }
            
            item {
                GestureToggleCard(
                    title = "Sound Feedback",
                    description = "Play sound when gestures are detected",
                    isEnabled = gestureSettings.soundFeedback,
                    onToggle = { enabled ->
                        scope.launch {
                            val newSettings = gestureSettings.copy(soundFeedback = enabled)
                            gestureController.updateSettings(newSettings)
                        }
                    }
                )
            }
            
            item {
                GestureToggleCard(
                    title = "Visual Feedback",
                    description = "Show visual feedback for gestures",
                    isEnabled = gestureSettings.visualFeedback,
                    onToggle = { enabled ->
                        scope.launch {
                            val newSettings = gestureSettings.copy(visualFeedback = enabled)
                            gestureController.updateSettings(newSettings)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun GestureToggleCard(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CoolWhite),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = H4,
                    color = FadedBlack,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = Caption,
                    color = SteelBlue
                )
            }
            
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MizuiroBase,
                    checkedTrackColor = BlushPink,
                    uncheckedThumbColor = IcyGrey,
                    uncheckedTrackColor = SteelBlue
                )
            )
        }
    }
}

@Composable
private fun GestureSensitivityCard(
    currentSensitivity: GestureSensitivity,
    onSensitivityChanged: (GestureSensitivity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CoolWhite),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Gesture Sensitivity",
                style = H4,
                color = FadedBlack,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Adjust how sensitive gesture detection is",
                style = Caption,
                color = SteelBlue
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GestureSensitivity.values().forEach { sensitivity ->
                    MizuiroButton(
                        text = sensitivity.name,
                        onClick = { onSensitivityChanged(sensitivity) },
                        secondary = currentSensitivity != sensitivity,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GestureActionCard(
    action: GestureAction,
    description: String,
    currentAction: GestureAction,
    onActionSelected: (GestureAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CoolWhite),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${gestureController.getActionIcon(action)} $description",
                    style = H4,
                    color = FadedBlack,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current: ${getActionDescription(currentAction)}",
                    style = Caption,
                    color = SteelBlue
                )
            }
            
            // Action selection would go here
            // For now, just show the current action
            Text(
                text = getActionIcon(currentAction),
                style = H3
            )
        }
    }
}

@Composable
private fun GestureDemoDialog(
    gestureController: GestureController,
    onDismiss: () -> Unit
) {
    // Gesture demo dialog implementation
    // This would show a demo of available gestures
}

// Helper functions
private fun getCurrentAction(settings: GestureSettings, action: GestureAction): GestureAction {
    return when (action) {
        GestureAction.SWIPE_UP -> settings.swipeUpAction
        GestureAction.SWIPE_DOWN -> settings.swipeDownAction
        GestureAction.SWIPE_LEFT -> settings.swipeLeftAction
        GestureAction.SWIPE_RIGHT -> settings.swipeRightAction
        GestureAction.DOUBLE_TAP -> settings.doubleTapAction
        GestureAction.LONG_PRESS -> settings.longPressAction
        GestureAction.TWO_FINGER_SWIPE_UP -> settings.twoFingerSwipeUpAction
        GestureAction.TWO_FINGER_SWIPE_DOWN -> settings.twoFingerSwipeDownAction
        GestureAction.TWO_FINGER_SWIPE_LEFT -> settings.twoFingerSwipeLeftAction
        GestureAction.TWO_FINGER_SWIPE_RIGHT -> settings.twoFingerSwipeRightAction
        GestureAction.PINCH_ZOOM -> settings.pinchZoomAction
        GestureAction.ROTATE -> settings.rotateAction
        else -> GestureAction.NONE
    }
}

private suspend fun updateGestureAction(
    gestureController: GestureController,
    gesture: GestureAction,
    newAction: GestureAction
) {
    val currentSettings = gestureController.gestureSettings.value
    val updatedSettings = when (gesture) {
        GestureAction.SWIPE_UP -> currentSettings.copy(swipeUpAction = newAction)
        GestureAction.SWIPE_DOWN -> currentSettings.copy(swipeDownAction = newAction)
        GestureAction.SWIPE_LEFT -> currentSettings.copy(swipeLeftAction = newAction)
        GestureAction.SWIPE_RIGHT -> currentSettings.copy(swipeRightAction = newAction)
        GestureAction.DOUBLE_TAP -> currentSettings.copy(doubleTapAction = newAction)
        GestureAction.LONG_PRESS -> currentSettings.copy(longPressAction = newAction)
        GestureAction.TWO_FINGER_SWIPE_UP -> currentSettings.copy(twoFingerSwipeUpAction = newAction)
        GestureAction.TWO_FINGER_SWIPE_DOWN -> currentSettings.copy(twoFingerSwipeDownAction = newAction)
        GestureAction.TWO_FINGER_SWIPE_LEFT -> currentSettings.copy(twoFingerSwipeLeftAction = newAction)
        GestureAction.TWO_FINGER_SWIPE_RIGHT -> currentSettings.copy(twoFingerSwipeRightAction = newAction)
        GestureAction.PINCH_ZOOM -> currentSettings.copy(pinchZoomAction = newAction)
        GestureAction.ROTATE -> currentSettings.copy(rotateAction = newAction)
        else -> currentSettings
    }
    gestureController.updateSettings(updatedSettings)
}

private fun getActionDescription(action: GestureAction): String {
    return when (action) {
        GestureAction.PLAY_PAUSE -> "Play/Pause"
        GestureAction.NEXT_TRACK -> "Next Track"
        GestureAction.PREVIOUS_TRACK -> "Previous Track"
        GestureAction.VOLUME_UP -> "Volume Up"
        GestureAction.VOLUME_DOWN -> "Volume Down"
        GestureAction.SHOW_QUEUE -> "Show Queue"
        GestureAction.SHOW_LIBRARY -> "Show Library"
        GestureAction.SHOW_DISCOVERY -> "Show Discovery"
        GestureAction.SHOW_EFFECTS -> "Show Effects"
        GestureAction.SHOW_DOWNLOADS -> "Show Downloads"
        GestureAction.ZOOM_ARTWORK -> "Zoom Artwork"
        GestureAction.ROTATE_ARTWORK -> "Rotate Artwork"
        GestureAction.LIKE_TRACK -> "Like Track"
        GestureAction.SHUFFLE_TOGGLE -> "Toggle Shuffle"
        GestureAction.REPEAT_TOGGLE -> "Toggle Repeat"
        GestureAction.SHOW_SETTINGS -> "Show Settings"
        GestureAction.GO_BACK -> "Go Back"
        else -> "None"
    }
}

private fun getActionIcon(action: GestureAction): String {
    return when (action) {
        GestureAction.PLAY_PAUSE -> "⏯️"
        GestureAction.NEXT_TRACK -> "⏭️"
        GestureAction.PREVIOUS_TRACK -> "⏮️"
        GestureAction.VOLUME_UP -> "🔊"
        GestureAction.VOLUME_DOWN -> "🔉"
        GestureAction.SHOW_QUEUE -> "📋"
        GestureAction.SHOW_LIBRARY -> "📚"
        GestureAction.SHOW_DISCOVERY -> "🔍"
        GestureAction.SHOW_EFFECTS -> "🎛️"
        GestureAction.SHOW_DOWNLOADS -> "📥"
        GestureAction.ZOOM_ARTWORK -> "🔍"
        GestureAction.ROTATE_ARTWORK -> "🔄"
        GestureAction.LIKE_TRACK -> "❤️"
        GestureAction.SHUFFLE_TOGGLE -> "🔀"
        GestureAction.REPEAT_TOGGLE -> "🔁"
        GestureAction.SHOW_SETTINGS -> "⚙️"
        GestureAction.GO_BACK -> "⬅️"
        else -> "—"
    }
}
