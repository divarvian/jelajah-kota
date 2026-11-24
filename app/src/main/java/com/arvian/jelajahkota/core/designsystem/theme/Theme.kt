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
    secondaryContainer = Color(0xFFFFECE0),
    onSecondaryContainer = Color(0xFF5A2300),
    background = White,
    onBackground = DarkText,
    surface = White,
    onSurface = DarkText,
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = SubtitleText,
    surfaceContainerLowest = White,
    surfaceContainerLow = SurfaceGray,
    surfaceContainer = Color(0xFFF3F5F8),
    surfaceContainerHigh = Color(0xFFECEFF3),
    outline = BrandBlueOutline,
    outlineVariant = BorderColor,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedContainer,
    onErrorContainer = Color(0xFF8C1D18)
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlueLightDarkMode,
    onPrimary = Color(0xFF00244D),
    primaryContainer = Color(0xFF0E3466),
    onPrimaryContainer = Color(0xFFD8E9FF),
    secondary = Color(0xFFFF9248),
    onSecondary = Color(0xFF381600),
    secondaryContainer = Color(0xFF4D2406),
    onSecondaryContainer = Color(0xFFFFDBC5),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurfaceVariant,
    surfaceContainerHigh = DarkSurfaceHigh,
    outline = BrandBlueLightDarkMode,
    outlineVariant = DarkOutlineVariant,
    error = ErrorRedDark,
    onError = Color(0xFF490B0E),
    errorContainer = ErrorRedContainerDark,
    onErrorContainer = Color(0xFFFFB4AB)
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
