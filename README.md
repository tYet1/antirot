# Antirot 🛑📱

A modern, offline-first Android application designed to curb digital brainrot and mindless scrolling. Antirot helps you reclaim your focus through conscious friction, mindful app-opening reflections, and customizable usage limits—all without sending a single byte of your data to the cloud.

---

## ✨ Features

- **🛡️ 100% Offline & Private:** Zero network permissions requested. No telemetry, no accounts, no analytics SDKs, and no cloud sync. All your rules and session history remain strictly on your device.
- **⏱️ Per-App Session Limits:** Enforce maximum continuous usage sessions (e.g. 5m, 15m, 30m) on distracting apps.
- **📅 Daily Time Budgets:** Set cumulative daily limits per app that automatically accumulate across sessions and reset at local midnight.
- **🔔 Periodic Session Reminders:** Non-intrusive reminders while using an app (e.g., every 5 minutes) independent of hard session limits.
- **🧘 Mindful App-Opening Reflections:** Interrupt the autopilot reflex when opening apps with full-screen reflection prompts:
  - Motivational/stoic quotes (built-in and custom).
  - Personal photos selected via Android's native system Photo Picker.
  - Configurable countdown delay before the "Continue" action unlocks.
  - "Taking you home" shortcut to immediately exit back to your home launcher.
- **📊 Usage Insights & Analytics:** 7-day screen time trend charts drawn natively on Compose Canvas, along with daily averages and top-used app metrics.
- **🎨 Custom Design System (WCAG AA Compliant):**
  - Built with Jetpack Compose & Material 3.
  - Three distinct theme presets with full Light and Dark mode support:
    - **Pink + Light Blue:** Soft rose backgrounds with light-blue accents.
    - **Black + Yellow + Red:** High-contrast dark surfaces with amber yellow and warning red.
    - **Green + White:** Crisp emerald accents with clean light surfaces.
  - Verified 90/90 color pairs passing WCAG 2.1 Level AA contrast standards ($\ge 4.5:1$).

---

## 🏗️ Architecture & Tech Stack

Antirot follows modern Android development practices and the Single-Module / Package-by-Feature pattern:

- **Language:** Kotlin 2.0.21
- **UI Framework:** Jetpack Compose + Material 3 (Compose BOM `2024.06.00`)
- **Dependency Injection:** Hilt 2.52
- **Database & Persistence:** Room 2.6.1 (`antirot.db`) + Jetpack DataStore Preferences
- **Background & Monitoring:** 
  - `AccessibilityService` for low-latency, battery-efficient foreground app transitions and rule evaluation.
  - Native `UsageStatsManager` integration for system-accurate daily screen time reconciliation.
- **Code Quality & Verification:** `ktlint`, JUnit unit tests, and WCAG contrast audits.

```
com.antirot.app/
├── core/            # Injectable Clock abstractions and installed apps manager
├── data/            # Room entities, DAOs, Database, DataStore preferences & repositories
├── service/         # AntirotAccessibilityService & session tracking engine
└── ui/
    ├── components/  # Core design system (AppCard, ProgressRing, Buttons, States)
    ├── insights/    # Usage charts & analytics
    ├── intervention/# Reflection & limit enforcement activity
    ├── limits/      # App limit configuration, app chooser & editor
    ├── settings/    # Themes, permissions & data controls
    ├── theme/       # Color palettes, typography, shapes & spacing tokens
    └── today/       # Daily dashboard & monitored app progress
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio:** Hedgehog (2023.1.1) or newer (Ladybug / Iguana recommended)
- **Android SDK:** Compile SDK 34, Min SDK 26 (Android 8.0 Oreo+)
- **JDK:** Java 17 or Java 21

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/antirot.git
   cd antirot
   ```
2. Open the project in Android Studio:
   - Select **File → Open...** and choose the root directory (`antirot`).
3. Build the debug APK using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
4. Run all code style and unit test checks:
   ```bash
   ./gradlew ktlintCheck testDebugUnitTest
   ```

### Installing on a Device via ADB

Make sure USB/Wireless debugging is enabled on your device:
```bash
./gradlew installDebug
```
Or manually install the built APK:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔒 Permissions & Privacy Disclosure

Antirot was built from day one to respect user privacy:

| Permission | Purpose |
|---|---|
| `BIND_ACCESSIBILITY_SERVICE` | Detects when a configured app enters the foreground to enforce reflection prompts and session limits. **Never reads or transmits screen content or keystrokes.** |
| `PACKAGE_USAGE_STATS` | Optional permission to query daily screentime totals from the Android OS. |
| `POST_NOTIFICATIONS` | Delivers periodic session interval reminders while apps are active (Android 13+). |
| `<queries>` Launcher Intent | Enumerates installed launchable apps so you can select which apps to monitor without needing the dangerous `QUERY_ALL_PACKAGES` permission. |

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
