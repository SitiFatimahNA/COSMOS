package com.siti.cosmos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary            = SpaceAccent,
    onPrimary          = White,
    primaryContainer   = SpaceDarkCard,
    onPrimaryContainer = White,
    secondary          = SpaceTeal,
    onSecondary        = SpaceBlack,
    secondaryContainer = Color(0xFF1A2550),
    onSecondaryContainer = White,
    tertiary           = SpaceAccentAlt,
    background         = SpaceBlack,
    onBackground       = White,
    surface            = SpaceDarkSurface,
    onSurface          = White,
    surfaceVariant     = SpaceDarkCard,
    onSurfaceVariant   = White70,
    outline            = Color(0xFF2A3560),
    error              = SpaceRed,
    onError            = White
)

private val LightColorScheme = lightColorScheme(
    primary            = SpaceAccent,
    onPrimary          = White,
    primaryContainer   = LightCard,
    onPrimaryContainer = LightOnSurface,
    secondary          = Color(0xFF0E8A8A),
    onSecondary        = White,
    secondaryContainer = Color(0xFFE0FAFA),
    onSecondaryContainer = LightOnSurface,
    tertiary           = SpaceAccentAlt,
    background         = LightBackground,
    onBackground       = LightOnSurface,
    surface            = LightSurface,
    onSurface          = LightOnSurface,
    surfaceVariant     = LightCard,
    onSurfaceVariant   = LightSubtext,
    outline            = Color(0xFFCDD5E0),
    error              = SpaceRed,
    onError            = White
)

@Composable
fun COSMOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
