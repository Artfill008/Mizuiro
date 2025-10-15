package com.mizuiro.music.gestures

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gesture Controller
 * 
 * Handles gesture recognition and control for the Mizuiro Music interface
 * with beautiful and intuitive gesture interactions
 */
@Singleton
class GestureController @Inject constructor() {
    
    private val _gestureState = MutableStateFlow(GestureState())
    val gestureState: StateFlow<GestureState> = _gestureState.asStateFlow()
    
    private val _gestureSettings = MutableStateFlow(GestureSettings())
    val gestureSettings: StateFlow<GestureSettings> = _gestureSettings.asStateFlow()
    
    private var gestureDetector: GestureDetector? = null
    private var gestureCallbacks: GestureCallbacks? = null
    
    /**
     * Initialize gesture controller
     */
    fun initialize(context: Context, callbacks: GestureCallbacks) {
        this.gestureCallbacks = callbacks
        this.gestureDetector = GestureDetector(context, MizuiroGestureListener())
    }
    
    /**
     * Handle touch events
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector?.onTouchEvent(event) ?: false
    }
    
    /**
     * Update gesture settings
     */
    fun updateSettings(settings: GestureSettings) {
        _gestureSettings.value = settings
    }
    
    /**
     * Enable/disable gesture controls
     */
    fun setGesturesEnabled(enabled: Boolean) {
        val currentSettings = _gestureSettings.value
        _gestureSettings.value = currentSettings.copy(enabled = enabled)
    }
    
    /**
     * Get available gesture actions
     */
    fun getAvailableActions(): List<GestureAction> {
        return listOf(
            GestureAction.SWIPE_UP,
            GestureAction.SWIPE_DOWN,
            GestureAction.SWIPE_LEFT,
            GestureAction.SWIPE_RIGHT,
            GestureAction.DOUBLE_TAP,
            GestureAction.LONG_PRESS,
            GestureAction.PINCH_ZOOM,
            GestureAction.ROTATE,
            GestureAction.TWO_FINGER_SWIPE_UP,
            GestureAction.TWO_FINGER_SWIPE_DOWN,
            GestureAction.TWO_FINGER_SWIPE_LEFT,
            GestureAction.TWO_FINGER_SWIPE_RIGHT
        )
    }
    
    /**
     * Get gesture action description
     */
    fun getActionDescription(action: GestureAction): String {
        return when (action) {
            GestureAction.SWIPE_UP -> "Swipe Up"
            GestureAction.SWIPE_DOWN -> "Swipe Down"
            GestureAction.SWIPE_LEFT -> "Swipe Left"
            GestureAction.SWIPE_RIGHT -> "Swipe Right"
            GestureAction.DOUBLE_TAP -> "Double Tap"
            GestureAction.LONG_PRESS -> "Long Press"
            GestureAction.PINCH_ZOOM -> "Pinch Zoom"
            GestureAction.ROTATE -> "Rotate"
            GestureAction.TWO_FINGER_SWIPE_UP -> "Two Finger Swipe Up"
            GestureAction.TWO_FINGER_SWIPE_DOWN -> "Two Finger Swipe Down"
            GestureAction.TWO_FINGER_SWIPE_LEFT -> "Two Finger Swipe Left"
            GestureAction.TWO_FINGER_SWIPE_RIGHT -> "Two Finger Swipe Right"
        }
    }
    
    /**
     * Get gesture action icon
     */
    fun getActionIcon(action: GestureAction): String {
        return when (action) {
            GestureAction.SWIPE_UP -> "⬆️"
            GestureAction.SWIPE_DOWN -> "⬇️"
            GestureAction.SWIPE_LEFT -> "⬅️"
            GestureAction.SWIPE_RIGHT -> "➡️"
            GestureAction.DOUBLE_TAP -> "👆👆"
            GestureAction.LONG_PRESS -> "👆⏱️"
            GestureAction.PINCH_ZOOM -> "🤏"
            GestureAction.ROTATE -> "🔄"
            GestureAction.TWO_FINGER_SWIPE_UP -> "👆👆⬆️"
            GestureAction.TWO_FINGER_SWIPE_DOWN -> "👆👆⬇️"
            GestureAction.TWO_FINGER_SWIPE_LEFT -> "👆👆⬅️"
            GestureAction.TWO_FINGER_SWIPE_RIGHT -> "👆👆➡️"
        }
    }
    
    /**
     * Mizuiro Gesture Listener
     */
    private inner class MizuiroGestureListener : GestureDetector.SimpleOnGestureListener() {
        
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (!_gestureSettings.value.enabled) return false
            
            val settings = _gestureSettings.value
            if (settings.singleTapAction != GestureAction.NONE) {
                gestureCallbacks?.onGestureAction(settings.singleTapAction)
                updateGestureState("Single tap detected")
            }
            return true
        }
        
        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (!_gestureSettings.value.enabled) return false
            
            val settings = _gestureSettings.value
            if (settings.doubleTapAction != GestureAction.NONE) {
                gestureCallbacks?.onGestureAction(settings.doubleTapAction)
                updateGestureState("Double tap detected")
            }
            return true
        }
        
        override fun onLongPress(e: MotionEvent) {
            if (!_gestureSettings.value.enabled) return
            
            val settings = _gestureSettings.value
            if (settings.longPressAction != GestureAction.NONE) {
                gestureCallbacks?.onGestureAction(settings.longPressAction)
                updateGestureState("Long press detected")
            }
        }
        
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (!_gestureSettings.value.enabled) return false
            
            val settings = _gestureSettings.value
            val deltaX = e2.x - (e1?.x ?: 0f)
            val deltaY = e2.y - (e1?.y ?: 0f)
            
            val action = when {
                Math.abs(deltaX) > Math.abs(deltaY) -> {
                    if (deltaX > 0) GestureAction.SWIPE_RIGHT else GestureAction.SWIPE_LEFT
                }
                else -> {
                    if (deltaY > 0) GestureAction.SWIPE_DOWN else GestureAction.SWIPE_UP
                }
            }
            
            if (settings.getActionForGesture(action) != GestureAction.NONE) {
                gestureCallbacks?.onGestureAction(settings.getActionForGesture(action))
                updateGestureState("Swipe detected: ${getActionDescription(action)}")
            }
            
            return true
        }
    }
    
    /**
     * Update gesture state
     */
    private fun updateGestureState(message: String) {
        val currentState = _gestureState.value
        _gestureState.value = currentState.copy(
            lastGesture = message,
            gestureCount = currentState.gestureCount + 1,
            lastGestureTime = System.currentTimeMillis()
        )
    }
}

/**
 * Gesture state data class
 */
data class GestureState(
    val isEnabled: Boolean = true,
    val lastGesture: String = "",
    val gestureCount: Int = 0,
    val lastGestureTime: Long = 0L,
    val isRecording: Boolean = false
)

/**
 * Gesture settings data class
 */
data class GestureSettings(
    val enabled: Boolean = true,
    val sensitivity: GestureSensitivity = GestureSensitivity.MEDIUM,
    val hapticFeedback: Boolean = true,
    val soundFeedback: Boolean = true,
    val visualFeedback: Boolean = true,
    val singleTapAction: GestureAction = GestureAction.PLAY_PAUSE,
    val doubleTapAction: GestureAction = GestureAction.NEXT_TRACK,
    val longPressAction: GestureAction = GestureAction.SHOW_QUEUE,
    val swipeUpAction: GestureAction = GestureAction.VOLUME_UP,
    val swipeDownAction: GestureAction = GestureAction.VOLUME_DOWN,
    val swipeLeftAction: GestureAction = GestureAction.PREVIOUS_TRACK,
    val swipeRightAction: GestureAction = GestureAction.NEXT_TRACK,
    val twoFingerSwipeUpAction: GestureAction = GestureAction.SHOW_LIBRARY,
    val twoFingerSwipeDownAction: GestureAction = GestureAction.SHOW_DISCOVERY,
    val twoFingerSwipeLeftAction: GestureAction = GestureAction.SHOW_EFFECTS,
    val twoFingerSwipeRightAction: GestureAction = GestureAction.SHOW_DOWNLOADS,
    val pinchZoomAction: GestureAction = GestureAction.ZOOM_ARTWORK,
    val rotateAction: GestureAction = GestureAction.ROTATE_ARTWORK
) {
    fun getActionForGesture(gesture: GestureAction): GestureAction {
        return when (gesture) {
            GestureAction.SWIPE_UP -> swipeUpAction
            GestureAction.SWIPE_DOWN -> swipeDownAction
            GestureAction.SWIPE_LEFT -> swipeLeftAction
            GestureAction.SWIPE_RIGHT -> swipeRightAction
            GestureAction.TWO_FINGER_SWIPE_UP -> twoFingerSwipeUpAction
            GestureAction.TWO_FINGER_SWIPE_DOWN -> twoFingerSwipeDownAction
            GestureAction.TWO_FINGER_SWIPE_LEFT -> twoFingerSwipeLeftAction
            GestureAction.TWO_FINGER_SWIPE_RIGHT -> twoFingerSwipeRightAction
            else -> GestureAction.NONE
        }
    }
}

/**
 * Gesture action enum
 */
enum class GestureAction {
    NONE,
    PLAY_PAUSE,
    NEXT_TRACK,
    PREVIOUS_TRACK,
    VOLUME_UP,
    VOLUME_DOWN,
    SHOW_QUEUE,
    SHOW_LIBRARY,
    SHOW_DISCOVERY,
    SHOW_EFFECTS,
    SHOW_DOWNLOADS,
    ZOOM_ARTWORK,
    ROTATE_ARTWORK,
    LIKE_TRACK,
    SHUFFLE_TOGGLE,
    REPEAT_TOGGLE,
    SHOW_SETTINGS,
    GO_BACK,
    SWIPE_UP,
    SWIPE_DOWN,
    SWIPE_LEFT,
    SWIPE_RIGHT,
    DOUBLE_TAP,
    LONG_PRESS,
    PINCH_ZOOM,
    ROTATE,
    TWO_FINGER_SWIPE_UP,
    TWO_FINGER_SWIPE_DOWN,
    TWO_FINGER_SWIPE_LEFT,
    TWO_FINGER_SWIPE_RIGHT
}

/**
 * Gesture sensitivity enum
 */
enum class GestureSensitivity {
    LOW, MEDIUM, HIGH
}

/**
 * Gesture callbacks interface
 */
interface GestureCallbacks {
    fun onGestureAction(action: GestureAction)
    fun onGestureError(error: String)
}

/**
 * Composable for gesture detection
 */
@Composable
fun GestureDetector(
    gestureController: GestureController,
    onGestureAction: (GestureAction) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    
    LaunchedEffect(gestureController) {
        gestureController.initialize(context, object : GestureCallbacks {
            override fun onGestureAction(action: GestureAction) {
                onGestureAction(action)
            }
            
            override fun onGestureError(error: String) {
                // Handle gesture errors
            }
        })
    }
    
    Box(
        modifier = modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    gestureController.onTouchEvent(event.nativeEvent)
                }
            }
        }
    ) {
        content()
    }
}
