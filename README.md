# Genshin DPS Archive

Theorycrafting archive for Genshin Impact character rankings, combat baselines, and account-aware pull-value guidance.

## Live website

https://genshin-tier-list-eight.vercel.app/

## Native Android app

A native Kotlin + Jetpack Compose Android project is being built alongside the website in `android-app/`. It is **not a WebView wrapper**.

- Native interface with Home, Tier List, Character Detail, and My Roster screens.
- Search by character, role, and element; filter by tier and element.
- Local favourites and owned-character list.
- Offline content bundle generated from the existing tier pages and character portrait files.
- GitHub Actions workflow for building and uploading a debug APK artifact.

### Build status and test APK

The APK workflow is in [GitHub Actions](https://github.com/Thunder2700c/Genshin-Tier-List/actions/workflows/android-build.yml). Open a completed **Android Build** run and download the `genshin-dps-archive-debug-apk` artifact. Debug APKs are for testing, not Play Store publication.

Read [android-app/README.md](android-app/README.md) for project setup, technical details, and release milestones.

## Development milestones

1. Complete and validate the native UI and offline content import.
2. Build and test a debug APK on real Android devices, including airplane-mode checks.
3. Improve the app icon, accessibility, startup performance, and automated tests.
4. Prepare a signed Android App Bundle (AAB), verify third-party asset permissions, and finish the Play Console listing and policy declarations.
5. Implement the mini-challenge feature **after the core app is complete**, then test it before including it in a release.

## Disclaimer

This is an independent fan theorycrafting project and is not affiliated with or endorsed by HoYoverse. Genshin Impact and its characters, artwork, and related marks belong to their respective rights holders. Third-party artwork and assets must be used in accordance with the applicable rights and permissions.
