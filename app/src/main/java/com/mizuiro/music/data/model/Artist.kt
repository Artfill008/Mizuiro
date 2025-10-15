package com.mizuiro.music.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Artist data model
 * 
 * Represents music artists with comprehensive metadata
 * and smart deduplication across sources.
 */
@Entity(tableName = "artists")
@Serializable
data class Artist(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val bannerUrl: String? = null,
    val trackIds: List<String> = emptyList(),
    val albumIds: List<String> = emptyList(),
    val source: ArtistSource,
    val youtubeId: String? = null,
    val spotifyId: String? = null,
    val isLiked: Boolean = false,
    val isFollowed: Boolean = false,
    val playCount: Int = 0,
    val lastPlayed: Long = 0L,
    val dateAdded: Long = System.currentTimeMillis(),
    val genre: String? = null,
    val subgenres: List<String> = emptyList(),
    val country: String? = null,
    val language: String? = null,
    val yearFormed: Int? = null,
    val yearDisbanded: Int? = null,
    val isActive: Boolean = true,
    val members: List<ArtistMember> = emptyList(),
    val socialLinks: Map<String, String> = emptyMap(),
    val tags: List<String> = emptyList(),
    val similarArtists: List<String> = emptyList(), // IDs of similar artists
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
    val speechiness: Float = 0.0f
)

@Serializable
enum class ArtistSource {
    YOUTUBE_MUSIC,
    LOCAL_FILE,
    STREAMING,
    IMPORTED
}

@Serializable
data class ArtistMember(
    val name: String,
    val role: String,
    val isActive: Boolean = true,
    val joinYear: Int? = null,
    val leaveYear: Int? = null
)

/**
 * Artist with additional metadata for UI display
 */
data class ArtistWithMetadata(
    val artist: Artist,
    val tracks: List<Track> = emptyList(),
    val albums: List<Album> = emptyList(),
    val isPlaying: Boolean = false,
    val currentTrackIndex: Int = -1,
    val isInQueue: Boolean = false,
    val isRecentlyPlayed: Boolean = false,
    val isFrequentlyPlayed: Boolean = false,
    val isRecommended: Boolean = false,
    val recommendationScore: Float = 0.0f
)

/**
 * Artist search result with relevance score
 */
data class ArtistSearchResult(
    val artist: Artist,
    val relevanceScore: Float = 0.0f,
    val matchType: MatchType = MatchType.TITLE
)
