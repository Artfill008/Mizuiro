package com.mizuiro.music

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Mizuiro Music Application
 * 
 * The main application class that initializes Hilt dependency injection
 * and sets up the app-wide configuration for the Mizuiro aesthetic.
 */
@HiltAndroidApp
class MizuiroMusicApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize app-wide configurations
        initializeMizuiroConfig()
    }
    
    private fun initializeMizuiroConfig() {
        // Set up any app-wide configurations here
        // This could include:
        // - Font loading
        // - Theme initialization
        // - Audio engine setup
        // - Cache configuration
    }
}
