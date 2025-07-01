# DailyQuote - Cross-Platform Inspirational Quotes App

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-blue.svg)](https://kotlinlang.org/lp/mobile/)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![iOS](https://img.shields.io/badge/Platform-iOS-lightgrey.svg)](https://developer.apple.com/ios/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg)](https://developer.android.com/jetpack/compose)
[![SwiftUI](https://img.shields.io/badge/UI-SwiftUI-blue.svg)](https://developer.apple.com/xcode/swiftui/)

A modern cross-platform mobile application built with **Kotlin Multiplatform** that delivers daily inspirational quotes. Features native UI with **Jetpack Compose** for Android and **SwiftUI** for iOS, implementing **Clean Architecture** with offline-first approach.

---

## 📱 Download & Try

**⭐ Star this repository if you find it helpful!**

[📖 Read the Complete Development Guide on Medium](https://medium.com/@abbasian.dev/zero-to-hero-in-kmm-real-app-development-with-compose-and-swiftui-7157b7de8528)

---

## Table of Contents

1. [Features](#features)
2. [Architecture](#architecture)
3. [Project Structure](#project-structure)
4. [Setup and Installation](#setup-and-installation)
5. [Usage](#usage)
6. [Dependencies](#dependencies)
---

## Features

### Cross-Platform Support
- **Shared Business Logic**: The application uses Kotlin Multiplatform to share core business logic between Android and iOS platforms, reducing code duplication and ensuring consistency.
- **Platform-Specific UI**: While the business logic is shared, the UI is implemented natively for each platform (Jetpack Compose for Android and SwiftUI for iOS) to provide a seamless user experience.

### Daily Inspirational Quotes
- **Fetch Daily Quotes**: The app fetches a new inspirational quote every day from a remote API.
- **Caching**: Quotes are cached locally to ensure offline access.

### Favorites Management
- **Save Quotes**: Users can mark quotes as favorites for quick access later.
- **Observe Favorites**: The app uses reactive programming to update the UI whenever the list of favorite quotes changes.

### Offline Support
- **Local Storage**: The app uses a local database to store quotes and user preferences, ensuring functionality even without an internet connection.

---

## Architecture

The project follows a **Clean Architecture** approach, which separates concerns into distinct layers:

1. **Presentation Layer**:
   - **Android**: Built using Jetpack Compose, a modern declarative UI toolkit.
   - **iOS**: Built using SwiftUI, Apple's declarative UI framework.

2. **Domain Layer**:
   - Contains business logic and use cases, such as fetching quotes, managing favorites, and handling notifications.

3. **Data Layer**:
   - **Remote Data Source**: Fetches quotes from a remote API using Ktor.
   - **Local Data Source**: Stores quotes and user preferences locally using Multiplatform Settings and platform-specific storage solutions.

---

## Project Structure

```
.
├── androidApp/          # Android-specific code and resources
│   ├── src/main/        # Main source code for Android
│   ├── build.gradle.kts # Android module Gradle configuration
│   └── ...              # Other Android-specific files
├── iosApp/              # iOS-specific code and resources
│   ├── iosApp.xcodeproj # Xcode project configuration
│   ├── SwiftUI Views    # iOS UI components
│   └── ...              # Other iOS-specific files
├── shared/              # Shared Kotlin Multiplatform code
│   ├── src/commonMain/  # Shared business logic
│   ├── src/androidMain/ # Android-specific implementations
│   ├── src/iosMain/     # iOS-specific implementations
│   └── build.gradle.kts # Shared module Gradle configuration
├── build.gradle.kts     # Root Gradle configuration
├── settings.gradle.kts  # Gradle settings
└── README.md            # Project documentation
```

---

## Setup and Installation

### Steps to Build

#### Android
1. Clone the repository:
   ```bash
   git clone https://github.com/abbasiandev/DailyQuote.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle:
   ```bash
   ./gradlew build
   ```
4. Run the app on an Android emulator or device.

#### iOS
1. Open the `iosApp/iosApp.xcodeproj` in Xcode.
2. Generate the shared framework:
   ```bash
   ./gradlew :shared:embedAndSignAppleFrameworkForXcode
   ```
3. Build and run the app on an iOS simulator or device.

---

## Usage

### Android
- Launch the app to view the daily quote.
- Use the "Favorite" button to save quotes.
- Access your saved quotes in the "Favorites" section.

### iOS
- Open the app to see the daily quote.
- Swipe to save quotes to your favorites.
- Access your saved quotes in the "Favorites" section.
- iOS code should be written and maintained in Xcode, as Android Studio's Swift support plugins are limited and not robust enough for full development.

---

## Dependencies

### Shared
- **Ktor**: Networking library for API calls.
- **Kotlinx Serialization**: JSON serialization.
- **Multiplatform Settings**: Cross-platform preferences management.

### Android
- **Jetpack Compose**: Modern UI toolkit for Android.
- **Coil**: Image loading library.
- **Koin**: Dependency injection framework.
- **DataStore**: Used for managing local storage and preferences on the Android side.

### iOS
- **SwiftUI**: Declarative UI framework for iOS.
- **Kingfisher**: Image caching and loading library.
- **Russhwolf Multiplatform Settings**: Used for managing local storage and preferences on the iOS side.
