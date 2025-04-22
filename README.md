# Daily Quote

<p align="center">
  <img src="https://github.com/abbasiandev/DailyQuote/blob/main/androidApp/src/main/ic_daily_quote-playstore.png" alt="Daily Quote Icon" width="150" height="150">
</p>

[View App Screenshot](https://mega.nz/file/TPpw1LaL#52lLcqE0Tp2-DaVRk5oc92ObM9z59c8oMBtCVZwNU4A)

## Table of Contents

1. [Features](#features)
2. [Architecture](#architecture)
3. [Project Structure](#project-structure)
4. [Setup and Installation](#setup-and-installation)
5. [Usage](#usage)
6. [Dependencies](#dependencies)
7. [Learning Objectives](#learning-objectives)
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

---

## Learning Objectives

This repository is designed to help developers learn the following:

1. **Kotlin Multiplatform Project (KMP)**:
   - Setting up a KMP project.
   - Sharing business logic across platforms.
   - Implementing platform-specific features.

2. **Clean Architecture**:
   - Structuring code into presentation, domain, and data layers.
   - Writing reusable and testable business logic.

3. **Modern UI Development**:
   - Using Jetpack Compose for Android.
   - Using SwiftUI for iOS.

4. **Dependency Injection**:
   - Setting up and using Koin for dependency management.

5. **Networking and Serialization**:
   - Making API calls with Ktor.
   - Parsing JSON responses with Kotlinx Serialization.

6. **Local Storage**:
   - Managing user preferences and local data storage with Multiplatform Settings.

7. **Time Management**:
   - Using `TimeRemainingService` to manage countdowns and provide updates on time remaining until the next quote becomes available.
