package com.antirot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.antirot.app.data.local.preferences.UserPreferencesManager
import com.antirot.app.data.repository.QuoteRepository
import com.antirot.app.ui.AntirotNavHost
import com.antirot.app.ui.theme.AntirotTheme
import com.antirot.app.ui.theme.ThemePreset
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: UserPreferencesManager

    @Inject
    lateinit var quoteRepository: QuoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Seed default quotes if needed
        CoroutineScope(Dispatchers.IO).launch {
            quoteRepository.seedDefaultsIfNeeded()
        }

        setContent {
            val preset by preferencesManager.themePresetFlow.collectAsState(initial = ThemePreset.PINK_LIGHT_BLUE)

            AntirotTheme(preset = preset) {
                AntirotNavHost()
            }
        }
    }
}
