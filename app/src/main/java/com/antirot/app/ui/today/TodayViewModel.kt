package com.antirot.app.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antirot.app.data.local.entity.AppRuleEntity
import com.antirot.app.data.repository.AppRuleRepository
import com.antirot.app.data.repository.UsageRepository
import com.antirot.app.service.AntirotAccessibilityService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MonitoredAppProgress(
    val rule: AppRuleEntity,
    val usageMinutes: Int,
    val progressRatio: Float,
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val appRuleRepository: AppRuleRepository,
    private val usageRepository: UsageRepository,
) : ViewModel() {

    val isServiceRunning: StateFlow<Boolean> = AntirotAccessibilityService.isRunning

    val rules: StateFlow<List<AppRuleEntity>> = appRuleRepository.allRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _appsProgress = MutableStateFlow<List<MonitoredAppProgress>>(emptyList())
    val appsProgress: StateFlow<List<MonitoredAppProgress>> = _appsProgress.asStateFlow()

    private val _totalScreenTimeMinutes = MutableStateFlow(0)
    val totalScreenTimeMinutes: StateFlow<Int> = _totalScreenTimeMinutes.asStateFlow()

    fun refreshUsage() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentRules = appRuleRepository.getActiveRulesSync()
            var totalMins = 0

            val progressList = currentRules.map { rule ->
                // Check usage from our db or system usage stats
                val dbUsage = usageRepository.getPackageTodayUsageSync(rule.packageName)
                val sysUsage = usageRepository.querySystemDailyUsageMs(rule.packageName)
                val effectiveMs = maxOf(dbUsage, sysUsage)
                val minutes = (effectiveMs / 60_000L).toInt()
                totalMins += minutes

                val limit = if (rule.dailyLimitMinutes > 0) rule.dailyLimitMinutes else rule.sessionLimitMinutes
                val ratio = if (limit > 0) (minutes.toFloat() / limit.toFloat()).coerceIn(0f, 1f) else 0f

                MonitoredAppProgress(
                    rule = rule,
                    usageMinutes = minutes,
                    progressRatio = ratio,
                )
            }

            _appsProgress.value = progressList
            _totalScreenTimeMinutes.value = totalMins
        }
    }

    fun toggleAllPaused(allPaused: Boolean) {
        viewModelScope.launch {
            appRuleRepository.setAllPaused(!allPaused)
            refreshUsage()
        }
    }
}
