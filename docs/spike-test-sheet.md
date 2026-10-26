# Spike Test Sheet — Foreground Detection & Intervention

**Branch:** `spike/foreground`  
**APK:** `spike/app/build/outputs/apk/debug/app-debug.apk`  
**Purpose:** Decide between Approach A (Activity) and Approach B (Overlay) for M5 interventions.

---

## Pre-conditions

- Install the APK via `adb install -r app-debug.apk` (or Android Studio Run).
- Have at least one of the hardcoded target apps installed:
  - Instagram (`com.instagram.android`)
  - Twitter / X (`com.twitter.android`)
  - TikTok (`com.zhiliaoapp.musically`)
- If none of these are installed, note it — the event log will still show package names for any app you switch to; only the intervention won't trigger.

---

## Setup steps (do once per device)

1. Open **Antirot Spike** on the device.
2. Tap **"Open Accessibility Settings"**.
3. Find **"Antirot Foreground Watcher"** in the list and enable it. Accept the system warning.
4. Return to the app — the **Accessibility** chip must turn green ("✓ Accessibility ON").
5. Tap **"Grant Usage Access"** (only shown if not already granted).
6. Find **"Antirot Spike"** in Usage Access settings, enable it.
7. Return — the **Usage Access** chip must turn green.

---

## Test cases

### TC-01 — Event log (A11Y source)

| Step | Action | Expected |
|------|--------|----------|
| 1 | Open any non-target app (e.g. Chrome, Settings) | A new `[A11Y]` row appears in the log with the correct package name |
| 2 | Open the device keyboard in any text field | A row with `[A11Y]` and an IME class name appears; it should be filtered (not trigger intervention) |
| 3 | Swipe to Home screen | A launcher package row appears (noise source visible) |
| 4 | Switch back to Antirot Spike | Own package row appears; no intervention |

**Pass criteria:** All expected rows appear; own app / launcher / IME rows are distinct noise entries and do **not** trigger intervention.

---

### TC-02 — Usage Access log (USAGE source)

| Step | Action | Expected |
|------|--------|----------|
| 1 | With Usage Access granted, switch between 3 apps | `[USAGE[RESUMED]]` and `[USAGE[PAUSED]]` rows appear within ~2 s |
| 2 | Note which appears first vs A11Y row for the same event | Record latency difference in the Results table |

**Pass criteria:** USAGE rows appear; compare latency with TC-01 A11Y rows for same events.

---

### TC-03 — Approach A intervention (Activity)

| Step | Action | Expected |
|------|--------|----------|
| 1 | Confirm toggle is set to **A** (right side of switch) | Mode label reads "Mode A: Activity…" |
| 2 | Open a hardcoded target app (Instagram / Twitter / TikTok) | Within ~1 s a dark full-screen **"⏱ Reflection"** screen appears over the target app |
| 3 | Observe countdown | Counter ticks 5→4→3→2→1→"Ready!" |
| 4 | Tap **Continue** (only enabled after countdown) | Reflection screen dismisses; you land in the target app |
| 5 | Press Back from the reflection screen mid-countdown | Back press does nothing (overridden) |
| 6 | Open the target app again immediately | Intervention fires again |
| 7 | Tap **Go Home** during countdown | Device returns to Home screen; no crash |

**Record:** latency from opening the target app to seeing the reflection screen (estimate).

---

### TC-04 — Approach B intervention (Overlay)

| Step | Action | Expected |
|------|--------|----------|
| 1 | Toggle switch to **B** (left side) | Mode label reads "Mode B: AccessibilityOverlay…" |
| 2 | Open a target app | Within ~1 s a dark overlay appears **on top of** the target app |
| 3 | Countdown ticks 5→1→"Ready!" | Verify text updates correctly |
| 4 | Tap **Continue** | Overlay dismisses; you see the target app underneath |
| 5 | Tap **Go Home** | Device goes home; overlay dismissed |
| 6 | Switch to a non-target app | Overlay dismisses (if it was showing) |
| 7 | Re-open the target app | Overlay re-appears |

**Record:** latency, any visual glitches, whether target app content is visible through overlay.

---

### TC-05 — Service status tracking

| Step | Action | Expected |
|------|--------|----------|
| 1 | Disable the Accessibility service in Settings | **Accessibility chip** turns red within ~2 s |
| 2 | Re-enable the service | Chip turns green again |
| 3 | Kill the Antirot Spike app from Recents while service is on | Log file at `Android/data/com.antirot.spike/files/spike-events.log` should persist entries from before kill |

---

### TC-06 — Log file persistence

| Step | Action | Expected |
|------|--------|----------|
| 1 | Generate some events (switch apps) | Rows appear on screen |
| 2 | Force-stop Antirot Spike | App closes |
| 3 | Pull log via `adb pull /sdcard/Android/data/com.antirot.spike/files/spike-events.log` | File contains all events logged before the kill |

---

## Results table

| Device | Android ver. | TC | Approach | Result | Notes |
|--------|--------------|----|----------|--------|-------|
| User's device | unknown | TC-01 | — | **PASS** | A11Y event log working correctly |
| User's device | unknown | TC-02 | — | **PASS** | USAGE log entries appearing |
| User's device | unknown | TC-03 | A | **PARTIAL** | Intervention fires; **Continue button broken** — after finish() the target app re-foregrounds and triggers a new intervention immediately (re-trigger loop) |
| User's device | unknown | TC-04 | B | **PARTIAL** | Overlay appears; **see-through** (background not fully opaque); **does not trigger when app is resumed from Recents** (TYPE_WINDOW_STATE_CHANGED not fired on Recents resume) |
| User's device | unknown | TC-05 | — | **PASS** | Service status chip updates correctly |
| User's device | unknown | TC-06 | — | **PASS** | Log file persists after force-stop |

## What to report back — answers received

1. **Approach A latency:** Not measured precisely; intervention fires quickly.
2. **Approach A Continue:** Broken — `finish()` causes target app to foreground → service re-fires immediately → new 5-second countdown starts. Fix: suppress re-trigger for ~10 s after Continue.
3. **Approach B see-through:** Background color `0xDD000000` (87% alpha) is not fully opaque. Fix: use `0xFF000000`.
4. **Approach B Recents:** `TYPE_WINDOW_STATE_CHANGED` is not fired when an app is resumed from the Recents screen. Needs `TYPE_WINDOWS_CHANGED` or UsageEvents polling to catch this path.
5. **Service status:** Tracked correctly.
