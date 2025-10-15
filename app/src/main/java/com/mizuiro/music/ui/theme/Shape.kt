package com.mizuiro.music.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Mizuiro Shape System
 * 
 * Defines the shape system following Mizuiro aesthetic principles.
 * Uses rounded corners with specific radius values for different components.
 */

val MizuiroShapes = Shapes(
    // Small components (buttons, chips)
    small = RoundedCornerShape(12.dp),
    
    // Medium components (cards, dialogs)
    medium = RoundedCornerShape(16.dp),
    
    // Large components (sheets, modals)
    large = RoundedCornerShape(20.dp)
)

// Custom shape definitions
val ButtonShape = RoundedCornerShape(20.dp)           // Primary buttons
val SecondaryButtonShape = RoundedCornerShape(16.dp)  // Secondary buttons
val CardShape = RoundedCornerShape(12.dp)             // Cards
val IconButtonShape = RoundedCornerShape(12.dp)       // Icon buttons
val InputFieldShape = RoundedCornerShape(24.dp)       // Input fields
val DialogShape = RoundedCornerShape(20.dp)           // Dialogs
val BottomSheetShape = RoundedCornerShape(24.dp)      // Bottom sheets
val ChipShape = RoundedCornerShape(16.dp)             // Chips
val BadgeShape = RoundedCornerShape(4.dp)             // Small badges
val ProgressBarShape = RoundedCornerShape(3.dp)       // Progress bars
val ThumbnailShape = RoundedCornerShape(8.dp)         // Image thumbnails
val AlbumArtShape = RoundedCornerShape(12.dp)         // Album art
val NotificationShape = RoundedCornerShape(16.dp)     // Notifications
val WidgetShape = RoundedCornerShape(20.dp)           // Home screen widgets

// Special shapes for aesthetic elements
val WobblyCardShape = RoundedCornerShape(12.dp)       // Cards with wobbly borders
val KawaiiButtonShape = RoundedCornerShape(16.dp)     // Buttons with kawaii elements
val RetroPanelShape = RoundedCornerShape(8.dp)        // Retro ASCII panels
val PixelatedShape = RoundedCornerShape(2.dp)         // Pixelated elements
