package com.antirot.app.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.antirot.app.ui.theme.ThemePreset
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "antirot_preferences")

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private val KEY_THEME_PRESET = stringPreferencesKey("theme_preset")
        private val KEY_DARK_MODE = stringPreferencesKey("dark_mode")
        private val KEY_REFLECTION_DELAY = intPreferencesKey("reflection_delay")
        private val KEY_GLOBAL_MESSAGE = stringPreferencesKey("global_message")
        private val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    }

    val themePresetFlow: Flow<ThemePreset> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_THEME_PRESET] ?: ThemePreset.PINK_LIGHT_BLUE.name
        try {
            ThemePreset.valueOf(name)
        } catch (_: Exception) {
            ThemePreset.PINK_LIGHT_BLUE
        }
    }

    val darkModeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: "SYSTEM"
    }

    val globalMessageFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_GLOBAL_MESSAGE] ?: "Take a deep breath. Is this what you intended to do?"
    }

    suspend fun setThemePreset(preset: ThemePreset) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_PRESET] = preset.name
        }
    }

    suspend fun setDarkMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = mode
        }
    }

    suspend fun setGlobalMessage(message: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_GLOBAL_MESSAGE] = message
        }
    }

    suspend fun clearPreferences() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
