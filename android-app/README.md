# Genshin DPS Archive — Android

Native Android application for the existing Genshin DPS Archive website.

## Project decisions

- **UI:** Kotlin + Jetpack Compose; this is a native Android interface, not a WebView wrapper.
- **Offline-first:** character data and portrait files are packaged as Android assets. Search, tier filters, favourites, and owned-character tracking work locally.
- **Source content:** the website remains at the repository root. The asset importer reads `tiers/*.html` and `assets/images/characters/*` during the Android build.
- **Package:** `com.thunder2700c.genshindpsarchive`
- **SDK:** compile/target API 36, with minimum API 26.
- **Website:** the current Vercel site is not replaced or moved by the Android project.

## Build the debug APK

The supported first build path is the **Android Build** GitHub Actions workflow. Every push affecting `android-app/`, `tiers/`, or character portraits can build a debug APK and upload it as an Actions artifact.

To build locally, use JDK 17, Android SDK Platform 36, Gradle 8.13, and Python 3. The app module's `preBuild` task runs `tools/import_website_data.py` and creates a generated offline asset bundle from the website's local files.

## Milestones

1. Native app skeleton and repeatable APK build.
2. Validate content import, screens, local persistence, and offline operation.
3. Improve app icon, accessibility, tests, performance, and release signing.
4. Produce a signed Android App Bundle (AAB), complete Play Console declarations, and publish after asset-rights checks.
5. Add the mini-challenge feature after the core app is complete.

The first debug build is a development artifact, not a Play Store-ready release.
