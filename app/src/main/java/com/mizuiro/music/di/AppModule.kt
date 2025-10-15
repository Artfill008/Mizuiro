package com.mizuiro.music.di

import android.content.Context
import android.media.AudioManager
import android.support.v4.media.session.MediaSessionCompat
import com.mizuiro.music.data.local.LocalFilesScanner
import com.mizuiro.music.data.remote.YouTubeMusicApi
import com.mizuiro.music.data.remote.YouTubeMusicApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Application-level dependency injection module
 * 
 * Provides singletons and core dependencies for the Mizuiro Music app.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAudioManager(@ApplicationContext context: Context): AudioManager {
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    
    @Provides
    @Singleton
    fun provideMediaSession(@ApplicationContext context: Context): MediaSessionCompat {
        return MediaSessionCompat(context, "MizuiroMusic")
    }
    
    @Provides
    @Singleton
    fun provideYouTubeMusicApi(): YouTubeMusicApi {
        return YouTubeMusicApiImpl()
    }
    
    @Provides
    @Singleton
    fun provideLocalFilesScanner(@ApplicationContext context: Context): LocalFilesScanner {
        return LocalFilesScanner(context)
    }
}
