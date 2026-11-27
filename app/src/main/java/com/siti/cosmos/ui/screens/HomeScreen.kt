package com.siti.cosmos.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.siti.cosmos.NasaGallery
import com.siti.cosmos.R
import com.siti.cosmos.ui.components.*
import com.siti.cosmos.ui.navigation.NavRoutes
import com.siti.cosmos.ui.theme.*

// ── Planet data for the carousel ──────────────────────────────────────────────
data class HomePlanet(
    val nameEn: String,
    val nameId: String,
    val imageRes: Int,
    val accentColor: Color
)

private val homePlanets = listOf(
    HomePlanet("Mercury", "Merkurius", R.drawable.planet_mercury, Color(0xFF8A8FA8)),
    HomePlanet("Venus", "Venus", R.drawable.planet_venus, Color(0xFFD4906A)),
    HomePlanet("Earth", "Bumi", R.drawable.planet_earth, Color(0xFF2E6FA3)),
    HomePlanet("Mars", "Mars", R.drawable.planet_mars, Color(0xFFC0392B)),
    HomePlanet("Jupiter", "Yupiter", R.drawable.planet_jupiter, Color(0xFFC8956A)),
    HomePlanet("Saturn", "Saturnus", R.drawable.planet_saturn, Color(0xFFD4B060)),
    HomePlanet("Uranus", "Uranus", R.drawable.planet_uranus, Color(0xFF5AC8D8)),
    HomePlanet("Neptune", "Neptunus", R.drawable.planet_neptune, Color(0xFF3A5FD8))
)

// ── Root composable ───────────────────────────────────────────────────────────

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = Dimens.space3xl)
    ) {
        // 1. Header
        item {
            HomeHeader(
                onSettingsClick = {
                    onNavigate(NavRoutes.SETTINGS)
                }
            )
        }

        // 2. Planet Carousel
        item {
            Spacer(Modifier.height(Dimens.sectionSpacing))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = stringResource(R.string.explore_planets),
                    subtitle = stringResource(R.string.planets_discover)
                )

                TextButton(
                    onClick = {
                        onNavigate(NavRoutes.PLANETS)
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.see_all),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.iconSm)
                    )
                }
            }

            Spacer(Modifier.height(Dimens.spaceMd))

            PlanetCarousel(
                planets = homePlanets,
                onNavigate = onNavigate
            )
        }

        // 3. NASA Gallery
        item {
            Spacer(Modifier.height(Dimens.sectionSpacing))
            NasaGallery()
        }

        // 4. Footer
        item {
            Spacer(Modifier.height(Dimens.spaceLg))

            Text(
                text = stringResource(R.string.made_for_explorers),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.screenPadding,
                        vertical = Dimens.spaceLg
                    )
            )
        }
    }
}


// ══════════════════════════════════════════════════════════════════════════════
// SUB-COMPOSABLES
// ══════════════════════════════════════════════════════════════════════════════

// ── 1. Header ─────────────────────────────────────────────────────────────────
@Composable
private fun HomeHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spaceLg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand mark
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = "COSMOS",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text  = stringResource(R.string.explore_universe),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Settings button
        Box(
            modifier = Modifier
                .size(Dimens.touchTarget)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onSettingsClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.settings),
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(Dimens.iconMd)
            )
        }
    }
}



// ── 4. Planet Carousel ────────────────────────────────────────────────────────
@Composable
private fun PlanetCarousel(
    planets: List<HomePlanet>,
    onNavigate: (String) -> Unit
) {
    LazyRow(
        contentPadding       = PaddingValues(horizontal = Dimens.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacing)
    ) {
        items(planets) { planet ->
            PlanetChip(planet = planet, onClick = {
                onNavigate(NavRoutes.planetDetail(planet.nameEn))
            })
        }
    }
}

@Composable
private fun PlanetChip(planet: HomePlanet, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.94f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "planet_scale"
    )

    Column(
        modifier            = Modifier
            .width(Dimens.planetChipSize)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) {
                pressed = false
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Planet image circle with accent glow border
        Box(
            modifier = Modifier
                .size(Dimens.planetChipSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            planet.accentColor.copy(alpha = 0.35f),
                            planet.accentColor.copy(alpha = 0.08f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(planet.imageRes)
                    .crossfade(true)
                    .build(),
                contentDescription = planet.nameEn,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(Dimens.planetChipSize - 8.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(Modifier.height(Dimens.spaceSm))
        Text(
            text = planet.nameId,
            style     = MaterialTheme.typography.labelMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis
        )
    }
}

// End of HomeScreen.kt
