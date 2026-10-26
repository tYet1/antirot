package com.antirot.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// =============================================================================
// Preset 1: Pink + Light Blue
// Soft pink surfaces with light-blue accents; calm, friendly, airy.
// =============================================================================

val PinkLightBlueLightColors = lightColorScheme(
    primary = Color(0xFF007A99),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6F3FF),
    onPrimaryContainer = Color(0xFF003645),
    secondary = Color(0xFF9E4767),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E2),
    onSecondaryContainer = Color(0xFF3E001D),
    tertiary = Color(0xFF00838F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFB4F1F9),
    onTertiaryContainer = Color(0xFF00363C),
    background = Color(0xFFFFF7F8),
    onBackground = Color(0xFF2B151C),
    surface = Color(0xFFFFF0F3),
    onSurface = Color(0xFF2B151C),
    surfaceVariant = Color(0xFFF7D8E0),
    onSurfaceVariant = Color(0xFF564047),
    outline = Color(0xFF887077),
    outlineVariant = Color(0xFFDAC0C8),
)

val PinkLightBlueDarkColors = darkColorScheme(
    primary = Color(0xFF7AD5F8),
    onPrimary = Color(0xFF003544),
    primaryContainer = Color(0xFF004D63),
    onPrimaryContainer = Color(0xFFC0ECFF),
    secondary = Color(0xFFFFAFD0),
    onSecondary = Color(0xFF5C1133),
    secondaryContainer = Color(0xFF7D2C4D),
    onSecondaryContainer = Color(0xFFFFD9E2),
    tertiary = Color(0xFF80D4E0),
    onTertiary = Color(0xFF00363C),
    tertiaryContainer = Color(0xFF004F57),
    onTertiaryContainer = Color(0xFFB4F1F9),
    background = Color(0xFF190C11),
    onBackground = Color(0xFFF7DEE4),
    surface = Color(0xFF201117),
    onSurface = Color(0xFFF7DEE4),
    surfaceVariant = Color(0xFF3D2A31),
    onSurfaceVariant = Color(0xFFD5BAC2),
    outline = Color(0xFF9E848C),
    outlineVariant = Color(0xFF513D45),
)

val PinkLightBlueSemanticLight = SemanticColors(
    success = Color(0xFF2E7D32),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFC8E6C9),
    onSuccessContainer = Color(0xFF0B3810),
    warning = Color(0xFFD84315),
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFFFCCBC),
    onWarningContainer = Color(0xFF4A1000),
    danger = Color(0xFFC2185B),
    onDanger = Color(0xFFFFFFFF),
    dangerContainer = Color(0xFFF8BBD0),
    onDangerContainer = Color(0xFF490020),
    timerTrack = Color(0x26007A99),
    timerProgress = Color(0xFF007A99),
    timerWarning = Color(0xFFC2185B),
)

val PinkLightBlueSemanticDark = SemanticColors(
    success = Color(0xFF81C784),
    onSuccess = Color(0xFF0B3810),
    successContainer = Color(0xFF1B5E20),
    onSuccessContainer = Color(0xFFA5D6A7),
    warning = Color(0xFFFFAB91),
    onWarning = Color(0xFF4A1000),
    warningContainer = Color(0xFFBF360C),
    onWarningContainer = Color(0xFFFFCCBC),
    danger = Color(0xFFF48FB1),
    onDanger = Color(0xFF490020),
    dangerContainer = Color(0xFF880E4F),
    onDangerContainer = Color(0xFFF8BBD0),
    timerTrack = Color(0x337AD5F8),
    timerProgress = Color(0xFF7AD5F8),
    timerWarning = Color(0xFFF48FB1),
)

// =============================================================================
// Preset 2: Black + Yellow + Red
// Dark/black surfaces, yellow highlights, red for limit warnings.
// =============================================================================

val BlackYellowRedDarkColors = darkColorScheme(
    primary = Color(0xFFFFD600),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF423700),
    onPrimaryContainer = Color(0xFFFFE66B),
    secondary = Color(0xFFFF5252),
    onSecondary = Color(0xFF450004),
    secondaryContainer = Color(0xFF6B000B),
    onSecondaryContainer = Color(0xFFFFDAD8),
    tertiary = Color(0xFFFFB300),
    onTertiary = Color(0xFF382300),
    tertiaryContainer = Color(0xFF573900),
    onTertiaryContainer = Color(0xFFFFDE9F),
    background = Color(0xFF0E0E0E),
    onBackground = Color(0xFFF0F0F0),
    surface = Color(0xFF161616),
    onSurface = Color(0xFFF0F0F0),
    surfaceVariant = Color(0xFF262626),
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = Color(0xFF666666),
    outlineVariant = Color(0xFF333333),
)

val BlackYellowRedLightColors = lightColorScheme(
    primary = Color(0xFF7A6400),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE373),
    onPrimaryContainer = Color(0xFF261D00),
    secondary = Color(0xFFBA1A1A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDAD6),
    onSecondaryContainer = Color(0xFF410002),
    tertiary = Color(0xFF7B5800),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDEA1),
    onTertiaryContainer = Color(0xFF271900),
    background = Color(0xFFFCFCFC),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFF4F4F4),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE5E5E5),
    onSurfaceVariant = Color(0xFF4D4D4D),
    outline = Color(0xFF7A7A7A),
    outlineVariant = Color(0xFFC4C4C4),
)

val BlackYellowRedSemanticDark = SemanticColors(
    success = Color(0xFF69F0AE),
    onSuccess = Color(0xFF00381B),
    successContainer = Color(0xFF00522B),
    onSuccessContainer = Color(0xFF94F8B8),
    warning = Color(0xFFFFD600),
    onWarning = Color(0xFF000000),
    warningContainer = Color(0xFF423700),
    onWarningContainer = Color(0xFFFFE66B),
    danger = Color(0xFFFF5252),
    onDanger = Color(0xFF000000),
    dangerContainer = Color(0xFF6B000B),
    onDangerContainer = Color(0xFFFFDAD8),
    timerTrack = Color(0x33FFD600),
    timerProgress = Color(0xFFFFD600),
    timerWarning = Color(0xFFFF5252),
)

val BlackYellowRedSemanticLight = SemanticColors(
    success = Color(0xFF008940),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFF96F8B9),
    onSuccessContainer = Color(0xFF00210C),
    warning = Color(0xFF7A6400),
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFFFE373),
    onWarningContainer = Color(0xFF261D00),
    danger = Color(0xFFBA1A1A),
    onDanger = Color(0xFFFFFFFF),
    dangerContainer = Color(0xFFFFDAD6),
    onDangerContainer = Color(0xFF410002),
    timerTrack = Color(0x267A6400),
    timerProgress = Color(0xFF7A6400),
    timerWarning = Color(0xFFBA1A1A),
)

// =============================================================================
// Preset 3: Green + White
// Clean white surfaces with crisp green accents.
// =============================================================================

val GreenWhiteLightColors = lightColorScheme(
    primary = Color(0xFF1B6C3B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB5F4C7),
    onPrimaryContainer = Color(0xFF00210C),
    secondary = Color(0xFF386A48),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFBAF0C7),
    onSecondaryContainer = Color(0xFF00210E),
    tertiary = Color(0xFF006875),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF97F0FF),
    onTertiaryContainer = Color(0xFF001F24),
    background = Color(0xFFFCFDFC),
    onBackground = Color(0xFF181D19),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF181D19),
    surfaceVariant = Color(0xFFDFE6DE),
    onSurfaceVariant = Color(0xFF414A43),
    outline = Color(0xFF727A72),
    outlineVariant = Color(0xFFC2C9C2),
)

val GreenWhiteDarkColors = darkColorScheme(
    primary = Color(0xFF78DB97),
    onPrimary = Color(0xFF003919),
    primaryContainer = Color(0xFF005327),
    onPrimaryContainer = Color(0xFF94F8B1),
    secondary = Color(0xFF9ED4AC),
    onSecondary = Color(0xFF02391C),
    secondaryContainer = Color(0xFF1F5131),
    onSecondaryContainer = Color(0xFFBAF0C7),
    tertiary = Color(0xFF50D7EC),
    onTertiary = Color(0xFF00363E),
    tertiaryContainer = Color(0xFF004E58),
    onTertiaryContainer = Color(0xFF97F0FF),
    background = Color(0xFF0F1511),
    onBackground = Color(0xFFDFE4DE),
    surface = Color(0xFF141C16),
    onSurface = Color(0xFFDFE4DE),
    surfaceVariant = Color(0xFF263329),
    onSurfaceVariant = Color(0xFFBFC9BF),
    outline = Color(0xFF8B948B),
    outlineVariant = Color(0xFF3E4A40),
)

val GreenWhiteSemanticLight = SemanticColors(
    success = Color(0xFF1B6C3B),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFB5F4C7),
    onSuccessContainer = Color(0xFF00210C),
    warning = Color(0xFFC04F00),
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFFFDCC7),
    onWarningContainer = Color(0xFF3E1500),
    danger = Color(0xFFBA1A1A),
    onDanger = Color(0xFFFFFFFF),
    dangerContainer = Color(0xFFFFDAD6),
    onDangerContainer = Color(0xFF410002),
    timerTrack = Color(0x261B6C3B),
    timerProgress = Color(0xFF1B6C3B),
    timerWarning = Color(0xFFBA1A1A),
)

val GreenWhiteSemanticDark = SemanticColors(
    success = Color(0xFF78DB97),
    onSuccess = Color(0xFF003919),
    successContainer = Color(0xFF005327),
    onSuccessContainer = Color(0xFF94F8B1),
    warning = Color(0xFFFFB68C),
    onWarning = Color(0xFF542200),
    warningContainer = Color(0xFF783300),
    onWarningContainer = Color(0xFFFFDCC7),
    danger = Color(0xFFFFB4AB),
    onDanger = Color(0xFF690005),
    dangerContainer = Color(0xFF93000A),
    onDangerContainer = Color(0xFFFFDAD6),
    timerTrack = Color(0x3378DB97),
    timerProgress = Color(0xFF78DB97),
    timerWarning = Color(0xFFFFB4AB),
)
