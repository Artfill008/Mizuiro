package com.mizuiro.music.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.mizuiro.music.R
import com.mizuiro.music.data.model.Track
import com.mizuiro.music.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Mizuiro Music Service
 * 
 * Background service that handles music playback and media session.
 * Provides a clean interface for the UI to control playback.
 */
@AndroidEntryPoint
class MizuiroMusicService : Service() {
    
    @Inject
    lateinit var audioManager: AudioManager
    
    @Inject
    lateinit var mediaSession: MediaSessionCompat
    
    private val binder = MusicBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private var currentTrack: Track? = null
    private var isPlaying = false
    private var currentPosition = 0L
    private var duration = 0L
    private var playbackSpeed = 1.0f
    private var isShuffled = false
    private var repeatMode = RepeatMode.NONE
    
    private var audioFocusRequest: AudioFocusRequest? = null
    private var audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener? = null
    
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "mizuiro_music_channel"
        private const val CHANNEL_NAME = "Mizuiro Music"
        private const val CHANNEL_DESCRIPTION = "Music playback notifications"
    }
    
    inner class MusicBinder : Binder() {
        fun getService(): MizuiroMusicService = this@MizuiroMusicService
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupAudioFocus()
        setupMediaSession()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> play()
            ACTION_PAUSE -> pause()
            ACTION_STOP -> stop()
            ACTION_NEXT -> next()
            ACTION_PREVIOUS -> previous()
            ACTION_SHUFFLE -> toggleShuffle()
            ACTION_REPEAT -> toggleRepeat()
            ACTION_SEEK_TO -> {
                val position = intent.getLongExtra(EXTRA_POSITION, 0L)
                seekTo(position)
            }
            ACTION_SET_TRACK -> {
                val track = intent.getParcelableExtra<Track>(EXTRA_TRACK)
                track?.let { setTrack(it) }
            }
            ACTION_SET_QUEUE -> {
                val tracks = intent.getParcelableArrayListExtra<Track>(EXTRA_QUEUE)
                tracks?.let { setQueue(it) }
            }
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        releaseAudioFocus()
        mediaSession.release()
    }
    
    /**
     * Create notification channel for Android O+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = CHANNEL_DESCRIPTION
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Setup audio focus for proper audio handling
     */
    private fun setupAudioFocus() {
        audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    // Resume playback
                    if (isPlaying) {
                        play()
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    // Stop playback
                    pause()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    // Pause playback temporarily
                    pause()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    // Lower volume
                    // TODO: Implement volume ducking
                }
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(audioFocusChangeListener!!)
            }.build()
        }
    }
    
    /**
     * Setup media session for media controls
     */
    private fun setupMediaSession() {
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                play()
            }
            
            override fun onPause() {
                pause()
            }
            
            override fun onStop() {
                stop()
            }
            
            override fun onSkipToNext() {
                next()
            }
            
            override fun onSkipToPrevious() {
                previous()
            }
            
            override fun onSeekTo(pos: Long) {
                seekTo(pos)
            }
        })
        
        mediaSession.isActive = true
    }
    
    /**
     * Start foreground service with notification
     */
    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }
    
    /**
     * Create notification for music playback
     */
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                R.drawable.ic_pause,
                "Pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PAUSE
                )
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play,
                "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY
                )
            )
        }
        
        val nextAction = NotificationCompat.Action(
            R.drawable.ic_skip_next,
            "Next",
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
        )
        
        val previousAction = NotificationCompat.Action(
            R.drawable.ic_skip_previous,
            "Previous",
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentTrack?.title ?: "Mizuiro Music")
            .setContentText(currentTrack?.artist ?: "Unknown Artist")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(null) // TODO: Add album art
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .build()
    }
    
    /**
     * Request audio focus
     */
    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { request ->
                audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } ?: false
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }
    
    /**
     * Release audio focus
     */
    private fun releaseAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { request ->
                audioManager.abandonAudioFocusRequest(request)
            }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }
    
    /**
     * Update media session metadata
     */
    private fun updateMediaSession() {
        currentTrack?.let { track ->
            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album ?: "")
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration)
                .build()
            
            mediaSession.setMetadata(metadata)
        }
        
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setState(
                if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                currentPosition,
                playbackSpeed
            )
            .build()
        
        mediaSession.setPlaybackState(playbackState)
    }
    
    // Public API methods
    
    fun play() {
        if (requestAudioFocus()) {
            isPlaying = true
            startForegroundService()
            updateMediaSession()
            // TODO: Start actual audio playback
        }
    }
    
    fun pause() {
        isPlaying = false
        updateMediaSession()
        // TODO: Pause actual audio playback
    }
    
    fun stop() {
        isPlaying = false
        currentPosition = 0L
        releaseAudioFocus()
        stopForeground(true)
        stopSelf()
    }
    
    fun next() {
        // TODO: Implement next track logic
        updateMediaSession()
    }
    
    fun previous() {
        // TODO: Implement previous track logic
        updateMediaSession()
    }
    
    fun seekTo(position: Long) {
        currentPosition = position
        updateMediaSession()
        // TODO: Implement seek logic
    }
    
    fun setTrack(track: Track) {
        currentTrack = track
        duration = track.duration
        currentPosition = 0L
        updateMediaSession()
    }
    
    fun setQueue(tracks: List<Track>) {
        // TODO: Implement queue management
    }
    
    fun toggleShuffle() {
        isShuffled = !isShuffled
        updateMediaSession()
    }
    
    fun toggleRepeat() {
        repeatMode = when (repeatMode) {
            RepeatMode.NONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.NONE
        }
        updateMediaSession()
    }
    
    fun getCurrentTrack(): Track? = currentTrack
    fun isPlaying(): Boolean = isPlaying
    fun getCurrentPosition(): Long = currentPosition
    fun getDuration(): Long = duration
    fun isShuffled(): Boolean = isShuffled
    fun getRepeatMode(): RepeatMode = repeatMode
    
    // Service actions
    companion object {
        const val ACTION_PLAY = "com.mizuiro.music.PLAY"
        const val ACTION_PAUSE = "com.mizuiro.music.PAUSE"
        const val ACTION_STOP = "com.mizuiro.music.STOP"
        const val ACTION_NEXT = "com.mizuiro.music.NEXT"
        const val ACTION_PREVIOUS = "com.mizuiro.music.PREVIOUS"
        const val ACTION_SHUFFLE = "com.mizuiro.music.SHUFFLE"
        const val ACTION_REPEAT = "com.mizuiro.music.REPEAT"
        const val ACTION_SEEK_TO = "com.mizuiro.music.SEEK_TO"
        const val ACTION_SET_TRACK = "com.mizuiro.music.SET_TRACK"
        const val ACTION_SET_QUEUE = "com.mizuiro.music.SET_QUEUE"
        
        const val EXTRA_POSITION = "position"
        const val EXTRA_TRACK = "track"
        const val EXTRA_QUEUE = "queue"
    }
}

enum class RepeatMode {
    NONE,
    ALL,
    ONE
}
