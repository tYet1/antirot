# Decision 001 — Intervention Approach for Production App

**Date:** 2026-10-26  
**Task:** T-00.7  
**Evidence:** `docs/spike-test-sheet.md` (real-device test, branch `spike/foreground`)

---

## Decision

**Use Approach A (Activity) as the primary intervention method, with two required bug fixes, plus Approach B (Overlay) as a targeted supplement for the Recents-resume path only.**

---

## Evidence summary

| Criterion | Approach A (Activity) | Approach B (Overlay) |
|-----------|----------------------|----------------------|
| Fires on normal app open | ✅ Yes | ✅ Yes |
| Fires on Recents resume | ✅ Yes (see fix below) | ❌ No — `TYPE_WINDOW_STATE_CHANGED` not fired |
| Continue button works | ❌ Broken — re-trigger loop | ✅ Works |
| Opaque background | ✅ Fully opaque | ❌ See-through (87% alpha) |
| Back press blocked | ✅ (onBackPressed no-op) | ❌ Not applicable |
| No SYSTEM_ALERT_WINDOW needed | ✅ | ❌ Requires it |
| Works in Compose | ✅ Native | ⚠️ Programmatic View only |
| Play Store risk | Low — standard Activity | Medium — requires SAW permission disclosure |

---

## Root causes and fixes required before M5

### Fix 1 — Approach A: Re-trigger loop on Continue (CRITICAL)

**Root cause:** When `InterventionActivity.finish()` is called after Continue, the target app returns to the foreground. The service receives a new `TYPE_WINDOW_STATE_CHANGED` event for that package and immediately fires a new intervention.

**Fix (M5):** Add a per-package "just-continued" suppression map in `WatcherAccessibilityService` (or the domain `SessionEngine`). After Continue is pressed, record `pkg → System.currentTimeMillis()`. In `onAccessibilityEvent`, skip triggering an intervention if the package was continued within a configurable grace window (default: 10 seconds). This also becomes the foundation for the `SnoozePolicy` in M4.

```
// Pseudocode for M5:
val continuedAt: MutableMap<String, Long> = mutableMapOf()
const val CONTINUE_GRACE_MS = 10_000L

fun onAccessibilityEvent(event) {
    val pkg = event.packageName ?: return
    val now = clock.now()
    if (continuedAt[pkg]?.let { now - it < CONTINUE_GRACE_MS } == true) return
    // ... rest of logic
}
```

### Fix 2 — Approach B: Semi-transparent overlay (MINOR)

**Root cause:** `setBackgroundColor(0xDD000000.toInt())` uses alpha = 0xDD (87%). Because `PixelFormat.TRANSLUCENT` is set, the compositor blends the window with content underneath.

**Fix (M5):** Use `0xFF000000.toInt()` (fully opaque black) and change `PixelFormat.TRANSLUCENT` → `PixelFormat.OPAQUE`. Since this is a reflection screen (not a notification), full opacity is correct.

### Fix 3 — Recents resume not detected by either approach (IMPORTANT)

**Root cause:** Android does not fire `TYPE_WINDOW_STATE_CHANGED` when an app is resumed from the Recents/Overview screen — it fires `TYPE_WINDOWS_CHANGED` instead. Neither approach intercepts this path reliably.

**Fix (M5):** Subscribe to **both** `typeWindowStateChanged` AND `typeWindowsChanged` in the `AccessibilityServiceInfo`. In `onAccessibilityEvent`, handle `TYPE_WINDOWS_CHANGED` by inspecting `windows` for the newly active window and extracting its `rootInActiveWindow?.packageName`. This covers the Recents path. The `SessionEngine` will deduplicate rapid events via the grace period and ignore-list.

```xml
<!-- accessibility_service_config.xml in production app -->
android:accessibilityEventTypes="typeWindowStateChanged|typeWindowsChanged"
```

---

## Chosen architecture for M5

```
TYPE_WINDOW_STATE_CHANGED  ──┐
                              ├──► WatcherAccessibilityService.onAccessibilityEvent()
TYPE_WINDOWS_CHANGED       ──┘         │
                                       ▼
                              PackageExtractor (noise filter, ignore-list)
                                       │
                                       ▼
                              SessionEngine.onForegroundChanged(pkg)
                                       │
                              ┌────────┴────────┐
                              ▼                 ▼
                         ReflectionPolicy   LimitEvaluator
                              │                 │
                              └────────┬────────┘
                                       ▼
                            InterventionController
                                       │
                              ┌────────┴────────┐
                              ▼                 ▼
                    Approach A Activity   (B overlay only if
                    [primary — M5]         SAW granted + Recents
                                           path detected — optional)
```

**For MVP (M5):** Implement Approach A only with all three fixes above. Approach B overlay is **not needed** for MVP — the Recents path will be covered by `TYPE_WINDOWS_CHANGED` feeding Approach A.

**For post-MVP (backlog):** Revisit Approach B as an optional "instant overlay" mode for users who grant SYSTEM_ALERT_WINDOW, if testing shows Approach A has unacceptable latency on specific OEMs.

---

## What this means for M1–M4

- **M1–M3:** No change. Build the design system, screens, and persistence without any real intervention.
- **M4:** `SessionEngine` must accept both `ForegroundChanged` and handle the grace-period suppression. `SnoozePolicy` should model the same concept as the continue-grace window.
- **M5:** Implement Approach A only. Subscribe to both event types. Add continue-grace suppression. Opaque background for the reflection screen. No SYSTEM_ALERT_WINDOW requested at this stage.
