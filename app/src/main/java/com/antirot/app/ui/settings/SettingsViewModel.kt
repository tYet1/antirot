package com.antirot.app.ui.settings

import android.app.AppOpsManager
import android.content.Context
import android.os.Process
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antirot.app.data.local.preferences.UserPreferencesManager
import com.antirot.app.data.repository.AppRuleRepository
import com.antirot.app.data.repository.UsageRepository
import com.antirot.app.service.AntirotAccessibilityService
import com.antirot.app.ui.theme.ThemePreset
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: UserPreferencesManager,
    private val appRuleRepository: AppRuleRepository,
    private val usageRepository: UsageRepository,
) : ViewModel() {

    val themePreset: StateFlow<ThemePreset> = preferencesManager.themePresetFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemePreset.PINK_LIGHT_BLUE)

    val darkMode: StateFlow<String> = preferencesManager.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "SYSTEM")

    val isAccessibilityServiceRunning: StateFlow<Boolean> = AntirotAccessibilityService.isRunning

    private val _isUsageAccessGranted = MutableStateFlow(false)
    val isUsageAccessGranted: StateFlow<Boolean> = _isUsageAccessGranted.asStateFlow()

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
        val mode = appOps?.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
        _isUsageAccessGranted.value = (mode == AppOpsManager.MODE_ALLOWED)
    }

    fun setThemePreset(preset: ThemePreset) {
        viewModelScope.launch {
            preferencesManager.setThemePreset(preset)
        }
    }

    fun setDarkMode(mode: String) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(mode)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            usageRepository.clearAllHistory()
            preferencesManager.clearPreferences()
        }
    }
}
