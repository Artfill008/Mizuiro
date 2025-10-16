package com.mizuiro.music.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Album data model
 * 
 * Represents music albums with comprehensive metadata
 * and smart deduplication across sources.
 */
@Entity(tableName = "albums")
@Serializable
data class Album(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val artistId: String,
    val thumbnailUrl: String? = null,
    val bannerUrl: String? = null,
    val trackIds: List<String> = emptyList(),
    val source: AlbumSource,
    val youtubeId: String? = null,
    val spotifyId: String? = null,
    val isLiked: Boolean = false,
    val playCount: Int = 0,
    val lastPlayed: Long = 0L,
    val dateAdded: Long = System.currentTimeMillis(),
    val releaseDate: Long = 0L,
    val year: Int? = null,
    val genre: String? = null,
    val subgenres: List<String> = emptyList(),
    val country: String? = null,
    val language: String? = null,
    val label: String? = null,
    val catalogNumber: String? = null,
    val barcode: String? = null,
    val isCompilation: Boolean = false,
    val isLive: Boolean = false,
    val isRemaster: Boolean = false,
    val isExplicit: Boolean = false,
    val duration: Long = 0L, // total duration in milliseconds
    val trackCount: Int = 0,
    val discCount: Int = 1,
    val tags: List<String> = emptyList(),
    val similarAlbums: List<String> = emptyList(), // IDs of similar albums
    val isDownloaded: Boolean = false,
    val isOffline: Boolean = false,
    val isRecentlyPlayed: Boolean = false,
    val isFrequentlyPlayed: Boolean = false,
    val isRecommended: Boolean = false,
    val recommendationScore: Float = 0.0f,
    val popularity: Float = 0.0f,
    val energy: Float = 0.5f,
    val valence: Float = 0.5f,
    val danceability: Float = 0.5f,
    val tempo: Float = 120f,
    val acousticness: Float = 0.0f,
    val instrumentalness: Float = 0.0f,
    val liveness: Float = 0.0f,
    val speechiness: Float = 0.0f,
    val mood: TrackMood? = null
)

@Serializable
enum class AlbumSource {
    YOUTUBE_MUSIC,
    LOCAL_FILE,
    STREAMING,
    IMPORTED
}

/**
 * Album with additional metadata for UI display
 */
data class AlbumWithMetadata(
    val album: Album,
    val tracks: List<Track> = emptyList(),
    val artist: Artist? = null,
    val isPlaying: Boolean = false,
    val currentTrackIndex: Int = -1,
    val isInQueue: Boolean = false,
    val isRecentlyPlayed: Boolean = false,
    val isFrequentlyPlayed: Boolean = false,
    val isRecommended: Boolean = false,
    val recommendationScore: Float = 0.0f
)

/**
 * Album search result with relevance score
 */
data class AlbumSearchResult(
    val album: Album,
    val relevanceScore: Float = 0.0f,
    val matchType: MatchType = MatchType.TITLE
)
