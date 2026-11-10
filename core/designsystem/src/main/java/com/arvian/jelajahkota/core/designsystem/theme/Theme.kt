package com.arvian.jelajahkota.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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
    outline = BrandBlueOutline
)

@Composable
fun JelajahKotaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // JelajahKota brand colors follow the approved Figma specification
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JelajahKotaTypography,
        content = content
    )
}
