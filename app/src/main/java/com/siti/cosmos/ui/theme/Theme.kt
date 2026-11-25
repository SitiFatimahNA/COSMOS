package com.siti.cosmos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════════════════════════
// COSMOS DESIGN SYSTEM — Theme
//
// Dark scheme: deep cosmic black with electric blue accent,
//   clearly layered surfaces (background → surface → surfaceVariant → elevated)
//
// Light scheme: cool near-white with navy blue accent,
//   warm card surfaces that feel airy but grounded
// ══════════════════════════════════════════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    // ── Primary (Electric Blue) ───────────────────────────────────────────────
    primary             = ElectricBlue,
    onPrimary           = White,
    primaryContainer    = Color(0xFF1E2E6E),   // deep blue container for chips
    onPrimaryContainer  = Color(0xFFB8C8FF),   // light blue text on container

    // ── Secondary (Soft Violet) ───────────────────────────────────────────────
    secondary             = SoftViolet,
    onSecondary           = White,
    secondaryContainer    = Color(0xFF2A1D5A),  // deep violet container
    onSecondaryContainer  = Color(0xFFD4C0FF),  // light violet text

    // ── Tertiary (Cosmic Cyan) ────────────────────────────────────────────────
    tertiary             = CosmicCyan,
    onTertiary           = CosmicBlack,
    tertiaryContainer    = Color(0xFF003A40),
    onTertiaryContainer  = Color(0xFF9FF0F5),

    // ── Background & Surface layers ───────────────────────────────────────────
    background          = SpaceNavy,            // #080C18 — page bg
    onBackground        = White90,

    surface             = SpaceDark,            // #0D1221 — surface / lists
    onSurface           = White90,

    surfaceVariant      = SpaceCard,            // #131A2E — cards
    onSurfaceVariant    = White70,

    surfaceTint         = ElectricBlue,

    // ── Inverse (for SnackBar, tooltips) ─────────────────────────────────────
    inverseSurface      = Color(0xFFE0E6FF),
    inverseOnSurface    = Ink,
    inversePrimary      = NavyBlue,

    // ── Outline ───────────────────────────────────────────────────────────────
    outline             = SpaceStroke,          // #232D48 — card borders
    outlineVariant      = SpaceStrokeSubtle,    // #1C2438 — inner separators

    // ── Error ─────────────────────────────────────────────────────────────────
    error               = AlertRed,
    onError             = White,
    errorContainer      = Color(0xFF4A1515),
    onErrorContainer    = Color(0xFFFFB3B3),

    // ── Scrim ─────────────────────────────────────────────────────────────────
    scrim               = Color(0xCC000000)
)

private val LightColorScheme = lightColorScheme(
    // ── Primary (Navy Blue) ───────────────────────────────────────────────────
    primary             = NavyBlue,
    onPrimary           = White,
    primaryContainer    = Color(0xFFDDE4FF),   // soft blue container
    onPrimaryContainer  = Color(0xFF0A1A6E),   // deep navy text on container

    // ── Secondary (Muted Purple) ──────────────────────────────────────────────
    secondary             = MutedPurple,
    onSecondary           = White,
    secondaryContainer    = Color(0xFFEDE0FF),
    onSecondaryContainer  = Color(0xFF30006E),

    // ── Tertiary ──────────────────────────────────────────────────────────────
    tertiary             = Color(0xFF0097A7),
    onTertiary           = White,
    tertiaryContainer    = Color(0xFFCCF5F8),
    onTertiaryContainer  = Color(0xFF003038),

    // ── Background & Surface layers ───────────────────────────────────────────
    background          = LightBackground,      // #F6F8FF — page bg
    onBackground        = Ink,

    surface             = LightSurface,         // #FFFFFF — surface
    onSurface           = Ink,

    surfaceVariant      = LightCard,            // #EFF2FE — cards
    onSurfaceVariant    = InkMedium,

    surfaceTint         = NavyBlue,

    // ── Inverse ───────────────────────────────────────────────────────────────
    inverseSurface      = SpaceCard,
    inverseOnSurface    = White90,
    inversePrimary      = ElectricBlue,

    // ── Outline ───────────────────────────────────────────────────────────────
    outline             = LightStroke,          // #D4DAF0
    outlineVariant      = Color(0xFFE5EAFF),    // softer inner separator

    // ── Error ─────────────────────────────────────────────────────────────────
    error               = AlertRed,
    onError             = White,
    errorContainer      = Color(0xFFFFE5E5),
    onErrorContainer    = Color(0xFF8B0000),

    // ── Scrim ─────────────────────────────────────────────────────────────────
    scrim               = Color(0x80000000)
)

@Composable
fun COSMOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,            // keep false — we own the palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
