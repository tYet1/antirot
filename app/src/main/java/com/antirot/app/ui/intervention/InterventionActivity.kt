package com.antirot.app.ui.intervention

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.antirot.app.data.local.preferences.UserPreferencesManager
import com.antirot.app.data.repository.QuoteRepository
import com.antirot.app.service.AntirotAccessibilityService
import com.antirot.app.ui.components.PrimaryButton
import com.antirot.app.ui.components.SecondaryButton
import com.antirot.app.ui.theme.AntirotTheme
import com.antirot.app.ui.theme.ThemePreset
import com.antirot.app.ui.theme.spacing
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay

@AndroidEntryPoint
class InterventionActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: UserPreferencesManager

    @Inject
    lateinit var quoteRepository: QuoteRepository

    companion object {
        const val EXTRA_PACKAGE = "extra_package"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_REASON = "extra_reason"
        const val EXTRA_DELAY_SECONDS = "extra_delay_seconds"
        const val EXTRA_REFLECTION_TYPE = "extra_reflection_type"
        const val EXTRA_PHOTO_URI = "extra_photo_uri"
        const val EXTRA_CUSTOM_MESSAGE = "extra_custom_message"

        const val REASON_REFLECTION = "REFLECTION"
        const val REASON_SESSION_LIMIT = "SESSION_LIMIT"
        const val REASON_DAILY_LIMIT = "DAILY_LIMIT"
    }

    private var intentVersion by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bindContent()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intentVersion++
    }

    private fun bindContent() {
        setContent {
            val preset by preferencesManager.themePresetFlow.collectAsState(initial = ThemePreset.PINK_LIGHT_BLUE)

            AntirotTheme(preset = preset) {
                key(intentVersion) {
                    val pkg = intent.getStringExtra(EXTRA_PACKAGE) ?: ""
                    val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "App"
                    val reason = intent.getStringExtra(EXTRA_REASON) ?: REASON_REFLECTION
                    val delaySeconds = intent.getIntExtra(EXTRA_DELAY_SECONDS, 5)
                    val reflectionType = intent.getStringExtra(EXTRA_REFLECTION_TYPE) ?: "QUOTE"
                    val photoUri = intent.getStringExtra(EXTRA_PHOTO_URI)
                    val customMessage = intent.getStringExtra(EXTRA_CUSTOM_MESSAGE)

                    InterventionContent(
                        packageName = pkg,
                        appName = appName,
                        reason = reason,
                        delaySeconds = delaySeconds,
                        reflectionType = reflectionType,
                        photoUri = photoUri,
                        customMessage = customMessage,
                        quoteRepository = quoteRepository,
                        onContinue = {
                            AntirotAccessibilityService.continuedAt[pkg] = System.currentTimeMillis()
                            finish()
                        },
                        onGoHome = {
                            AntirotAccessibilityService.continuedAt[pkg] = System.currentTimeMillis()
                            finish()
                            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(homeIntent)
                        },
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Suppress back-press during intervention
    }
}

@Composable
private fun InterventionContent(
    packageName: String,
    appName: String,
    reason: String,
    delaySeconds: Int,
    reflectionType: String,
    photoUri: String?,
    customMessage: String?,
    quoteRepository: QuoteRepository,
    onContinue: () -> Unit,
    onGoHome: () -> Unit,
) {
    val context = LocalContext.current
    var secondsRemaining by remember { mutableIntStateOf(delaySeconds) }
    var isUnlocked by remember { mutableStateOf(delaySeconds <= 0) }

    var quoteText by remember { mutableStateOf("Pause and breathe.") }
    var quoteAuthor by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val q = quoteRepository.getRandomQuote()
        quoteText = q.text
        quoteAuthor = q.author
    }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
        isUnlocked = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = MaterialTheme.spacing.extraLarge),
            ) {
                Icon(
                    imageVector = when (reason) {
                        InterventionActivity.REASON_SESSION_LIMIT,
                        InterventionActivity.REASON_DAILY_LIMIT,
                        -> Icons.Default.HourglassBottom
                        else -> Icons.Default.Spa
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp),
                )

                Spacer(Modifier.height(MaterialTheme.spacing.small))

                Text(
                    text = when (reason) {
                        InterventionActivity.REASON_SESSION_LIMIT -> "Session limit reached"
                        InterventionActivity.REASON_DAILY_LIMIT -> "Daily limit reached"
                        else -> "Pause & reflect"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Opening $appName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            // Body: Quote / Photo / Custom Message
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Photo if present
                if ((reflectionType == "PHOTO" || reflectionType == "BOTH") && !photoUri.isNullOrBlank()) {
                    val bitmap = remember(photoUri) {
                        try {
                            context.contentResolver.openInputStream(Uri.parse(photoUri))?.use {
                                BitmapFactory.decodeStream(it)
                            }
                        } catch (_: Exception) {
                            null
                        }
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Reflection Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    }
                }

                // Quote if selected
                if (reflectionType == "QUOTE" || reflectionType == "BOTH") {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(MaterialTheme.spacing.medium),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp),
                            )
                            Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))
                            Text(
                                text = "“$quoteText”",
                                style = MaterialTheme.typography.bodyLarge,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            if (quoteAuthor.isNotBlank()) {
                                Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))
                                Text(
                                    text = "— $quoteAuthor",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }

                // Custom message override if provided
                if (!customMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    Text(
                        text = customMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            // Countdown & Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!isUnlocked) {
                    Text(
                        text = "Continue available in ${secondsRemaining}s",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                }

                PrimaryButton(
                    text = if (isUnlocked) "Continue to $appName" else "Wait (${secondsRemaining}s)",
                    onClick = onContinue,
                    enabled = isUnlocked,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(MaterialTheme.spacing.small))

                SecondaryButton(
                    text = "Taking you home",
                    onClick = onGoHome,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
