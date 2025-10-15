# Mizuiro Music - ProGuard Rules
# ═══════════════════════════════════════

# Keep Mizuiro-specific classes
-keep class com.mizuiro.music.** { *; }
-keepclassmembers class com.mizuiro.music.** { *; }

# Keep custom fonts
-keep class androidx.compose.ui.text.font.** { *; }

# FFmpeg
-keep class com.arthenica.mobileffmpeg.** { *; }

# NewPipe Extractor
-keep class org.schabi.newpipe.** { *; }
-dontwarn org.schabi.newpipe.**

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keepattributes Signature
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# Media3
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Custom animations and effects
-keep class com.mizuiro.music.ui.components.WobblyLine { *; }
-keep class com.mizuiro.music.ui.components.KawaiiMascot { *; }
-keep class com.mizuiro.music.audio.effects.** { *; }

# Debug builds
-printmapping mapping.txt
-printseeds seeds.txt
-printusage usage.txt
