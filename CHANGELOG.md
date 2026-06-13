# Changelog

All notable changes to GeoFlash will be documented in this file.

---

## [1.0.0] — 2026-06-13

### Initial Release

**Core features**
- Real-time GPS location tracking with automatic QR code regeneration on every new position fix
- QR code generated using the `geo:<lat>,<lng>` URI standard (opens in Google Maps and compatible apps)
- Latitude, longitude and accuracy (in metres) displayed below the QR code

**Share & Export**
- Share button — sends the QR image and the `geo:` link via the Android share sheet
- Save button — exports a timestamped PNG to `Pictures/GeoFlash/` via MediaStore (no storage permission required on Android 10+)

**UX**
- Runtime location permission request (fine + coarse)
- Graceful GPS-disabled state with a direct link to system location settings
- Loading indicator while waiting for the first GPS fix
- Edge-to-edge layout with Material 3 and dynamic colour theming
- Support dialog shown on first launch (YouTube & SoundCloud links)
- Support dialog accessible at any time via the ♥ icon in the top bar

**Technical**
- Kotlin 2.2 · Jetpack Compose · Material 3
- Google Play Services Location 21.3
- ZXing Core 3.5.3 for QR bitmap generation
- AGP 9.2.1 · compileSdk 36 (Android 16) · minSdk 26 (Android 8.0)
