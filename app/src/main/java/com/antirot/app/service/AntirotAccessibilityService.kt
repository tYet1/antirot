package com.antirot.app.service

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import com.antirot.app.data.local.entity.AppRuleEntity
import com.antirot.app.data.repository.AppRuleRepository
import com.antirot.app.data.repository.UsageRepository
import com.antirot.app.ui.intervention.InterventionActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AntirotAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var appRuleRepository: AppRuleRepository

    @Inject
    lateinit var usageRepository: UsageRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var sessionJob: Job? = null

    private var activePackage: String? = null
    private var sessionStartTime: Long = 0L
    private var lastReminderTime: Long = 0L
    private var activeRule: AppRuleEntity? = null

    // Track the last foreground package to prevent intra-app re-triggers
    private var lastKnownForeground: String? = null

    companion object {
        private const val TAG = "AntirotA11yService"
        const val CHANNEL_REMINDERS = "antirot_reminders"
        const val NOTIFICATION_ID_REMINDER = 1001

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        /**
         * Timestamp of when the user tapped Continue.
         * Suppresses re-triggering for CONTINUE_GRACE_MS after Continue.
         */
        val continuedAt = ConcurrentHashMap<String, Long>()
        const val CONTINUE_GRACE_MS = 10_000L

        private val NOISE_PACKAGES = setOf(
            "com.android.systemui",
            "com.android.launcher",
            "com.google.android.apps.nexuslauncher",
            "com.sec.android.app.launcher",
            "com.miui.home",
            "com.huawei.android.launcher",
            "com.oneplus.launcher",
            "com.oppo.launcher",
        )
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        _isRunning.value = true
        createNotificationChannel()
        Log.d(TAG, "AntirotAccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return
        val cls = event.className?.toString()

        // Filter own package — do NOT update lastKnownForeground so when intervention closes,
        // returning to the monitored app doesn't look like a new app launch.
        if (pkg == packageName) return

        // Filter noise packages
        if (pkg in NOISE_PACKAGES) return
        if (cls != null && cls.contains("InputMethod", ignoreCase = true)) return

        val prev = lastKnownForeground
        lastKnownForeground = pkg

        if (prev != pkg) {
            handlePackageTransition(previousPackage = prev, newPackage = pkg)
        }
    }

    private fun handlePackageTransition(previousPackage: String?, newPackage: String) {
        serviceScope.launch {
            // End previous session if there was one
            endCurrentSession(previousPackage)

            // Look up rules for newPackage
            val rule = appRuleRepository.getRule(newPackage)
            if (rule != null && !rule.isPaused) {
                activePackage = newPackage
                activeRule = rule
                sessionStartTime = System.currentTimeMillis()
                lastReminderTime = sessionStartTime

                // Check grace period
                val lastContinued = continuedAt[newPackage] ?: 0L
                val inGrace = (System.currentTimeMillis() - lastContinued) < CONTINUE_GRACE_MS

                // Check reflection if enabled and not in grace period
                if (rule.reflectionEnabled && !inGrace) {
                    launchIntervention(
                        packageName = newPackage,
                        appName = rule.appName,
                        reason = InterventionActivity.REASON_REFLECTION,
                        delaySeconds = rule.reflectionDelaySeconds,
                        reflectionType = rule.reflectionType,
                        photoUri = rule.reflectionPhotoUri,
                        customMessage = rule.customMessage,
                    )
                }

                // Start active session monitor job
                startSessionMonitor(rule)
            } else {
                activePackage = null
                activeRule = null
            }
        }
    }

    private fun startSessionMonitor(rule: AppRuleEntity) {
        sessionJob?.cancel()
        sessionJob = serviceScope.launch {
            while (isActive && activePackage == rule.packageName) {
                delay(1_000L)
                val now = System.currentTimeMillis()
                val sessionElapsedMinutes = ((now - sessionStartTime) / 60_000L).toInt()

                // Check session limit
                if (rule.sessionLimitMinutes > 0 && sessionElapsedMinutes >= rule.sessionLimitMinutes) {
                    val lastContinued = continuedAt[rule.packageName] ?: 0L
                    if (now - lastContinued >= CONTINUE_GRACE_MS) {
                        launchIntervention(
                            packageName = rule.packageName,
                            appName = rule.appName,
                            reason = InterventionActivity.REASON_SESSION_LIMIT,
                            delaySeconds = 0,
                            reflectionType = rule.reflectionType,
                            photoUri = rule.reflectionPhotoUri,
                            customMessage = rule.customMessage,
                        )
                        break
                    }
                }

                // Check daily limit
                if (rule.dailyLimitMinutes > 0) {
                    val recordedTodayMs = usageRepository.getPackageTodayUsageSync(rule.packageName)
                    val currentSessionMs = now - sessionStartTime
                    val totalTodayMinutes = ((recordedTodayMs + currentSessionMs) / 60_000L).toInt()
                    if (totalTodayMinutes >= rule.dailyLimitMinutes) {
                        val lastContinued = continuedAt[rule.packageName] ?: 0L
                        if (now - lastContinued >= CONTINUE_GRACE_MS) {
                            launchIntervention(
                                packageName = rule.packageName,
                                appName = rule.appName,
                                reason = InterventionActivity.REASON_DAILY_LIMIT,
                                delaySeconds = 0,
                                reflectionType = rule.reflectionType,
                                photoUri = rule.reflectionPhotoUri,
                                customMessage = rule.customMessage,
                            )
                            break
                        }
                    }
                }

                // Check reminders
                if (rule.reminderIntervalMinutes > 0) {
                    val reminderElapsedMinutes = ((now - lastReminderTime) / 60_000L).toInt()
                    if (reminderElapsedMinutes >= rule.reminderIntervalMinutes) {
                        lastReminderTime = now
                        postReminderNotification(rule.appName, sessionElapsedMinutes)
                    }
                }
            }
        }
    }

    private suspend fun endCurrentSession(pkg: String?) {
        sessionJob?.cancel()
        sessionJob = null
        if (pkg != null && sessionStartTime > 0L) {
            val endTime = System.currentTimeMillis()
            if (endTime > sessionStartTime) {
                usageRepository.recordSession(pkg, sessionStartTime, endTime)
            }
            sessionStartTime = 0L
        }
    }

    private fun launchIntervention(
        packageName: String,
        appName: String,
        reason: String,
        delaySeconds: Int,
        reflectionType: String,
        photoUri: String?,
        customMessage: String?,
    ) {
        val intent = Intent(this, InterventionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            putExtra(InterventionActivity.EXTRA_PACKAGE, packageName)
            putExtra(InterventionActivity.EXTRA_APP_NAME, appName)
            putExtra(InterventionActivity.EXTRA_REASON, reason)
            putExtra(InterventionActivity.EXTRA_DELAY_SECONDS, delaySeconds)
            putExtra(InterventionActivity.EXTRA_REFLECTION_TYPE, reflectionType)
            putExtra(InterventionActivity.EXTRA_PHOTO_URI, photoUri)
            putExtra(InterventionActivity.EXTRA_CUSTOM_MESSAGE, customMessage)
        }
        startActivity(intent)
    }

    private fun postReminderNotification(appName: String, elapsedMinutes: Int) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val notification = NotificationCompat.Builder(this, CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$appName Reminder")
            .setContentText("You have been using $appName for $elapsedMinutes minutes.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_REMINDER, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Session Reminders",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Reminders while actively using an app"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        _isRunning.value = false
        sessionJob?.cancel()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        _isRunning.value = false
        serviceScope.cancel()
        super.onDestroy()
    }
}
