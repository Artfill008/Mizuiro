# Mizuiro Music (水色)

> Where Music Meets Aesthetic

A revolutionary Android music player that completely rejects Material Design 3 in favor of the Japanese Mizuiro aesthetic - cold, melancholic, digital-nostalgic visual language that combines Otaku culture, Web 1.0 visuals, and kawaii minimalism.

## 🌊 Philosophy

Mizuiro Music is not just a music player. It's a digital artifact, frozen in time between 2000s webcore and modern otaku culture. It's intentionally awkward, because aesthetics matter more than ergonomics. It's cold, because warmth is mainstream. It's pixelated, because smooth edges are forgetfulness.

**This is not a product. This is a statement piece.**

## ✨ Features

### 🎵 Multi-Source Music
- **YouTube Music Integration** (Primary source)
- **Local Files Support** (Secondary source)
- **Smart Deduplication** (Links similar tracks)
- **Offline Caching** (Download for offline play)

### 🎨 Mizuiro Aesthetic
- **Cold Color Palette** (Never pure white, always blue-tinted)
- **Wobbly Lines** (Organic, animated dividers)
- **Kawaii Mascot** (Cinnamoroll-inspired character)
- **Retro Typography** (VT323 for digital elements)
- **Pixelated Animations** (2-4 fps for nostalgia)

### 🎛️ Audio Effects Lab
- **Nightcore** (Speed +25%, Pitch +2-3 semitones)
- **Vocaloid** (Pitch +3-5 semitones, robotization)
- **Daycore** (Speed -25%, Pitch -2-3 semitones)
- **Chipmunk** (Speed +35%, Pitch +4 semitones)
- **Slowed + Reverb** (Speed -15%, Heavy reverb)
- **Bass Boost** (Low-frequency enhancement)
- **Custom Parameters** (User-defined effects)

### 🎯 Smart Features
- **Mood-Based Discovery** (Energetic, Chill, Melancholic, etc.)
- **Smart Playlists** (Auto-generated based on rules)
- **Listening Statistics** (Retro-styled analytics)
- **Achievement System** (Kawaii badges and rewards)
- **Seasonal Themes** (Automatic aesthetic shifts)

## 🎨 Design Principles

### Color System
```
PRIMARY:
- Mizuiro Light: #D4F1F9 (main light blue)
- Mizuiro Base: #A8E6F0 (base accent blue)
- Mizuiro Medium: #7DD3E5 (medium intensity)

BACKGROUND:
- Cool White: #F5F8FA (main background)
- Icy Grey: #E8EDEF (secondary background)
- Concrete: #D3D8DB (module background)

ACCENTS:
- Steel Blue: #B8C5D0 (secondary text)
- Cyber Silver: #C8D4DC (borders, dividers)
- Faded Black: #4A5860 (primary text)

KAWAII ACCENT:
- Blush Pink: #FFD4E5 (special elements only)
- Pastel Lavender: #E8E4F3 (auxiliary kawaii)
```

### Typography
- **Primary**: M PLUS Rounded 1c (rounded, friendly)
- **Retro**: VT323 (pixelated, digital)
- **Fallback**: System fonts with rounded characteristics

### Animation Philosophy
- **Linear Easing** (Not Material's smooth curves)
- **Stepped Animations** (Pixelated feel)
- **No Ripple Effects** (Custom glow instead)
- **Low Frame Rate** (2-4 fps for nostalgia)

## 🏗️ Architecture

### Tech Stack
- **Kotlin** + **Jetpack Compose**
- **Hilt** (Dependency Injection)
- **Room** (Local Database)
- **Retrofit** (Networking)
- **Coil** (Image Loading)
- **ExoPlayer** (Media Playback)
- **FFmpeg** (Audio Processing)
- **NewPipe Extractor** (YouTube Integration)

### Project Structure
```
app/
├── src/main/java/com/mizuiro/music/
│   ├── ui/
│   │   ├── theme/           # Mizuiro theme system
│   │   ├── components/      # Custom UI components
│   │   │   ├── buttons/     # Mizuiro buttons
│   │   │   ├── cards/       # Custom cards
│   │   │   ├── decorations/ # Wobbly lines, mascot
│   │   │   └── layouts/     # Custom layouts
│   │   ├── screens/         # App screens
│   │   └── navigation/      # Navigation system
│   ├── data/                # Data layer
│   ├── domain/              # Business logic
│   └── service/             # Background services
└── src/main/res/
    ├── values/              # Colors, strings, themes
    ├── font/                # Custom fonts
    └── drawable/            # Icons and images
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 26+ (Android 8.0)
- Kotlin 1.9.10+
- Gradle 8.0+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run on device or emulator

### Building
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## 🎯 Target Audience

This app is designed for:
- **Aesthetic Enthusiasts** who value unique experiences
- **Otaku Culture** fans who appreciate Japanese aesthetics
- **Digital Nostalgia** lovers who miss Web 1.0
- **Music Lovers** who want something different
- **Design Rebels** who reject mainstream UI patterns

## ⚠️ Important Notes

### Design Philosophy
- **Aesthetics Over Ergonomics**: The UI prioritizes visual appeal over conventional usability
- **Intentional Unconventionality**: If it feels weird, you're probably doing it right
- **No Material Design**: This app completely rejects Material Design 3
- **Adaptation Period**: Users need time to adapt to the unique interface

### Performance
- **60fps Target**: Despite the aesthetic, the app maintains smooth performance
- **Memory Optimized**: Efficient caching and memory management
- **Battery Conscious**: Adaptive quality based on battery level

## 🧪 Testing

### Visual Regression Tests
- Screenshot comparisons for UI consistency
- Color palette validation
- Typography rendering checks
- Animation performance tests

### Functional Tests
- Audio playback quality
- Effects processing accuracy
- Network connectivity handling
- Offline mode functionality

## 📱 Screenshots

*Coming soon - the app is currently in development*

## 🤝 Contributing

We welcome contributions from aesthetic enthusiasts! Please read our contributing guidelines and ensure your changes maintain the Mizuiro aesthetic.

### Development Guidelines
1. **Read the entire technical specification** (65 sections!)
2. **Understand the aesthetic** before coding
3. **No shortcuts** - don't use Material Design components
4. **Embrace the unusual** - if it looks like every other app, you're doing it wrong
5. **Maintain the vision** - always ask "Does this maintain the Mizuiro aesthetic?"

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Mizuiro Aesthetic Community** for inspiration
- **Otaku Culture** for the visual language
- **Web 1.0 Nostalgia** for the retro elements
- **Kawaii Culture** for the cute elements

## 📞 Support

- **Discord**: [Join our community](https://discord.gg/mizuiromusic)
- **Reddit**: [r/MizuiroMusic](https://reddit.com/r/MizuiroMusic)
- **GitHub Issues**: [Report bugs](https://github.com/mizuiro-music/android/issues)
- **Email**: support@mizuiromusic.app

---

**Made with ♡ and ～～～ by aesthetic enthusiasts**

*Version 1.0.0-mizuiro-classic*

♪♪♪ 水色 ♪♪♪
