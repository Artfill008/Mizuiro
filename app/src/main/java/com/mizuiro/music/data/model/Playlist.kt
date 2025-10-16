package com.mizuiro.music.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Playlist data model
 * 
 * Represents both user-created playlists and system-generated ones.
 * Supports smart playlists with automatic rules.
 */
@Entity(tableName = "playlists")
@Serializable
data class Playlist(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val trackIds: List<String> = emptyList(),
    val source: PlaylistSource,
    val isSmart: Boolean = false,
    val smartRules: List<SmartRule> = emptyList(),
    val isPublic: Boolean = false,
    val isCollaborative: Boolean = false,
    val ownerId: String? = null,
    val ownerName: String? = null,
    val dateCreated: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis(),
    val playCount: Int = 0,
    val duration: Long = 0L, // total duration in milliseconds
    val trackCount: Int = 0,
    val tags: List<String> = emptyList(),
    val mood: TrackMood? = null,
    val energy: Float = 0.5f,
    val isDownloaded: Boolean = false,
    val isOffline: Boolean = false,
    val isLiked: Boolean = false,
    val isPinned: Boolean = false,
    val sortOrder: PlaylistSortOrder = PlaylistSortOrder.DATE_ADDED,
    val isShuffled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val lastPlayed: Long = 0L,
    val isRecentlyPlayed: Boolean = false,
    val isFrequentlyPlayed: Boolean = false,
    val isRecommended: Boolean = false,
    val recommendationScore: Float = 0.0f
)

@Serializable
enum class PlaylistSource {
    USER_CREATED,
    YOUTUBE_MUSIC,
    SYSTEM_GENERATED,
    IMPORTED,
    DISCOVERED
}

@Serializable
data class SmartRule(
    val type: SmartRuleType,
    val operator: SmartRuleOperator,
    val value: String,
    val isActive: Boolean = true
)

@Serializable
enum class SmartRuleType {
    GENRE,
    ARTIST,
    ALBUM,
    YEAR,
    MOOD,
    ENERGY,
    VALENCE,
    DANCEABILITY,
    TEMPO,
    DURATION,
    PLAY_COUNT,
    LAST_PLAYED,
    DATE_ADDED,
    IS_LIKED,
    IS_DOWNLOADED,
    LANGUAGE,
    COUNTRY,
    EXPLICIT,
    POPULARITY
}

@Serializable
enum class SmartRuleOperator {
    EQUALS,
    NOT_EQUALS,
    CONTAINS,
    NOT_CONTAINS,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN_OR_EQUAL,
    IS_EMPTY,
    IS_NOT_EMPTY,
    IN_RANGE,
    NOT_IN_RANGE
}

@Serializable
enum class PlaylistSortOrder {
    DATE_ADDED,
    TITLE,
    ARTIST,
    ALBUM,
    DURATION,
    PLAY_COUNT,
    LAST_PLAYED,
    RANDOM,
    CUSTOM
}

@Serializable
enum class RepeatMode {
    NONE,
    ONE,
    ALL
}

/**
 * Playlist with additional metadata for UI display
 */
data class PlaylistWithMetadata(
    val playlist: Playlist,
    val tracks: List<Track> = emptyList(),
    val isPlaying: Boolean = false,
    val currentTrackIndex: Int = -1,
    val isInQueue: Boolean = false,
    val isRecentlyPlayed: Boolean = false,
    val isFrequentlyPlayed: Boolean = false,
    val isRecommended: Boolean = false,
    val recommendationScore: Float = 0.0f
)

/**
 * Playlist search result with relevance score
 */
data class PlaylistSearchResult(
    val playlist: Playlist,
    val relevanceScore: Float = 0.0f,
    val matchType: MatchType = MatchType.TITLE
)
