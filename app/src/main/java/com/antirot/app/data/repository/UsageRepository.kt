package com.antirot.app.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import com.antirot.app.core.clock.Clock
import com.antirot.app.data.local.dao.DailyTotal
import com.antirot.app.data.local.dao.UsageSessionDao
import com.antirot.app.data.local.entity.UsageSessionEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class UsageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usageSessionDao: UsageSessionDao,
    private val clock: Clock,
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayKey(): String {
        return dateFormat.format(Date(clock.now()))
    }

    suspend fun recordSession(packageName: String, startTime: Long, endTime: Long): Long {
        val duration = (endTime - startTime).coerceAtLeast(0)
        val dayKey = dateFormat.format(Date(startTime))
        val session = UsageSessionEntity(
            packageName = packageName,
            startTime = startTime,
            endTime = endTime,
            durationMs = duration,
            dayKey = dayKey,
        )
        return usageSessionDao.insertSession(session)
    }

    fun observeTodayTotal(): Flow<Long> {
        return usageSessionDao.observeTotalDurationForDay(getTodayKey())
    }

    fun observePackageTodayUsage(packageName: String): Flow<Long> {
        return usageSessionDao.observeTotalDurationForPackage(packageName, getTodayKey())
    }

    suspend fun getPackageTodayUsageSync(packageName: String): Long {
        return usageSessionDao.getTotalDurationForPackage(packageName, getTodayKey())
    }

    fun observeWeeklyTotals(): Flow<List<DailyTotal>> {
        return usageSessionDao.observeWeeklyDailyTotals()
    }

    /**
     * Query UsageStatsManager for accurate daily total if permission is granted.
     */
    fun querySystemDailyUsageMs(packageName: String): Long {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return 0L
        val calendar = Calendar.getInstance().apply {
            timeInMillis = clock.now()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = clock.now()

        return try {
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
            val appStat = stats?.firstOrNull { it.packageName == packageName }
            appStat?.totalTimeInForeground ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    suspend fun clearAllHistory() {
        usageSessionDao.clearAll()
    }
}
