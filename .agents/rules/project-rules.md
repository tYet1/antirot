# Project rules (always on)

Source of truth: @screen_time_app_project_brief.md
Execution plan: @PLAN.md (follow its operating protocol in section 0 exactly)

## Hard rules
- Kotlin + Jetpack Compose + Material 3, Room, DataStore, Hilt, Coroutines/Flow. Min SDK 26.
- No network permission, analytics, ads, accounts, or cloud sync.
- No QUERY_ALL_PACKAGES. List apps with a <queries> launcher intent.
- UI copy must never say an app was "blocked", "killed" or "force-stopped". Say "taking you home".
- No button or setting that does nothing. Hide it until it is wired.
- Domain logic lives in domain/ as pure Kotlin with an injectable Clock. Never call System.currentTimeMillis() outside core/clock.
- Write domain unit tests before the implementation.
- Request only permissions needed by implemented features.
- Never obstruct the user from disabling the service, pausing rules, or uninstalling.
- Never fake or simulate results that need a real device. Write instructions for me and stop.
- One task at a time, in PLAN.md order. Update checkboxes as you go.
- Stop at the end of every milestone and at every task marked HUMAN, and wait for my confirmation.
- If the plan or brief seems wrong, log it under "Decisions & deviations" in PLAN.md and ask me. Do not silently deviate.
- After each task from M1 onward, run: ./gradlew ktlintCheck testDebugUnitTest assembleDebug and report the result honestly.
