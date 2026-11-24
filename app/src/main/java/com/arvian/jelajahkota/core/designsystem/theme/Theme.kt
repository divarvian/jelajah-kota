package com.arvian.jelajahkota.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandBluePrimary,
    onPrimary = White,
    primaryContainer = BrandBlueContainer,
    onPrimaryContainer = BrandBlueDark,
    secondary = OrangeRibbon,
    onSecondary = White,
    background = White,
    onBackground = DarkText,
    surface = White,
    onSurface = DarkText,
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = SubtitleText,
    outline = BrandBlueOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4C9AFF),
    onPrimary = Color(0xFF00234E),
    primaryContainer = Color(0xFF003B82),
    onPrimaryContainer = Color(0xFFD6E6FF),
    secondary = OrangeRibbon,
    onSecondary = White,
    background = Color(0xFF101318),
    onBackground = Color(0xFFE8ECF2),
    surface = Color(0xFF171B22),
    onSurface = Color(0xFFE8ECF2),
    surfaceVariant = Color(0xFF212630),
    onSurfaceVariant = Color(0xFFB4BDCB),
    outline = Color(0xFF4C9AFF)
)

@Composable
fun JelajahKotaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JelajahKotaTypography,
        content = content
    )
}
