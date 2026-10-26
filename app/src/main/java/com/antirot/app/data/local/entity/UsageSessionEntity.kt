package com.antirot.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usage_sessions",
    indices = [
        Index("packageName"),
        Index("dayKey"),
    ],
)
data class UsageSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val startTime: Long,
    val endTime: Long,
    val durationMs: Long,
    val dayKey: String,
)
