# PLAN.md: Screen-Time App Execution Plan

**Source of truth:** `screen_time_app_project_brief.md`. Background and rationale: `screen_time_app_workflow.md`.
**This file is the agent's working plan.** Follow it top to bottom, and keep it updated.

---

## 0. Operating protocol (the agent MUST follow this)

1. Work on **one task at a time**, in ID order, unless a task says it can run in parallel.
2. Before starting a task: set its status to `[~]` (in progress). When finished and verified: set `[x]`. If blocked: `[!]` plus a one-line reason under it.
3. A task is done only when its **Done when** line is true AND `./gradlew ktlintCheck testDebugUnitTest assembleDebug` passes (from M1 onward).
4. At the end of each milestone: stop, summarise in the **Progress log** (bottom of this file), list anything deferred, and **wait for my confirmation** before the next milestone.
5. Items marked **🧑 HUMAN** need a real device or my decision. Do not fake or simulate them. Write exact instructions for me and stop.
6. If a task reveals the plan or brief is wrong, do not silently deviate. Add an entry to **Decisions & deviations** and ask me.
7. Hard rules (never violate):
   - No network permission, analytics, ads, or accounts.
   - No `QUERY_ALL_PACKAGES`; use a `<queries>` launcher intent.
   - UI copy never says an app was "blocked", "killed" or "force-stopped". Use "taking you home".
   - No button or setting that does nothing. Hide it until wired.
   - Domain logic is pure Kotlin with an injectable `Clock`; no `System.currentTimeMillis()` outside `core/clock`.
   - Request only permissions needed by implemented features.
   - Never obstruct the user from disabling the service, pausing rules, or uninstalling.
8. Prefer small commits, one per task: `T-xx: short description`.
9. Keep dependencies minimal and pin versions in a version catalog (`libs.versions.toml`).

Status legend: `[ ]` todo · `[~]` in progress · `[x]` done · `[!]` blocked

---

## M0: Feasibility spike (branch `spike/foreground`, throwaway)

Goal: prove foreground detection and an intervention overlay work on real devices before building anything else.

- [x] **T-00.1** Minimal Compose project with an `AccessibilityService` that logs package changes from `TYPE_WINDOW_STATE_CHANGED` to an on-screen list and a log file.
  *Done when:* switching apps adds correct entries; own app, keyboard and system UI are visible as separate noise sources in the log.
- [x] **T-00.2** Button that deep-links to Accessibility settings; status text showing whether the service is enabled.
  *Done when:* status updates after enabling/disabling.
- [x] **T-00.3** Hardcoded target list. When a target package is foregrounded, show an intervention via **Approach A** (Activity started from the service, `singleTask`, excluded from recents) with a 5 s countdown and Continue / Go Home buttons.
- [x] **T-00.4** Same behaviour via **Approach B** (accessibility overlay window, `TYPE_ACCESSIBILITY_OVERLAY`). Toggle between A and B in the spike UI.
- [x] **T-00.5** `UsageEvents` logger (ACTIVITY_RESUMED/PAUSED) behind a Usage Access check, to compare against accessibility events.
- [x] **T-00.6** 🧑 HUMAN: I test on real device(s). Agent produces `docs/spike-test-sheet.md` with exact steps and a results table (device, Android version, approach, latency, issues).
- [x] **T-00.7** Agent writes `docs/decisions/001-intervention.md` choosing A or B (or A with B fallback), with evidence from the test sheet.

**Gate M0:** decision recorded and I confirm. The spike branch is not merged.

---

## M1: Foundation & design system

- [x] **T-01.1** Create the production project: single module, package-by-feature (`core/ data/ domain/ service/ ui/`), Hilt, Room, DataStore, Compose M3, ktlint, version catalog, min SDK 26.
  *Done when:* builds, and `./gradlew ktlintCheck testDebugUnitTest assembleDebug` runs green.
- [x] **T-01.2** Theme system: `AppTheme` with three presets (Pink + Light Blue, Black + Yellow + Red, Green + White), each with light/dark variants where sensible, plus semantic tokens (`success`, `warning`, `danger`, `timerTrack`).
- [ ] **T-01.3** Typography scale, shape and spacing tokens.
- [ ] **T-01.4** Components: `AppCard`, `ProgressRing`, `LimitProgressBar`, `StatTile`, `SectionHeader`, `EmptyState`, `ErrorState`, `PrimaryButton/SecondaryButton`.
- [ ] **T-01.5** Preview gallery showing every component in every theme.
- [ ] **T-01.6** Contrast audit script or test: every text/background pair in every theme against WCAG AA. Output `docs/contrast-report.md`; fix failures.

**Gate M1:** previews render in all themes; contrast report has no AA failures.

---

## M2: Screens with fake data

Use in-memory fake repositories behind interfaces (`FakeRuleRepository`, etc.).

- [ ] **T-02.1** Bottom navigation (Today, Limits, Insights, Settings) with state-preserving navigation.
- [ ] **T-02.2** **Today:** total usage, per-app progress, session/daily time remaining, quick actions.
- [ ] **T-02.3** **App limits:** list of configured apps with status chip, pause toggle, FAB to add.
- [ ] **T-02.4** **Choose apps:** searchable list with icon and name (fake list for now), multi-select or single-select flow.
- [ ] **T-02.5** **Edit app rule:** session limit, daily limit, reminder interval, pre-limit warning threshold, reflection settings, message overrides, pause/delete.
- [ ] **T-02.6** **Insights:** daily and weekly charts (simple Compose canvas bars, no heavy chart library).
- [ ] **T-02.7** **Settings:** theme picker (live preview), permission/service status section (stub states), privacy info, data controls (stubs hidden until wired).
- [ ] **T-02.8** **Reflection/intervention screen:** quote/photo/both, countdown, Close/Continue, themed.
- [ ] **T-02.9** Loading, empty and error states for every list screen.

**Gate M2:** every screen reachable and themed in all three themes; no dead buttons.

---

## M3: Persistence & real app selection

- [ ] **T-03.1** Room schema: `AppRule`, `MessageEntry`, `UsageSession`, `DailyUsage`, `Quote`. Export schema JSON; set up migration scaffolding.
- [ ] **T-03.2** DataStore: theme, global defaults (reflection delay/frequency, grace period, snooze limits), consent and onboarding flags.
- [ ] **T-03.3** Real repositories replacing fakes; ViewModels use `StateFlow`.
- [ ] **T-03.4** Installed-app listing via `<queries>` launcher intent; icon loading with an in-memory cache; exclude our own app.
- [ ] **T-03.5** Rule CRUD (create/edit/pause/delete) fully wired to the UI.
- [ ] **T-03.6** Messages: global default, per-app override, per-event (reminder / session limit / daily limit / reflection), random pool, editable built-in examples, neutral fallback when empty.
- [ ] **T-03.7** Quotes: built-in set + user-written; photos via system Photo Picker, **copied into app-private storage**; store the file path.
- [ ] **T-03.8** DAO instrumented tests and repository tests.

**Gate M3:** 🧑 HUMAN: I confirm rules, theme, messages, quotes and photos survive killing and restarting the app.

---

## M4: Domain engine (pure Kotlin, test-first)

Write tests **before** implementation. Target ≥90% coverage on `domain/`.

- [ ] **T-04.1** `Clock` interface + `FakeClock`; `DayKey` helper (local date, handles timezone and DST).
- [ ] **T-04.2** `SessionEngine` state machine. Inputs: `ForegroundChanged(pkg?)`, `ScreenOff`, `ScreenOn`, `Tick`, `DayChanged`, `ServiceDisconnected`. Ignores own package, system UI, IME, launcher. Configurable grace period.
- [ ] **T-04.3** Tests: session resets on reopen after grace; same session within grace; screen-off ends the session; session split at midnight; timezone/clock change.
- [ ] **T-04.4** `LimitEvaluator`: session and daily limits, earliest-wins when both are enabled, remaining-time calculations.
- [ ] **T-04.5** `ReminderPlanner`: interval independent of session limit, pre-limit warnings, **dedupe keys** (`pkg+dayKey+event+bucket`) that survive simulated process death.
- [ ] **T-04.6** `MessageResolver`: precedence (app+event, then global+event, then neutral default); random pool without immediate repeat.
- [ ] **T-04.7** `ReflectionPolicy`: every opening / every Nth opening; per-app over global default.
- [ ] **T-04.8** `SnoozePolicy`: bounded snoozes per app per day.
- [ ] **T-04.9** Coverage report committed to `docs/coverage.md`.

**Gate M4:** all domain tests green; coverage ≥90%.

---

## M5: Service integration & interventions

- [ ] **T-05.1** Consent flow: prominent in-app disclosure (what is read, that nothing leaves the device, how to turn it off) shown **before** opening Accessibility settings; consent persisted.
- [ ] **T-05.2** `AppWatcherAccessibilityService` feeding `SessionEngine`; persists ticks and sessions to Room; handles `onInterrupt` / `onUnbind`.
- [ ] **T-05.3** Intervention controller using the approach chosen in M0 (reflection, session reminder, session limit, daily limit). Hook up the message resolver and theme.
- [ ] **T-05.4** At an exhausted limit: `GLOBAL_ACTION_HOME` + message; re-intervene on reopen. Snooze option per policy. Copy follows the hard rules.
- [ ] **T-05.5** Notifications: channels, `POST_NOTIFICATIONS` runtime request in context, graceful fallback to in-overlay reminders.
- [ ] **T-05.6** Usage Access flow plus `UsageEvents` reconciliation on service start and after boot (`BOOT_COMPLETED` receiver only if needed).
- [ ] **T-05.7** Reliability screen: status of Usage Access, Accessibility, notifications, battery optimization, with deep links; OEM guidance text.
- [ ] **T-05.8** Revocation handling: clear recovery UI for each missing permission or disabled service.
- [ ] **T-05.9** Replace stubs on Today/Settings with real data. No fabricated totals anywhere.
- [ ] **T-05.10** 🧑 HUMAN: run `docs/test-checklist.md` on ≥2 real devices; agent fixes failures I report.

**Gate M5:** checklist passes on both devices, or known issues are documented and accepted by me.

---

## M6: Insights, polish, release readiness

- [ ] **T-06.1** Insights wired to real session data (daily and weekly, per-app).
- [ ] **T-06.2** Accessibility pass: TalkBack labels, 48 dp touch targets, font scale up to 200%, reduced-motion respect.
- [ ] **T-06.3** Settings JSON export/import (local file via the system file picker).
- [ ] **T-06.4** "Delete all data" control with confirmation.
- [ ] **T-06.5** Visual polish pass: spacing, type hierarchy, restrained motion, consistent empty states.
- [ ] **T-06.6** Cleanup: dead code, unused permissions/dependencies, lint warnings.
- [ ] **T-06.7** Play-readiness notes: Accessibility API disclosure text, privacy policy draft, data-safety answers in `docs/play-readiness.md`.
- [ ] **T-06.8** Final run of the MVP acceptance criteria from the brief, with pass/fail per line in `docs/acceptance.md`.

**Gate M6 (Definition of Done):** every MVP acceptance criterion passes; domain tests green; manifest has only needed permissions; no UI copy claims blocking or killing apps.

---

## Backlog (do NOT start without my approval)
Schedules / time windows · app groups with shared limits · home-screen widget (Glance) · "Why are you opening this?" prompt · weekly review notification · custom user-defined palettes · streaks.

---

## Risk register (agent: check before each milestone)

| Risk | Mitigation | Owner |
|------|-----------|-------|
| Overlay or activity launch blocked on newer Android | M0 spike on real devices; keep both approaches until decided | M0 |
| OEM kills the service | Reliability screen + guidance; test on at least one aggressive-OEM device | M5 |
| Event noise causing false session resets | Ignore list + grace period, covered by tests | M4 |
| Clock/timezone changes corrupt totals | `DayKey` + midnight split tests | M4 |
| Play policy rejection (Accessibility API) | Prominent disclosure, consent, honest copy | M5/M6 |
| Scope creep | Backlog is frozen until M6 passes | all |

---

## Decisions & deviations
*(agent appends: date · task ID · decision · reason)*

- 2026-10-26 · T-00.1..T-00.5 · Used `compileSdk=34` (not 35) because only `android-34` and `android-36` are installed on this machine; android-35 is absent. Compose BOM downgraded from 2024.09.01 → 2024.06.00 to stay compatible with AGP 8.5.2. Added `gradle.properties` with `android.useAndroidX=true` (missing from initial scaffold). Android theme changed from `Theme.Material3.DayNight.NoActionBar` (not available as XML) to `@android:style/Theme.NoTitleBar` — both activities use Compose `setContent` so the host theme only needs to suppress the default title bar.
- 2026-10-26 · T-00.3 (post-device-test fix) · Three bugs fixed: (1) Added `lastKnownForeground` tracking — only trigger intervention when package changes FROM a different app; fixes intra-app navigation re-trigger (Instagram DMs → Feed) and the Continue re-trigger loop simultaneously. (2) `continuedAt` grace map (10 s) as belt-and-suspenders. (3) Changed `InterventionActivity` from `singleTask`/`taskAffinity=""` to `singleInstance`/`taskAffinity="com.antirot.spike.intervention"` to prevent target app's task preempting the intervention on Recents resume. Approach B overlay changed to `PixelFormat.OPAQUE` + `0xFF1A1A2E` background.

## Progress log
*(agent appends at each milestone end: date · milestone · summary · deferred items · test results)*

- 2026-10-26 · M0 (in progress) · T-00.1–T-00.5 done and committed (a4c39a3). APK builds clean (35 tasks, 0 errors, 1 deprecation warning for `checkOpNoThrow` in UsageLogger — harmless, API 26+). T-00.6 is HUMAN — waiting for device test results before writing the intervention decision doc (T-00.7).
- 2026-10-26 · M0 COMPLETE · T-00.6 real-device results received. T-00.7 decision written. Decision: **Approach A (Activity) primary** with 3 required fixes for M5 — (1) continue-grace suppression to break re-trigger loop, (2) fully opaque overlay background, (3) subscribe to `typeWindowsChanged` to catch Recents-resume path. Approach B not needed for MVP. Spike branch stays unmerged. Gate: awaiting user confirmation to proceed to M1.
- 2026-10-26 · M0 GATE PASSED · Post-device-test fixes applied and confirmed working by user: Approach A fires once on app open, no intra-app re-triggers, Continue works, Recents resume works. All three fixes (prev-pkg guard, continuedAt grace, singleInstance taskAffinity) committed at 2effe82. M1 starting.
