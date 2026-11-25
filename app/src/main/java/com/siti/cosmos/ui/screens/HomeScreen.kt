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
import com.siti.cosmos.data.model.ApodResponse
import com.siti.cosmos.ui.components.*
import com.siti.cosmos.ui.navigation.NavRoutes
import com.siti.cosmos.ui.theme.*
import com.siti.cosmos.ui.viewmodel.ApodViewModel

// ── Planet data for the carousel ──────────────────────────────────────────────
data class HomePlanet(
    val nameEn: String,
    val imageRes: Int,
    val accentColor: Color
)

private val homePlanets = listOf(
    HomePlanet("Mercury", R.drawable.planet_mercury, Color(0xFF8A8FA8)),
    HomePlanet("Venus",   R.drawable.planet_venus,   Color(0xFFD4906A)),
    HomePlanet("Earth",   R.drawable.planet_earth,   Color(0xFF2E6FA3)),
    HomePlanet("Mars",    R.drawable.planet_mars,    Color(0xFFC0392B)),
    HomePlanet("Jupiter", R.drawable.planet_jupiter, Color(0xFFC8956A)),
    HomePlanet("Saturn",  R.drawable.planet_saturn,  Color(0xFFD4B060)),
    HomePlanet("Uranus",  R.drawable.planet_uranus,  Color(0xFF5AC8D8)),
    HomePlanet("Neptune", R.drawable.planet_neptune, Color(0xFF3A5FD8)),
)

// ── Exploration category definition ───────────────────────────────────────────
private data class ExploreCategory(
    val titleRes: Int,
    val subtitleRes: Int,
    val icon: ImageVector,
    val gradientStart: Color,
    val gradientEnd: Color,
    val route: String
)

private val exploreCategories = listOf(
    ExploreCategory(
        titleRes      = R.string.nav_apod,
        subtitleRes   = R.string.apod_category_sub,
        icon          = Icons.Outlined.PhotoCamera,
        gradientStart = Color(0xFF3A54D6),
        gradientEnd   = Color(0xFF7C4DDE),
        route         = NavRoutes.APOD
    ),
    ExploreCategory(
        titleRes      = R.string.nav_mars,
        subtitleRes   = R.string.mars_category_sub,
        icon          = Icons.Outlined.Explore,
        gradientStart = Color(0xFFC0392B),
        gradientEnd   = Color(0xFFE67E22),
        route         = NavRoutes.MARS
    ),
    ExploreCategory(
        titleRes      = R.string.nav_neo,
        subtitleRes   = R.string.neo_category_sub,
        icon          = Icons.Outlined.Stars,
        gradientStart = Color(0xFF0D7A7A),
        gradientEnd   = Color(0xFF1A5ED6),
        route         = NavRoutes.NEO
    ),
    ExploreCategory(
        titleRes      = R.string.nav_favs,
        subtitleRes   = R.string.favs_category_sub,
        icon          = Icons.Outlined.FavoriteBorder,
        gradientStart = Color(0xFFD4760A),
        gradientEnd   = Color(0xFFD6353A),
        route         = NavRoutes.FAVORITES
    ),
)

// ── Root composable ───────────────────────────────────────────────────────────
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val apodVm: ApodViewModel = viewModel()
    val apodState by apodVm.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier       = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = Dimens.space3xl)
    ) {
        // ── 1. Header ──────────────────────────────────────────────────────────
        item {
            HomeHeader(onSettingsClick = { onNavigate(NavRoutes.SETTINGS) })
        }

        // ── 2. APOD Hero Card ──────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(Dimens.spaceLg))
            ApodHeroSection(
                apod      = apodState.apod,
                isLoading = apodState.isLoading,
                error     = if (apodState.apod == null) apodState.error else null,
                fromCache = apodState.fromCache,
                onRetry   = { apodVm.retry() },
                onClick   = { onNavigate(NavRoutes.APOD) }
            )
        }

        // ── 3. Explore Categories ──────────────────────────────────────────────
        item {
            Spacer(Modifier.height(Dimens.sectionSpacing))
            SectionHeader(
                title    = stringResource(R.string.explore_section),
                modifier = Modifier.padding(horizontal = Dimens.screenPadding)
            )
            Spacer(Modifier.height(Dimens.spaceMd))
            ExploreCategoryRow(
                categories = exploreCategories,
                onNavigate = onNavigate
            )
        }

        // ── 4. Planet Carousel ─────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(Dimens.sectionSpacing))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title    = stringResource(R.string.explore_planets),
                    subtitle = stringResource(R.string.planets_discover)
                )
                TextButton(
                    onClick = { onNavigate(NavRoutes.PLANETS) },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text  = stringResource(R.string.see_all),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.iconSm)
                    )
                }
            }
            Spacer(Modifier.height(Dimens.spaceMd))
            PlanetCarousel(
                planets    = homePlanets,
                onNavigate = onNavigate
            )
        }

        // ── 5. NASA Gallery ────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(Dimens.sectionSpacing))
            NasaGallery()
        }

        // ── 6. Footer ──────────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(Dimens.spaceLg))
            Text(
                text     = stringResource(R.string.made_for_explorers),
                style    = MaterialTheme.typography.labelSmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spaceLg)
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

// ── 2. APOD Hero Section ──────────────────────────────────────────────────────
@Composable
private fun ApodHeroSection(
    apod: ApodResponse?,
    isLoading: Boolean,
    error: String?,
    fromCache: Boolean,
    onRetry: () -> Unit,
    onClick: () -> Unit
) {
    when {
        isLoading -> ApodHeroShimmer()
        apod != null -> ApodHeroCard(
            apod      = apod,
            fromCache = fromCache,
            onClick   = onClick
        )
        error != null -> ApodHeroError(message = error, onRetry = onRetry)
    }
}

@Composable
private fun ApodHeroShimmer() {
    Box(
        modifier = Modifier
            .padding(horizontal = Dimens.screenPadding)
            .fillMaxWidth()
            .height(Dimens.imageHeightHero)
            .clip(RoundedCornerShape(Dimens.radiusXl))
    ) {
        ShimmerBox(modifier = Modifier.fillMaxSize(), cornerRadius = Dimens.radiusXl)
    }
}

@Composable
private fun ApodHeroError(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = Dimens.screenPadding)
            .fillMaxWidth()
            .height(Dimens.imageHeightHero)
            .clip(RoundedCornerShape(Dimens.radiusXl))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            Icon(
                Icons.Outlined.CloudOff,
                contentDescription = null,
                tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f),
                modifier = Modifier.size(Dimens.space4xl)
            )
            Text(
                text  = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.spaceLg)
            )
            OutlinedButton(
                onClick = onRetry,
                shape   = RoundedCornerShape(Dimens.radiusSm)
            ) {
                Icon(
                    Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.iconSm)
                )
                Spacer(Modifier.width(Dimens.spaceXs))
                Text("Retry", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun ApodHeroCard(
    apod: ApodResponse,
    fromCache: Boolean,
    onClick: () -> Unit
) {
    // Subtle press animation
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "hero_scale"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = Dimens.screenPadding)
            .fillMaxWidth()
            .height(Dimens.imageHeightHero)
            .scale(scale)
            .clip(RoundedCornerShape(Dimens.radiusXl))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) {
                pressed = false
                onClick()
            }
    ) {
        // Hero image
        if (apod.mediaType != "video") {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(apod.hdUrl ?: apod.url)
                    .crossfade(true)
                    .build(),
                contentDescription = apod.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
        } else {
            // Video APOD — use thumbnail URL and show play indicator
            Box(modifier = Modifier.fillMaxSize().background(SpaceNavy)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(apod.url).crossfade(true).build(),
                    contentDescription = apod.title,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
                // Play badge
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(Dimens.space5xl)
                        .clip(CircleShape)
                        .background(Color.Black.copy(0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.PlayArrow,
                        contentDescription = "Video",
                        tint     = White,
                        modifier = Modifier.size(Dimens.space3xl)
                    )
                }
            }
        }

        // Multi-stop gradient — transparent top, dense black bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.4f to Color.Transparent,
                            0.72f to Color.Black.copy(alpha = 0.55f),
                            1.0f to Color.Black.copy(alpha = 0.88f)
                        )
                    )
                )
        )

        // Top-left: APOD label
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Dimens.spaceLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.radiusXs))
                    .background(ElectricBlue.copy(alpha = 0.85f))
                    .padding(horizontal = Dimens.spaceSm, vertical = 3.dp)
            ) {
                Text(
                    text  = "NASA APOD",
                    style = MaterialTheme.typography.labelSmall,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
            if (fromCache) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.radiusXs))
                        .background(Color.Black.copy(0.45f))
                        .padding(horizontal = Dimens.spaceSm, vertical = 3.dp)
                ) {
                    Text(
                        text  = "Cached",
                        style = MaterialTheme.typography.labelSmall,
                        color = White70
                    )
                }
            }
        }

        // Bottom: title + date + open button
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(Dimens.spaceLg)
        ) {
            Text(
                text      = apod.title,
                style     = MaterialTheme.typography.titleLarge,
                color     = White,
                maxLines  = 2,
                overflow  = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(Dimens.spaceXs))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = apod.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = White70
                )
                // "Explore →" button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.radiusFull))
                        .background(ElectricBlue)
                        .padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceXs)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXxs)
                    ) {
                        Text(
                            text   = stringResource(R.string.explore_btn),
                            style  = MaterialTheme.typography.labelLarge,
                            color  = White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            Icons.Outlined.ArrowForward,
                            contentDescription = null,
                            tint     = White,
                            modifier = Modifier.size(Dimens.iconSm)
                        )
                    }
                }
            }
        }
    }
}

// ── 3. Explore Category Row ───────────────────────────────────────────────────
@Composable
private fun ExploreCategoryRow(
    categories: List<ExploreCategory>,
    onNavigate: (String) -> Unit
) {
    LazyRow(
        contentPadding       = PaddingValues(horizontal = Dimens.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacing)
    ) {
        items(categories) { category ->
            ExploreCategoryCard(category = category, onClick = { onNavigate(category.route) })
        }
    }
}

@Composable
private fun ExploreCategoryCard(
    category: ExploreCategory,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue    = if (pressed) 0.96f else 1f,
        animationSpec  = spring(stiffness = Spring.StiffnessMedium),
        label          = "cat_scale"
    )

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(110.dp)
            .scale(scale)
            .clip(RoundedCornerShape(Dimens.radiusLg))
            .background(
                Brush.linearGradient(
                    colors = listOf(category.gradientStart, category.gradientEnd),
                    start  = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end    = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) {
                pressed = false
                onClick()
            }
            .padding(Dimens.spaceLg)
    ) {
        // Icon top-left
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(White15)
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = category.icon,
                contentDescription = null,
                tint               = White,
                modifier           = Modifier.size(Dimens.iconMd)
            )
        }

        // Title bottom-left
        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text(
                text      = stringResource(category.titleRes),
                style     = MaterialTheme.typography.labelLarge,
                color     = White,
                fontWeight = FontWeight.Bold,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis
            )
            Text(
                text  = stringResource(category.subtitleRes),
                style = MaterialTheme.typography.labelSmall,
                color = White70,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
            text      = planet.nameEn,
            style     = MaterialTheme.typography.labelMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis
        )
    }
}

// End of HomeScreen.kt
