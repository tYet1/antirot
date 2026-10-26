package com.antirot.app.ui.limits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antirot.app.core.apps.AppInfo
import com.antirot.app.core.apps.InstalledAppsManager
import com.antirot.app.data.local.entity.AppRuleEntity
import com.antirot.app.data.repository.AppRuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LimitsViewModel @Inject constructor(
    private val appRuleRepository: AppRuleRepository,
    private val installedAppsManager: InstalledAppsManager,
) : ViewModel() {

    val rules: StateFlow<List<AppRuleEntity>> = appRuleRepository.allRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredApps: StateFlow<List<AppInfo>> = combine(_installedApps, _searchQuery) { apps, query ->
        if (query.isBlank()) {
            apps
        } else {
            apps.filter {
                it.appName.contains(query, ignoreCase = true) ||
                    it.packageName.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val apps = installedAppsManager.getInstalledLaunchableApps()
            _installedApps.value = apps
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun togglePause(rule: AppRuleEntity) {
        viewModelScope.launch {
            appRuleRepository.setRulePaused(rule.packageName, !rule.isPaused)
        }
    }

    fun deleteRule(packageName: String) {
        viewModelScope.launch {
            appRuleRepository.deleteRule(packageName)
        }
    }

    fun saveRule(rule: AppRuleEntity) {
        viewModelScope.launch {
            appRuleRepository.saveRule(rule)
        }
    }

    suspend fun getRule(packageName: String): AppRuleEntity? {
        return appRuleRepository.getRule(packageName)
    }

    fun getAppInfo(packageName: String): AppInfo? {
        return installedAppsManager.getAppInfo(packageName)
    }
}
