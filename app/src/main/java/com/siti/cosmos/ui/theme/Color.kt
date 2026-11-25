package com.siti.cosmos.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════════════════════════
// COSMOS DESIGN SYSTEM — Color Tokens
//
// Dark palette layers (most distinct → deepest):
//   CosmicBlack < SpaceNavy < SpaceDark < SpaceCard < SpaceCardElevated
//
// Light palette layers:
//   LightBg < LightSurface < LightCard < LightCardElevated
// ══════════════════════════════════════════════════════════════════════════════

// ── Dark Palette ──────────────────────────────────────────────────────────────

/** Deepest background — true cosmic black with very slight blue tint */
val CosmicBlack       = Color(0xFF050810)

/** Page background in dark mode */
val SpaceNavy         = Color(0xFF080C18)

/** Surface / list background — clearly distinct from background */
val SpaceDark         = Color(0xFF0D1221)

/** Card / container level */
val SpaceCard         = Color(0xFF131A2E)   // also exported as legacy alias below

/** Elevated card / modal / bottom sheet */
val SpaceCardElevated = Color(0xFF1A2340)

/** Stroke / divider / outline — just visible, not harsh */
val SpaceStroke       = Color(0xFF232D48)

/** Subtle stroke for inner separators */
val SpaceStrokeSubtle = Color(0xFF1C2438)

// ── Dark Accent Colors ────────────────────────────────────────────────────────

/** Primary accent — electric blue */
val ElectricBlue  = Color(0xFF4D6EF5)

/** Pressed / hover state of primary */
val ElectricBlueDeep = Color(0xFF3A5AE8)

/** Secondary accent — soft violet */
val SoftViolet    = Color(0xFF8B6CF7)

/** Tertiary accent — subtle cyan */
val CosmicCyan    = Color(0xFF00C8D4)

// ── Semantic ──────────────────────────────────────────────────────────────────

val AlertRed      = Color(0xFFEF4444)
val AlertOrange   = Color(0xFFF97316)
val SuccessGreen  = Color(0xFF22C55E)
val StarGold      = Color(0xFFFBBF24)

// ── Dark Text Hierarchy ───────────────────────────────────────────────────────

val White         = Color(0xFFFFFFFF)
val White90       = Color(0xE6FFFFFF)
val White70       = Color(0xB3FFFFFF)
val White50       = Color(0x80FFFFFF)
val White30       = Color(0x4DFFFFFF)
val White15       = Color(0x26FFFFFF)

// ── Light Palette ─────────────────────────────────────────────────────────────

/** Page background — cool near-white, very subtle blue tint */
val LightBackground   = Color(0xFFF6F8FF)

/** Surface — pure white */
val LightSurface      = Color(0xFFFFFFFF)

/** Card background — slight cool tint */
val LightCard         = Color(0xFFEFF2FE)

/** Elevated card / modal */
val LightCardElevated = Color(0xFFE6EAFC)

/** Stroke / outline — soft gray-blue */
val LightStroke       = Color(0xFFD4DAF0)

// ── Light Accent Colors ───────────────────────────────────────────────────────

/** Primary in light — slightly deeper blue for WCAG AA contrast on white */
val NavyBlue      = Color(0xFF3250D6)

/** Muted purple for light secondary */
val MutedPurple   = Color(0xFF7C5CE6)

// ── Light Text Hierarchy ──────────────────────────────────────────────────────

val Ink           = Color(0xFF0F1629)   // primary text on light
val InkMedium     = Color(0xFF3A4468)   // secondary text
val InkMuted      = Color(0xFF6B7592)   // tertiary / placeholder
val InkDisabled   = Color(0xFFB0BAD4)   // disabled

// ══════════════════════════════════════════════════════════════════════════════
// LEGACY ALIASES — kept for backward compatibility with:
//   NasaGallery.kt, ExploreScreen.kt, Planet3DView.kt, old screens
// DO NOT REMOVE unless those files are fully migrated.
// ══════════════════════════════════════════════════════════════════════════════

val SpaceBlack       = CosmicBlack
val SpaceDarkSurface = SpaceDark
val SpaceDarkCard    = SpaceCard
val SpaceAccent      = ElectricBlue
val SpaceAccentAlt   = SoftViolet
val SpaceTeal        = CosmicCyan
val SpaceRed         = AlertRed
val SpaceOrange      = AlertOrange
val SpaceGold        = StarGold

val SpaceBackground  = LightBackground
val SpacePurple      = ElectricBlue
val SpaceBlue        = Color(0xFFE4E9FF)
val SpaceWhite       = Ink
val LightOnSurface   = Ink
val LightSubtext     = InkMuted
val DarkBackground   = SpaceNavy
val DarkCard         = SpaceCard
val DarkWhite        = White
val DarkBlue         = Color(0xFF1E3270)
