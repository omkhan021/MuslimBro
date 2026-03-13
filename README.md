# MuslimBro

A comprehensive Islamic companion app for Android, built with modern Android development best practices. MuslimBro brings together prayer times, Quran reading, Qibla direction, alarm scheduling, and home screen widgets — all in one clean, offline-capable app.

---

## Features

- **Prayer Times** — Accurate calculation using the Adhan library with support for 13 calculation methods (MWL, Egyptian, Karachi, Umm al-Qura, etc.) and Hanafi/Shafi'i Madhabs. Per-prayer notification toggles and a live next-prayer countdown.
- **Quran Reader** — Browse all 114 Surahs, read Uthmani script with translations, and search across the full text.
- **Quran Audio Player** — Stream recitations via a Media3/ExoPlayer-backed foreground service with word-level highlighting.
- **Qibla Direction** — Compass-based Qibla finder using device sensors and GPS location.
- **Prayer Alarms** — Exact alarms using `USE_EXACT_ALARM` with Adhan audio playback (Fajr and regular).
- **Home Screen Widgets** — Two Glance-powered widgets (2×1 small, 4×2 medium) showing upcoming prayer times, auto-refreshed via WorkManager.
- **Settings** — Calculation method, Madhab, location mode (GPS / manual), and notification preferences all persisted with DataStore.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material3 |
| Architecture | Multi-module MVVM + Clean Architecture |
| DI | Hilt 2.59.2 (KSP) |
| Navigation | Jetpack Navigation Compose |
| Database | Room 2.6.1 (KSP) |
| Preferences | DataStore Preferences |
| Networking | Retrofit 2.11.0 + OkHttp 4.12.0 |
| Prayer Calc | Adhan2 (Kotlin Multiplatform) |
| Audio | Media3 / ExoPlayer 1.4.1 |
| Widgets | Jetpack Glance 1.1.1 |
| Background | WorkManager 2.9.1 |
| Location | Google Play Services Location 21.3.0 |
| Image Loading | Coil 2.7.0 |
| Build System | Gradle 9.3 + Convention Plugins |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 (Android 15) |

### Build Logic

The project uses a `build-logic` composite build with six convention plugins to eliminate boilerplate across modules:

| Plugin | Purpose |
|---|---|
| `muslimbro.android.application` | App-level AGP config, SDK versions, R8 |
| `muslimbro.android.library` | Library-level AGP config, consumer ProGuard |
| `muslimbro.android.feature` | Feature module defaults (depends on `library` + `hilt` + `compose`) |
| `muslimbro.hilt` | Applies Hilt + KSP, adds `hilt-android` dependency |
| `muslimbro.room` | Applies Room plugin + KSP, adds Room dependencies |
| `muslimbro.compose` | Enables Compose build feature, adds Compose BOM + Material3 |

---

## Project Structure

```
MuslimBro/
├── app/                        # Application shell, navigation host, DI entry point
├── build-logic/
│   └── convention/             # Gradle convention plugins
├── core/
│   ├── common/                 # Shared utilities (Result, Flow extensions, dispatchers)
│   ├── data/                   # Repository implementations, Room DB, DataStore, Workers
│   ├── domain/                 # Models, repository interfaces, use cases
│   ├── network/                # Retrofit API, network models
│   └── ui/                     # Shared Compose theme, components, fonts
└── feature/
    ├── alarms/                 # Alarm scheduling, BroadcastReceivers, foreground service
    ├── prayertimes/            # Prayer times screen + ViewModel
    ├── qibla/                  # Qibla compass screen + ViewModel
    ├── quran/                  # Surah list + Quran reader screens
    ├── quranplayer/            # ExoPlayer service, player bar, ViewModel
    ├── settings/               # Settings screen + ViewModel
    └── widget/                 # Glance widgets + WidgetUpdateWorker
```

---

## Prerequisites

| Tool | Version |
|---|---|
| Android Studio | Ladybug (2024.2.1) or newer |
| JDK | 21 |
| Android SDK | API 35 |
| Gradle | 9.3 (wrapper included) |
| Git | Any recent version |

> **Note:** The Gradle wrapper (`gradle/wrapper/gradle-wrapper.properties`) is included. You do **not** need Gradle installed globally.

---

## Local Setup

### 1. Clone the repository

```bash
git clone https://github.com/omkhan021/MuslimBro.git
cd MuslimBro
```

### 2. Open in Android Studio

- Open Android Studio → **File → Open** → select the `MuslimBro` folder.
- Wait for Gradle sync to complete. All dependencies will be downloaded automatically.

### 3. Configure SDK path

If prompted, set your Android SDK path. Or create/update `local.properties` in the project root:

```properties
sdk.dir=/path/to/your/Android/sdk
```

On Windows this typically looks like:
```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

### 4. Build & run

Select a device or emulator (API 26+) and click **Run**, or from the command line:

```bash
# Debug build
./gradlew assembleDebug

# Install directly to a connected device
./gradlew installDebug

# Release build (requires signing config)
./gradlew assembleRelease
```

---

## Quran Database

The Quran text, translations, and word-level data are stored in a pre-built SQLite database bundled as an asset at:

```
core/data/src/main/assets/quran_data.db
```

This database is copied to Room on first launch via `createFromAsset()`. No network call is required for Quran text — the app works fully offline for reading.

The database was generated using `build_quran_db.py` (included in the repo root) from open Quran data sources.

---

## Architecture

```
UI Layer (Compose Screens)
        ↓
ViewModel (StateFlow, Hilt)
        ↓
Use Cases (core:domain)
        ↓
Repository Interfaces (core:domain)
        ↓
Repository Implementations (core:data)
        ↓
Room DB / DataStore / Retrofit / Adhan
```

- **Unidirectional data flow** — ViewModels expose `StateFlow<UiState>`, screens collect and render.
- **AppResult<T>** — A sealed class (`Loading`, `Success`, `Error`) used throughout the data layer to represent async states without exceptions leaking into the UI.
- **Clean module boundaries** — Feature modules depend only on `core:domain` interfaces; they never touch `core:data` directly.

---

## Permissions

| Permission | Reason |
|---|---|
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | GPS-based prayer time calculation and Qibla direction |
| `USE_EXACT_ALARM` | Precise prayer alarm delivery (auto-granted on API 33+) |
| `POST_NOTIFICATIONS` | Prayer time notifications (runtime on API 33+) |
| `RECEIVE_BOOT_COMPLETED` | Reschedule alarms after device restart |
| `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Adhan audio and Quran playback services |
| `INTERNET` / `ACCESS_NETWORK_STATE` | Quran audio streaming |
| `VIBRATE` / `WAKE_LOCK` | Prayer alarm wake and vibration |

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

Please follow the existing module structure and convention plugins when adding new features.

---

## License

This project is open source. See [LICENSE](LICENSE) for details.
