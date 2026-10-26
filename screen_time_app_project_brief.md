# Screen-Time App — AI Coding Spec

## Goal
Build a polished, Android-first app that helps users spend less time in distracting apps. Keep the MVP local-first: no account, backend, cloud sync, ads, or analytics SDK.

## Recommended stack
- **Kotlin + Jetpack Compose + Material 3** for the UI.
- **Room** for app rules and usage/session history; **DataStore** for preferences.
- **UsageStatsManager / UsageEvents** for usage totals and usage reconciliation.
- **AccessibilityService** for foreground-app detection and rule-based interventions, only after clear in-app disclosure and explicit consent.
- Android notifications for reminders. Use Android's system Photo Picker for user-selected photos.

Use Flutter only if cross-platform support becomes a real requirement; Android usage monitoring and interventions would still need native Kotlin integration.

## Required features

### 1. Per-app limits
Users can select installed apps and configure rules independently:
- **Daily limit:** total allowed foreground usage per local calendar day.
- **Per-session limit:** allowed time for each opening, e.g. Instagram for 5 minutes every time it is opened.
- **Session reminders:** remind the user at a customizable interval while an app is open, e.g. every 5 minutes. The interval must be independent of the session limit and configurable per app.
- **Pre-limit warnings:** optional reminders before session or daily limits expire.
- Enable/pause, edit, and delete every rule.
- Display today's use, session time remaining, and daily time remaining.

Track only foreground use. Leaving an app ends its current session; opening it again starts a fresh session timer. Daily usage accumulates across sessions. If both session and daily limits are enabled, intervene when either limit is exhausted. Avoid duplicate reminders and restore/reconcile state after a process restart.

### 2. App-opening reflection screen
For each selected app, let the user enable an interruption when opening it:
- Show a full-screen reflection screen with a **quote, selected photo, or both**.
- Support built-in quotes, user-written quotes, and photos selected from the device.
- Let the user customize the delay/countdown before **Continue** becomes available.
- Provide clear actions such as **Close app / Go back** and **Continue**.
- Let users choose whether to show this on every opening or at a configurable frequency.
- Keep settings independent per app, with a global default.

**Platform caveat:** A normal Android app cannot guarantee that it will intercept another app before that app begins opening. Implement and test the most reliable user-consented interruption possible. Do not promise a guaranteed pre-launch gate. If reliable interception is not possible on a device, use a clear reflection/intervention flow and a Home action rather than pretending the app was blocked before launch.

### 3. Custom intervention messages
Allow users to write and save messages displayed when:
- a session reminder occurs;
- a session limit expires;
- a daily limit expires;
- the app-opening reflection screen appears.

Support a global default, per-app overrides, and an optional pool of messages selected randomly. Include editable example messages, but do not force users to use motivational language. Make empty-message behavior sensible (use a neutral default rather than a blank screen).

### 4. Color themes
Provide a theme picker in Settings. Each theme must consistently style the dashboard, timers, charts, settings, notifications where supported, and reflection/intervention screens.

Required preset palettes:
- **Pink + Light Blue** — soft pink surfaces with light-blue accents; calm, friendly, airy.
- **Black + Yellow + Red** — dark/black surfaces, yellow highlights, red for limit warnings.
- **Green + White** — clean white surfaces with green accents.

Also support system light/dark appearance where practical. Maintain readable contrast, accessible text sizes, and clear status colors in every theme. Save the selected theme. Custom user-defined palettes are optional, not required for the MVP.

## Main screens
1. **Today:** today's usage, progress toward goals, remaining limits, and quick actions.
2. **App limits:** configured apps and their session/daily rules.
3. **Choose apps:** searchable list of launchable installed apps with app icon and name.
4. **Edit app rule:** session limit, daily limit, reminder interval, warning thresholds, opening reflection, and custom messages.
5. **Insights:** simple daily/weekly usage charts.
6. **Settings:** theme picker, permissions/service status, privacy information, and data controls.
7. **Reflection/intervention:** quote/photo, optional countdown, custom message, and Close/Continue actions.

Use simple bottom navigation for Today, Limits, Insights, and Settings. Make the UI feel premium and intentional: clear type hierarchy, generous spacing, rounded cards, restrained motion, polished empty/loading/error states, and no decorative clutter. Avoid nonfunctional placeholder buttons.

## Android constraints and privacy
- Usage statistics require the user to grant Usage Access in Android Settings.
- Accessibility Service must be explained before the user is sent to Settings. For Google Play distribution, follow the current Accessibility API disclosure, consent, and declaration requirements.
- An ordinary app cannot truly force-stop another app. At an exhausted limit, use a supported intervention such as sending the user to Home and re-intervening if they reopen the app. Never claim to kill/force-stop apps.
- Never prevent users from disabling the service, pausing rules, or uninstalling the app.
- Request only permissions needed by implemented features. Use the system Photo Picker instead of broad photo-library access when possible.
- Test permission revocation, Accessibility Service disabled, process death, reboot, and background restrictions on real devices. Usage summaries alone are not guaranteed real-time enforcement; validate enforcement separately.

Official references:
- [Jetpack Compose](https://developer.android.com/develop/ui/compose)
- [UsageStatsManager](https://developer.android.com/reference/android/app/usage/UsageStatsManager)
- [AccessibilityService](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)
- [Google Play Accessibility API policy](https://support.google.com/googleplay/android-developer/answer/10964491)

## MVP acceptance criteria
- Users can select an installed app and create, edit, pause, and delete its rules.
- Per-session timers reset on each new opening; daily totals aggregate across sessions and reset at the local day boundary.
- Reminder intervals and countdown/delay settings are customizable and work independently of the session limit.
- Quotes, photos, custom messages, and theme selections persist after app restart.
- Custom messages can be configured globally and overridden per app/event.
- Pink + Light Blue, Black + Yellow + Red, and Green + White themes cover all primary screens and intervention UI with readable contrast.
- Missing/revoked permissions are clearly explained with a route to the relevant Settings screen.
- No fabricated usage totals, misleading force-stop claims, or controls that do nothing.

## Build order
1. Implement the design system and primary screens using realistic preview data.
2. Add theme persistence, app selection, local storage, and rule editing.
3. Implement session/daily calculations and reminders with unit tests.
4. Prototype foreground detection and the reflection/intervention flow on real devices before polishing secondary features.
5. Add insights, permission recovery, and final accessibility/UI polish.
