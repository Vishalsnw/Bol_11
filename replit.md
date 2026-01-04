# Bol_11

## Overview
This is a fantasy movie trading Android app built in Kotlin. It simulates a movie stock market where users can "trade" movies using virtual coins based on real-world box office performance.

## Project Architecture
- **Language**: Kotlin
- **Platform**: Android (Native)
- **Build System**: Gradle 8.2.0 with Kotlin 1.9.20
- **UI**: XML Layouts with Material Design (Dark Theme)
- **Data**: Real-time automated movie data scraping using Jsoup from public search results
- **AI**: 1000+ AI traders (Market Bots, Studio Bots) to maintain market liquidity

## Project Structure
```
├── app/
│   ├── src/main/
│   │   ├── java/com/vishalsnw/bol11/
│   │   │   ├── api/MovieDataScraper.kt    - Web scraping for movie data
│   │   │   ├── model/Models.kt            - Data models
│   │   │   └── MainActivity.kt            - Main activity
│   │   ├── res/layout/activity_main.xml   - Main UI layout
│   │   └── AndroidManifest.xml            - App manifest
│   └── build.gradle                        - App-level build config
├── build.gradle                            - Project-level build config
└── settings.gradle                         - Project settings
```

## Dependencies
- AndroidX Core KTX 1.12.0
- AppCompat 1.6.1
- Material Design 1.11.0
- ConstraintLayout 2.1.4
- Jsoup 1.17.2 (HTML parsing)
- Lifecycle components 2.7.0
- Kotlin Coroutines 1.7.3

## Replit Environment Notes
- **This is a native Android app** - it cannot be built or run directly in Replit
- Replit does not have Android SDK or emulator support
- Use this environment for viewing and editing source code
- **For building**: Use GitHub Actions, Android Studio locally, or a CI/CD pipeline

## Build Instructions (Outside Replit)
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on emulator or physical device

## Project State
- **Status**: Initial Android project structure created
- **Last Updated**: January 2026
