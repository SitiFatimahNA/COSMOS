package com.siti.cosmos.ui.theme

import androidx.compose.ui.unit.dp

// ══════════════════════════════════════════════════════════════════════════════
// COSMOS DESIGN SYSTEM — Spacing & Dimension Tokens
//
// Use these instead of hardcoding dp values in Composables.
// Every screen should use the same horizontal content padding (Dimens.screenPadding).
// ══════════════════════════════════════════════════════════════════════════════

object Dimens {

    // ── Spacing scale ─────────────────────────────────────────────────────────
    val spaceXxs  = 2.dp
    val spaceXs   = 4.dp
    val spaceSm   = 8.dp
    val spaceMd   = 12.dp
    val spaceLg   = 16.dp
    val spaceXl   = 20.dp
    val spaceXxl  = 24.dp
    val space3xl  = 32.dp
    val space4xl  = 40.dp
    val space5xl  = 48.dp
    val space6xl  = 64.dp

    // ── Layout constants ──────────────────────────────────────────────────────
    /** Horizontal padding applied to all screen content */
    val screenPadding     = 20.dp

    /** Horizontal padding for tighter sub-sections (chips, filters) */
    val screenPaddingMd   = 16.dp

    /** Vertical spacing between major sections on a screen */
    val sectionSpacing    = 24.dp

    /** Spacing between items within a section (cards in a row/grid) */
    val itemSpacing       = 12.dp

    /** Spacing between items in a dense list */
    val itemSpacingDense  = 8.dp

    // ── Corner Radius ─────────────────────────────────────────────────────────
    /** Small — chips, badges, tags */
    val radiusXs   = 6.dp

    /** Medium — input fields, filter chips */
    val radiusSm   = 10.dp

    /** Default — standard card corners */
    val radiusMd   = 14.dp

    /** Large — featured cards, image cards */
    val radiusLg   = 18.dp

    /** Extra large — hero cards, bottom sheets */
    val radiusXl   = 24.dp

    /** Full pill — FAB, avatar, fully rounded element */
    val radiusFull = 100.dp

    // ── Elevation / Shadow ────────────────────────────────────────────────────
    val elevationNone  = 0.dp
    val elevationSm    = 2.dp
    val elevationMd    = 4.dp
    val elevationLg    = 8.dp

    // ── Icon sizes ────────────────────────────────────────────────────────────
    val iconXs  = 16.dp
    val iconSm  = 20.dp
    val iconMd  = 24.dp
    val iconLg  = 28.dp
    val iconXl  = 32.dp

    // ── Touch target minimum (accessibility) ─────────────────────────────────
    val touchTarget = 48.dp

    // ── Bottom Navigation Bar ─────────────────────────────────────────────────
    val bottomNavHeight = 68.dp

    // ── Top Bar ───────────────────────────────────────────────────────────────
    val topBarHeight    = 56.dp

    // ── Image aspect ratios (expressed as height for a fillMaxWidth image) ───
    /** 16:9 — standard landscape photo */
    val imageHeightWide    = 220.dp

    /** 4:3 — slightly taller, good for hero */
    val imageHeightHero    = 260.dp

    /** Square */
    val imageHeightSquare  = 180.dp

    /** Gallery / horizontal scroll card */
    val imageHeightGallery = 150.dp

    /** Thumbnail in list */
    val imageSizeThumbnail = 72.dp

    /** Planet chip circle */
    val planetChipSize = 80.dp
}
