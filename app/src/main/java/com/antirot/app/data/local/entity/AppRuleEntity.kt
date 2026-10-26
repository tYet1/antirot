package com.antirot.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_rules")
data class AppRuleEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Int = 0,
    val sessionLimitMinutes: Int = 0,
    val reminderIntervalMinutes: Int = 0,
    val warningThresholdMinutes: Int = 0,
    val reflectionEnabled: Boolean = false,
    val reflectionDelaySeconds: Int = 5,
    val reflectionType: String = "QUOTE",
    val reflectionPhotoUri: String? = null,
    val customMessage: String? = null,
    val isPaused: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
