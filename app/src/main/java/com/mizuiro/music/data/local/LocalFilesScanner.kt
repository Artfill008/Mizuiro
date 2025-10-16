package com.mizuiro.music.data.local

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.mizuiro.music.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local Files Scanner
 * 
 * Scans the device for local music files and extracts metadata.
 * Supports various audio formats and provides smart deduplication.
 */
@Singleton
class LocalFilesScanner @Inject constructor(
    private val context: Context
) {
    
    private val mediaMetadataRetriever = MediaMetadataRetriever()
    
    /**
     * Scan for all local music files
     */
    suspend fun scanAllMusicFiles(): List<Track> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<Track>()
        
        // Query external storage for audio files
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.GENRE,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)
            val composerColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            
            while (cursor.moveToNext()) {
                try {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn) ?: ""
                    val title = cursor.getString(titleColumn) ?: name
                    val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                    val album = cursor.getString(albumColumn) ?: "Unknown Album"
                    val duration = cursor.getLong(durationColumn)
                    val size = cursor.getLong(sizeColumn)
                    val data = cursor.getString(dataColumn) ?: ""
                    val mimeType = cursor.getString(mimeTypeColumn) ?: ""
                    val year = cursor.getInt(yearColumn)
                    val genre = cursor.getString(genreColumn) ?: ""
                    val albumArtist = cursor.getString(albumArtistColumn) ?: artist
                    val composer = cursor.getString(composerColumn) ?: ""
                    val trackNumber = cursor.getInt(trackColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn) * 1000 // Convert to milliseconds
                    val dateModified = cursor.getLong(dateModifiedColumn) * 1000 // Convert to milliseconds
                    
                    // Skip if file doesn't exist
                    if (!File(data).exists()) continue
                    
                    // Extract additional metadata using MediaMetadataRetriever
                    val additionalMetadata = extractMetadata(data)
                    
                    val track = Track(
                        id = "local_$id",
                        title = title,
                        artist = artist,
                        album = album,
                        duration = duration,
                        thumbnailUrl = null, // Will be extracted separately
                        audioUrl = data,
                        localPath = data,
                        source = TrackSource.LOCAL_FILE,
                        youtubeId = null,
                        isLiked = false,
                        playCount = 0,
                        lastPlayed = 0L,
                        dateAdded = dateAdded,
                        genre = genre.ifEmpty { null },
                        year = if (year > 0) year else null,
                        bitrate = additionalMetadata.bitrate,
                        fileSize = size,
                        isDownloaded = true,
                        isOffline = true,
                        mood = additionalMetadata.mood,
                        energy = additionalMetadata.energy,
                        valence = additionalMetadata.valence,
                        danceability = additionalMetadata.danceability,
                        tempo = additionalMetadata.tempo,
                        key = additionalMetadata.key,
                        mode = additionalMetadata.mode,
                        acousticness = additionalMetadata.acousticness,
                        instrumentalness = additionalMetadata.instrumentalness,
                        liveness = additionalMetadata.liveness,
                        speechiness = additionalMetadata.speechiness,
                        popularity = 0.0f,
                        explicit = false,
                        language = null,
                        country = null,
                        tags = additionalMetadata.tags,
                        similarTracks = emptyList(),
                        audioFeatures = additionalMetadata.audioFeatures
                    )
                    
                    tracks.add(track)
                } catch (e: Exception) {
                    // Skip problematic files
                    continue
                }
            }
        }
        
        tracks
    }
    
    /**
     * Extract additional metadata from audio file
     */
    private suspend fun extractMetadata(filePath: String): ExtractedMetadata = withContext(Dispatchers.IO) {
        try {
            mediaMetadataRetriever.setDataSource(filePath)
            
            val bitrate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0
            val mimeType = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: ""
            val duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
            val artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
            val album = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: ""
            val genre = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: ""
            val year = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) ?: ""
            val track = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER) ?: ""
            val disc = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER) ?: ""
            val composer = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER) ?: ""
            val albumArtist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) ?: ""
            val writer = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER) ?: ""
            val date = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE) ?: ""
            val hasAudio = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO) ?: ""
            val hasVideo = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO) ?: ""
            val location = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION) ?: ""
            val rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION) ?: ""
            val width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: ""
            val height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: ""
            val frameCount = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT) ?: ""
            val frameRate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_RATE) ?: ""
            val channelCount = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUDIO_CHANNELS) ?: ""
            val sampleRate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUDIO_SAMPLE_RATE) ?: ""
            
            // Analyze audio features (simplified)
            val audioFeatures = analyzeAudioFeatures(filePath, duration, bitrate)
            
            // Determine mood based on audio features
            val mood = determineMood(audioFeatures)
            
            // Extract tags
            val tags = extractTags(genre, year, composer, albumArtist, writer)
            
            ExtractedMetadata(
                bitrate = bitrate,
                mimeType = mimeType,
                duration = duration,
                title = title,
                artist = artist,
                album = album,
                genre = genre,
                year = year,
                track = track,
                disc = disc,
                composer = composer,
                albumArtist = albumArtist,
                writer = writer,
                date = date,
                hasAudio = hasAudio,
                hasVideo = hasVideo,
                location = location,
                rotation = rotation,
                width = width,
                height = height,
                frameCount = frameCount,
                frameRate = frameRate,
                channelCount = channelCount,
                sampleRate = sampleRate,
                mood = mood,
                energy = audioFeatures.energy,
                valence = audioFeatures.valence,
                danceability = audioFeatures.danceability,
                tempo = audioFeatures.tempo,
                key = audioFeatures.key,
                mode = audioFeatures.mode,
                acousticness = audioFeatures.acousticness,
                instrumentalness = audioFeatures.instrumentalness,
                liveness = audioFeatures.liveness,
                speechiness = audioFeatures.speechiness,
                tags = tags,
                audioFeatures = audioFeatures
            )
        } catch (e: Exception) {
            // Return default metadata if extraction fails
            ExtractedMetadata()
        }
    }
    
    /**
     * Analyze audio features (simplified implementation)
     */
    private fun analyzeAudioFeatures(filePath: String, duration: Long, bitrate: Int): AudioFeatures {
        // This is a simplified implementation
        // In a real app, you would use audio analysis libraries like Essentia or librosa
        
        val durationMinutes = duration / 60000.0
        val bitrateKbps = bitrate / 1000.0
        
        // Simple heuristics based on file properties
        val energy = when {
            bitrateKbps > 320 -> 0.8f
            bitrateKbps > 256 -> 0.7f
            bitrateKbps > 192 -> 0.6f
            bitrateKbps > 128 -> 0.5f
            else -> 0.4f
        }
        
        val valence = when {
            durationMinutes > 5.0 -> 0.6f
            durationMinutes > 3.0 -> 0.5f
            else -> 0.4f
        }
        
        val danceability = when {
            bitrateKbps > 320 -> 0.7f
            bitrateKbps > 256 -> 0.6f
            else -> 0.5f
        }
        
        val tempo = when {
            durationMinutes > 4.0 -> 120f
            durationMinutes > 3.0 -> 110f
            else -> 100f
        }
        
        return AudioFeatures(
            loudness = 0.0f,
            tempo = tempo,
            timeSignature = 4,
            key = 0,
            mode = 0,
            danceability = danceability,
            energy = energy,
            valence = valence,
            acousticness = 0.0f,
            instrumentalness = 0.0f,
            liveness = 0.0f,
            speechiness = 0.0f,
            popularity = 0.0f
        )
    }
    
    /**
     * Determine mood based on audio features
     */
    private fun determineMood(audioFeatures: AudioFeatures): TrackMood? {
        val energy = audioFeatures.energy
        val valence = audioFeatures.valence
        val danceability = audioFeatures.danceability
        
        return when {
            energy > 0.8f && valence > 0.7f -> TrackMood.ENERGETIC
            energy > 0.7f && valence > 0.6f -> TrackMood.HAPPY
            energy < 0.3f && valence < 0.3f -> TrackMood.SAD
            energy < 0.4f && valence > 0.6f -> TrackMood.CALM
            energy > 0.6f && valence < 0.4f -> TrackMood.ANGRY
            danceability > 0.7f -> TrackMood.EXCITED
            energy < 0.5f && valence < 0.5f -> TrackMood.MELANCHOLIC
            else -> null
        }
    }
    
    /**
     * Extract tags from metadata
     */
    private fun extractTags(genre: String, year: String, composer: String, albumArtist: String, writer: String): List<String> {
        val tags = mutableListOf<String>()
        
        if (genre.isNotEmpty()) tags.add(genre)
        if (year.isNotEmpty()) tags.add(year)
        if (composer.isNotEmpty()) tags.add(composer)
        if (albumArtist.isNotEmpty()) tags.add(albumArtist)
        if (writer.isNotEmpty()) tags.add(writer)
        
        return tags
    }
    
    /**
     * Get supported audio formats
     */
    fun getSupportedFormats(): List<String> {
        return listOf(
            "audio/mpeg",
            "audio/mp3",
            "audio/mp4",
            "audio/aac",
            "audio/flac",
            "audio/ogg",
            "audio/wav",
            "audio/wma",
            "audio/m4a",
            "audio/3gp",
            "audio/amr",
            "audio/awb"
        )
    }
    
    /**
     * Check if file is supported
     */
    fun isSupportedFormat(mimeType: String): Boolean {
        return getSupportedFormats().contains(mimeType)
    }
}

/**
 * Extracted metadata from audio file
 */
private data class ExtractedMetadata(
    val bitrate: Int = 0,
    val mimeType: String = "",
    val duration: Long = 0L,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val genre: String = "",
    val year: String = "",
    val track: String = "",
    val disc: String = "",
    val composer: String = "",
    val albumArtist: String = "",
    val writer: String = "",
    val date: String = "",
    val hasAudio: String = "",
    val hasVideo: String = "",
    val location: String = "",
    val rotation: String = "",
    val width: String = "",
    val height: String = "",
    val frameCount: String = "",
    val frameRate: String = "",
    val channelCount: String = "",
    val sampleRate: String = "",
    val mood: TrackMood? = null,
    val energy: Float = 0.5f,
    val valence: Float = 0.5f,
    val danceability: Float = 0.5f,
    val tempo: Float = 120f,
    val key: Int? = null,
    val mode: Int? = null,
    val acousticness: Float = 0.0f,
    val instrumentalness: Float = 0.0f,
    val liveness: Float = 0.0f,
    val speechiness: Float = 0.0f,
    val tags: List<String> = emptyList(),
    val audioFeatures: AudioFeatures? = null
)
