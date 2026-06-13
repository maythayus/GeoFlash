# GeoFlash

> Instantly generate a geolocated QR code from your current GPS position.

GeoFlash is a free Android app that captures your real-time GPS coordinates and encodes them into a shareable QR code using the standard `geo:` URI format. Built with Kotlin and Jetpack Compose.

---

## Features

- **Real-time GPS tracking** — QR code regenerates automatically on every new location fix
- **geo: URI standard** — QR codes open directly in Google Maps and any compatible app
- **Share** — Send the QR image + geo link via any Android app (WhatsApp, SMS, email…)
- **Save to gallery** — Exports a PNG to `Pictures/GeoFlash/` without requiring storage permission (Android 10+)
- **Accuracy display** — Shows latitude, longitude and GPS accuracy in metres
- **Permission handling** — Graceful prompt for location access; redirects to settings if GPS is disabled
- **Edge-to-edge UI** — Full Material 3 design with dynamic theming

---

## Screenshots

> _(coming soon)_

---

## Tech Stack

| Layer | Library / Tool |
|---|---|
| Language | Kotlin 2.2 |
| UI | Jetpack Compose + Material 3 |
| Location | Google Play Services Location 21.3 |
| QR generation | ZXing Core 3.5.3 |
| Build system | Gradle 9 · AGP 9.2.1 |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 36 (Android 16) |

---

## Getting Started

### Prerequisites

- Android Studio Meerkat or later
- Android SDK 36+
- A physical device or emulator with GPS/network location

### Clone & Build

```bash
git clone https://github.com/maythayus/GeoFlash.git
cd GeoFlash
```

Open the project in Android Studio, let Gradle sync, then run on a device or emulator.

### Permissions

GeoFlash requests the following permissions at runtime:

| Permission | Purpose |
|---|---|
| `ACCESS_FINE_LOCATION` | Precise GPS coordinates |
| `ACCESS_COARSE_LOCATION` | Fallback network location |

---

## How It Works

1. On launch, the app requests location permission.
2. Once granted, it subscribes to live location updates via `LocationManager`.
3. Each new GPS fix is encoded as `geo:<lat>,<lng>` and rendered as a 260 dp QR bitmap using ZXing.
4. The user can **share** the image + link or **save** it to the gallery with a single tap.

---

## Support the Project

GeoFlash is free and open source. If you find it useful, consider supporting the developer:

- 📺 [YouTube — @maythayus1](https://www.youtube.com/@maythayus1?sub_confirmation=1)
- 🎵 [SoundCloud](https://soundcloud.com/user-807863479-448305626)

---

## License

This project is released under the [MIT License](LICENSE).
