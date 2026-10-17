package com.dashdazz.hyperglow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Default Neon Accents
val NeonCyan = Color(0xFF00F0FF)
val CyberPurple = Color(0xFFB026FF)
val ElectricAmber = Color(0xFFFFB800)
val HyperGreen = Color(0xFF00FF66)
val PureAmoledBlack = Color(0xFF000000)
val DarkSurface = Color(0xFF0D1117)
val DarkSurfaceCard = Color(0xFF161B22)

data class HyperGlowPalette(
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val background: Color,
    val cardBackground: Color,
    val isPureAmoled: Boolean,
    val gradientBorder: Brush
)

val LocalHyperGlow = compositionLocalOf {
    HyperGlowPalette(
        primaryAccent = NeonCyan,
        secondaryAccent = CyberPurple,
        background = PureAmoledBlack,
        cardBackground = DarkSurfaceCard,
        isPureAmoled = true,
        gradientBorder = Brush.linearGradient(listOf(NeonCyan, CyberPurple))
    )
}

@Composable
fun HyperGlowTheme(
    accentColor: Color = NeonCyan,
    isPureAmoled: Boolean = true,
    content: @Composable () -> Unit
) {
    val bg = if (isPureAmoled) PureAmoledBlack else Color(0xFF0B0F19)
    val cardBg = if (isPureAmoled) Color(0xFF10141C) else DarkSurfaceCard
    val borderBrush = Brush.linearGradient(listOf(accentColor, CyberPurple))

    val palette = HyperGlowPalette(
        primaryAccent = accentColor,
        secondaryAccent = CyberPurple,
        background = bg,
        cardBackground = cardBg,
        isPureAmoled = isPureAmoled,
        gradientBorder = borderBrush
    )

    val colorScheme = darkColorScheme(
        primary = accentColor,
        secondary = CyberPurple,
        background = bg,
        surface = cardBg,
        onPrimary = Color.Black,
        onBackground = Color(0xFFE2E8F0),
        onSurface = Color(0xFFE2E8F0)
    )

    CompositionLocalProvider(LocalHyperGlow provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
