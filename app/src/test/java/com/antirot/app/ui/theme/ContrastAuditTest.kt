package com.antirot.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import org.junit.Assert.assertTrue
import org.junit.Test

class ContrastAuditTest {

    data class ContrastCheck(
        val themeName: String,
        val pairName: String,
        val textColor: Color,
        val backgroundColor: Color,
        val ratio: Double,
        val requiredRatio: Double = 4.5,
        val passed: Boolean = ratio >= requiredRatio,
    )

    private fun linearize(channel: Float): Double = if (channel <= 0.04045f) {
        (channel / 12.92).toDouble()
    } else {
        ((channel + 0.055) / 1.055).toDouble().pow(2.4)
    }

    private fun relativeLuminance(color: Color): Double {
        val r = linearize(color.red)
        val g = linearize(color.green)
        val b = linearize(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun contrastRatio(c1: Color, c2: Color): Double {
        val l1 = relativeLuminance(c1)
        val l2 = relativeLuminance(c2)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun checkColorScheme(
        themeName: String,
        scheme: ColorScheme,
        semantic: SemanticColors,
    ): List<ContrastCheck> {
        val pairs = listOf(
            Triple("onPrimary / primary", scheme.onPrimary, scheme.primary),
            Triple("onPrimaryContainer / primaryContainer", scheme.onPrimaryContainer, scheme.primaryContainer),
            Triple("onSecondary / secondary", scheme.onSecondary, scheme.secondary),
            Triple("onSecondaryContainer / secondaryContainer", scheme.onSecondaryContainer, scheme.secondaryContainer),
            Triple("onTertiary / tertiary", scheme.onTertiary, scheme.tertiary),
            Triple("onTertiaryContainer / tertiaryContainer", scheme.onTertiaryContainer, scheme.tertiaryContainer),
            Triple("onBackground / background", scheme.onBackground, scheme.background),
            Triple("onSurface / surface", scheme.onSurface, scheme.surface),
            Triple("onSurfaceVariant / surfaceVariant", scheme.onSurfaceVariant, scheme.surfaceVariant),
            Triple("onSuccess / success", semantic.onSuccess, semantic.success),
            Triple("onSuccessContainer / successContainer", semantic.onSuccessContainer, semantic.successContainer),
            Triple("onWarning / warning", semantic.onWarning, semantic.warning),
            Triple("onWarningContainer / warningContainer", semantic.onWarningContainer, semantic.warningContainer),
            Triple("onDanger / danger", semantic.onDanger, semantic.danger),
            Triple("onDangerContainer / dangerContainer", semantic.onDangerContainer, semantic.dangerContainer),
        )

        return pairs.map { (name, text, bg) ->
            val ratio = contrastRatio(text, bg)
            ContrastCheck(
                themeName = themeName,
                pairName = name,
                textColor = text,
                backgroundColor = bg,
                ratio = (ratio * 100).toInt() / 100.0,
            )
        }
    }

    @Test
    fun `all theme color pairs meet WCAG AA contrast requirement`() {
        val allChecks = mutableListOf<ContrastCheck>()

        allChecks.addAll(
            checkColorScheme(
                "Pink + Light Blue (Light)",
                PinkLightBlueLightColors,
                PinkLightBlueSemanticLight,
            ),
        )
        allChecks.addAll(
            checkColorScheme(
                "Pink + Light Blue (Dark)",
                PinkLightBlueDarkColors,
                PinkLightBlueSemanticDark,
            ),
        )
        allChecks.addAll(
            checkColorScheme(
                "Black + Yellow + Red (Dark)",
                BlackYellowRedDarkColors,
                BlackYellowRedSemanticDark,
            ),
        )
        allChecks.addAll(
            checkColorScheme(
                "Black + Yellow + Red (Light)",
                BlackYellowRedLightColors,
                BlackYellowRedSemanticLight,
            ),
        )
        allChecks.addAll(
            checkColorScheme(
                "Green + White (Light)",
                GreenWhiteLightColors,
                GreenWhiteSemanticLight,
            ),
        )
        allChecks.addAll(
            checkColorScheme(
                "Green + White (Dark)",
                GreenWhiteDarkColors,
                GreenWhiteSemanticDark,
            ),
        )

        // Generate report markdown
        val sb = StringBuilder()
        sb.appendLine("# WCAG AA Contrast Audit Report")
        sb.appendLine()
        sb.appendLine("**Target Standard:** WCAG 2.1 Level AA (Minimum 4.5:1 for normal body text).")
        sb.appendLine()
        sb.appendLine("| Theme | Color Pair | Contrast Ratio | Required | Status |")
        sb.appendLine("|---|---|---|---|---|")

        var failuresCount = 0
        allChecks.forEach { check ->
            val status = if (check.passed) "✅ PASS" else "❌ FAIL"
            if (!check.passed) failuresCount++
            sb.appendLine(
                "| ${check.themeName} | ${check.pairName} | ${check.ratio}:1 | ${check.requiredRatio}:1 | $status |",
            )
        }

        sb.appendLine()
        sb.appendLine("## Summary")
        sb.appendLine("- Total checks: ${allChecks.size}")
        sb.appendLine("- Passed: ${allChecks.size - failuresCount}")
        sb.appendLine("- Failures: $failuresCount")

        // Try writing to docs/contrast-report.md if accessible from current working dir
        try {
            val userDir = System.getProperty("user.dir") ?: "."
            val root = File(userDir).let { if (it.name == "app") it.parentFile ?: it else it }
            val target = File(root, "docs/contrast-report.md")
            target.parentFile?.mkdirs()
            target.writeText(sb.toString())
        } catch (_: Exception) {
            // Ignore file write failure in sandbox
        }

        val failedDetails = allChecks.filter { !it.passed }.joinToString("\n") {
            "${it.themeName} -> ${it.pairName}: ratio ${it.ratio}:1 < ${it.requiredRatio}:1"
        }

        assertTrue("WCAG AA contrast failures found:\n$failedDetails", failuresCount == 0)
    }
}
