package com.mizuiro.music.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Track data model representing a music track
 * 
 * This is the core data structure for all music content in Mizuiro Music.
 * Supports both YouTube Music and local files with smart deduplication.
 */
@Entity(tableName = "tracks")
@Serializable
data class Track(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val album: String? = null,
    val duration: Long = 0L, // in milliseconds
    val thumbnailUrl: String? = null,
    val audioUrl: String? = null,
    val localPath: String? = null,
    val source: TrackSource,
    val youtubeId: String? = null,
    val isLiked: Boolean = false,
    val playCount: Int = 0,
    val lastPlayed: Long = 0L,
    val dateAdded: Long = System.currentTimeMillis(),
    val genre: String? = null,
    val year: Int? = null,
    val bitrate: Int? = null,
    val fileSize: Long? = null,
    val isDownloaded: Boolean = false,
    val isOffline: Boolean = false,
    val mood: TrackMood? = null,
    val energy: Float = 0.5f, // 0.0 to 1.0
    val valence: Float = 0.5f, // 0.0 to 1.0
    val danceability: Float = 0.5f, // 0.0 to 1.0
    val tempo: Float = 120f, // BPM
    val key: Int? = null, // 0-11 (C=0, C#=1, etc.)
    val mode: Int? = null, // 0=minor, 1=major
    val acousticness: Float = 0.0f,
    val instrumentalness: Float = 0.0f,
    val liveness: Float = 0.0f,
    val speechiness: Float = 0.0f,
    val popularity: Float = 0.0f,
    val explicit: Boolean = false,
    val language: String? = null,
    val country: String? = null,
    val tags: List<String> = emptyList(),
    val similarTracks: List<String> = emptyList(), // IDs of similar tracks
    val audioFeatures: AudioFeatures? = null
)

@Serializable
enum class TrackSource {
    YOUTUBE_MUSIC,
    LOCAL_FILE,
    STREAMING,
    DOWNLOADED
}

@Serializable
enum class TrackMood {
    ENERGETIC,
    CHILL,
    MELANCHOLIC,
    ROMANTIC,
    NOSTALGIC,
    DREAMY,
    AGGRESSIVE,
    PEACEFUL,
    MYSTERIOUS,
    HAPPY,
    SAD,
    ANGRY,
    EXCITED,
    CALM,
    LONELY,
    HOPEFUL
}

@Serializable
data class AudioFeatures(
    val loudness: Float = 0.0f,
    val tempo: Float = 120f,
    val timeSignature: Int = 4,
    val key: Int = 0,
    val mode: Int = 0,
    val danceability: Float = 0.5f,
    val energy: Float = 0.5f,
    val valence: Float = 0.5f,
    val acousticness: Float = 0.0f,
    val instrumentalness: Float = 0.0f,
    val liveness: Float = 0.0f,
    val speechiness: Float = 0.0f,
    val popularity: Float = 0.0f
)

/**
 * Track with additional metadata for UI display
 */
data class TrackWithMetadata(
    val track: Track,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val currentPosition: Long = 0L,
    val isInQueue: Boolean = false,
    val queuePosition: Int = -1,
    val isInPlaylist: Boolean = false,
    val playlistIds: List<String> = emptyList(),
    val isRecentlyPlayed: Boolean = false,
    val isFrequentlyPlayed: Boolean = false,
    val isRecommended: Boolean = false,
    val recommendationScore: Float = 0.0f
)

/**
 * Track search result with relevance score
 */
data class TrackSearchResult(
    val track: Track,
    val relevanceScore: Float = 0.0f,
    val matchType: MatchType = MatchType.TITLE
)

@Serializable
enum class MatchType {
    TITLE,
    ARTIST,
    ALBUM,
    GENRE,
    LYRIC,
    SIMILAR
}
