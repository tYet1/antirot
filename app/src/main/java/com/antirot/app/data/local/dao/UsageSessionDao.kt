package com.antirot.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.antirot.app.data.local.entity.UsageSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageSessionDao {

    @Insert
    suspend fun insertSession(session: UsageSessionEntity): Long

    @Query("SELECT * FROM usage_sessions WHERE dayKey = :dayKey ORDER BY startTime DESC")
    fun observeSessionsForDay(dayKey: String): Flow<List<UsageSessionEntity>>

    @Query("SELECT * FROM usage_sessions WHERE packageName = :packageName AND dayKey = :dayKey")
    suspend fun getSessionsForPackageAndDay(packageName: String, dayKey: String): List<UsageSessionEntity>

    @Query(
        "SELECT COALESCE(SUM(durationMs), 0) FROM usage_sessions " +
            "WHERE packageName = :packageName AND dayKey = :dayKey",
    )
    fun observeTotalDurationForPackage(packageName: String, dayKey: String): Flow<Long>

    @Query(
        "SELECT COALESCE(SUM(durationMs), 0) FROM usage_sessions " +
            "WHERE packageName = :packageName AND dayKey = :dayKey",
    )
    suspend fun getTotalDurationForPackage(packageName: String, dayKey: String): Long

    @Query("SELECT COALESCE(SUM(durationMs), 0) FROM usage_sessions WHERE dayKey = :dayKey")
    fun observeTotalDurationForDay(dayKey: String): Flow<Long>

    @Query(
        "SELECT dayKey, COALESCE(SUM(durationMs), 0) as totalMs FROM usage_sessions " +
            "GROUP BY dayKey ORDER BY dayKey DESC LIMIT 7",
    )
    fun observeWeeklyDailyTotals(): Flow<List<DailyTotal>>

    @Query("DELETE FROM usage_sessions")
    suspend fun clearAll()
}

data class DailyTotal(
    val dayKey: String,
    val totalMs: Long,
)
