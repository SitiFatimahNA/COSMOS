package com.siti.cosmos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.siti.cosmos.Planet3D
import com.siti.cosmos.R
import com.siti.cosmos.planetColors
import com.siti.cosmos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetDetailScreen(
    planetName: String,
    onBack: () -> Unit
) {
    val planet   = allPlanets.find { it.nameEn.equals(planetName, ignoreCase = true) }
                   ?: allPlanets.first()
    val colors3d = planetColors(planet.imageRes)

    // Overlay top bar over scrollable content
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        LazyColumn(
            modifier       = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = Dimens.space6xl)
        ) {

            // ── 3D Planet Hero ─────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .background(
                            Brush.radialGradient(
                                colors  = listOf(
                                    colors3d.ring.copy(alpha = 0.20f),
                                    colors3d.ring.copy(alpha = 0.06f),
                                    MaterialTheme.colorScheme.background
                                ),
                                radius  = 900f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Planet3D(
                        baseColor  = colors3d.base,
                        darkColor  = colors3d.dark,
                        lightColor = colors3d.light,
                        ringColor  = colors3d.ring,
                        size       = 240.dp
                    )

                    // Interaction hint — bottom of hero
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = Dimens.spaceLg),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs)
                    ) {
                        Icon(
                            Icons.Outlined.TouchApp,
                            contentDescription = null,
                            tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.45f),
                            modifier = Modifier.size(Dimens.iconSm)
                        )
                        Text(
                            text  = stringResource(R.string.planet_3d_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.45f)
                        )
                    }
                }
            }

            // ── Name + Type ────────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.padding(
                        horizontal = Dimens.screenPadding,
                        vertical   = Dimens.spaceXs
                    )
                ) {
                    Text(
                        text          = planet.nameEn,
                        style         = MaterialTheme.typography.displaySmall,
                        color         = MaterialTheme.colorScheme.onBackground,
                        fontWeight    = FontWeight.Black
                    )
                    Spacer(Modifier.height(Dimens.spaceXs))
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
                    ) {
                        // Planet type chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimens.radiusFull))
                                .background(colors3d.base.copy(alpha = 0.18f))
                                .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceXxs + 2.dp)
                        ) {
                            Text(
                                text  = planet.type,
                                style = MaterialTheme.typography.labelMedium,
                                color = colors3d.base,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // ── Description ────────────────────────────────────────────────────
            item {
                Text(
                    text   = planet.description,
                    style  = MaterialTheme.typography.bodyMedium,
                    color  = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.dp.value.sp,
                    modifier   = Modifier.padding(
                        horizontal = Dimens.screenPadding,
                        vertical   = Dimens.spaceSm
                    )
                )
            }

            // ── Stats grid ─────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(Dimens.spaceLg))
                Text(
                    text     = stringResource(R.string.planet_information),
                    style    = MaterialTheme.typography.titleMedium,
                    color    = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = Dimens.screenPadding)
                )
                Spacer(Modifier.height(Dimens.spaceMd))

                val stats = listOf(
                    StatItem(Icons.Outlined.SocialDistance, stringResource(R.string.distance_from_sun), planet.distance),
                    StatItem(Icons.Outlined.Circle,         stringResource(R.string.diameter),           planet.diameter),
                    StatItem(Icons.Outlined.Refresh,        stringResource(R.string.rotation),           planet.rotation),
                    StatItem(Icons.Outlined.PublishedWithChanges, stringResource(R.string.revolution),   planet.revolution),
                )

                // 2×2 grid using two rows
                Column(
                    modifier              = Modifier.padding(horizontal = Dimens.screenPadding),
                    verticalArrangement   = Arrangement.spacedBy(Dimens.itemSpacing)
                ) {
                    stats.chunked(2).forEach { row ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacing)
                        ) {
                            row.forEach { stat ->
                                PlanetStatCard(
                                    icon     = stat.icon,
                                    label    = stat.label,
                                    value    = stat.value,
                                    accent   = colors3d.base,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill empty slot if row has 1 item
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            // ── Fun fact ───────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(Dimens.spaceXl))
                FunFactCard(
                    fact    = planet.fact,
                    accent  = colors3d.base,
                    modifier = Modifier.padding(horizontal = Dimens.screenPadding)
                )
            }
        }

        // ── Overlay back button ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(Dimens.spaceMd)
                .size(Dimens.touchTarget)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onBack
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_to_explore),
                tint               = White,
                modifier           = Modifier.size(Dimens.iconMd)
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private data class StatItem(
    val icon: ImageVector,
    val label: String,
    val value: String
)

@Composable
fun PlanetStatCard(
    icon: ImageVector = Icons.Outlined.Info,
    label: String,
    value: String,
    accent: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(Dimens.spaceLg)
    ) {
        // Icon with accent tint
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(Dimens.radiusSm))
                .background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = accent,
                modifier           = Modifier.size(Dimens.iconSm)
            )
        }
        Spacer(Modifier.height(Dimens.spaceSm))
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Dimens.spaceXxs))
        Text(
            text      = value,
            style     = MaterialTheme.typography.labelLarge,
            color     = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FunFactCard(fact: String, accent: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.radiusXl))
            .background(
                Brush.linearGradient(
                    colors = listOf(accent.copy(alpha = 0.18f), accent.copy(alpha = 0.08f))
                )
            )
            .padding(Dimens.spaceXl)
    ) {
        Column {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                Icon(
                    Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint     = accent,
                    modifier = Modifier.size(Dimens.iconMd)
                )
                Text(
                    text      = stringResource(R.string.did_you_know),
                    style     = MaterialTheme.typography.titleSmall,
                    color     = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(Dimens.spaceMd))
            Text(
                text      = fact,
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.dp.value.sp
            )
        }
    }
}

// Keep sp extension accessible
private val Float.sp get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)
