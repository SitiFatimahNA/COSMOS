package com.siti.cosmos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.siti.cosmos.ui.viewmodel.ApodViewModel

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val apodVm: ApodViewModel = viewModel()
    val apodState by apodVm.state.collectAsStateWithLifecycle()

    // planets list inline (not needing Composable context)
    val planets = listOf(
        HomePlanet("Mercury", R.drawable.planet_mercury, Color(0xFF777C91)),
        HomePlanet("Venus",   R.drawable.planet_venus,   Color(0xFFD58B55)),
        HomePlanet("Earth",   R.drawable.planet_earth,   Color(0xFF348FE0)),
        HomePlanet("Mars",    R.drawable.planet_mars,    Color(0xFFD65B50)),
        HomePlanet("Jupiter", R.drawable.planet_jupiter, Color(0xFFC58A64)),
        HomePlanet("Saturn",  R.drawable.planet_saturn,  Color(0xFFD9B56D)),
        HomePlanet("Uranus",  R.drawable.planet_uranus,  Color(0xFF65C9D6)),
        HomePlanet("Neptune", R.drawable.planet_neptune, Color(0xFF4668DB)),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Header banner ──────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Settings icon top-right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                        .clickable { onNavigate(NavRoutes.SETTINGS) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚙", fontSize = 18.sp)
                }

                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Text(
                        "COSMOS ✦",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.explore_universe),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        stringResource(R.string.journey_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ── Explore categories ─────────────────────────────────────────────────
        item {
            SectionHeader(
                title    = stringResource(R.string.explore_section),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(staticExploreSections) { section ->
                    ExploreCard(section = section, onClick = { onNavigate(section.route) })
                }
            }
        }

        // ── APOD Featured ──────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            SectionHeader(
                title    = stringResource(R.string.apod_title),
                subtitle = stringResource(R.string.apod_subtitle),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(10.dp))
            when {
                apodState.isLoading -> ShimmerBox(
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(200.dp)
                )
                apodState.error != null -> ErrorState(
                    message = apodState.error!!,
                    onRetry = { apodVm.retry() },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                apodState.apod != null -> {
                    val apod = apodState.apod!!
                    SpaceImageCard(
                        imageUrl = apod.url,
                        title    = apod.title,
                        subtitle = apod.date,
                        onClick  = { onNavigate(NavRoutes.APOD) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }

        // ── Planet quick-access ────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title    = stringResource(R.string.explore_planets),
                    subtitle = stringResource(R.string.planets_discover)
                )
                TextButton(onClick = { onNavigate(NavRoutes.PLANETS) }) {
                    Text(
                        "See All →",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(planets) { planet ->
                    HomePlanetChip(planet = planet, onClick = {
                        onNavigate(NavRoutes.planetDetail(planet.nameEn))
                    })
                }
            }
        }

        // ── NASA Image Gallery ─────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            NasaGallery()
        }

        // ── Footer ─────────────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.made_for_explorers),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}

// ── Data + sub-composables ─────────────────────────────────────────────────────

data class HomePlanet(val nameEn: String, val imageRes: Int, val color: Color)

data class ExploreSection(
    val titleRes: Int,
    val emoji: String,
    val gradientColors: List<Color>,
    val route: String
)

val staticExploreSections = listOf(
    ExploreSection(R.string.nav_apod,  "🌠", listOf(Color(0xFF4B6CF7), Color(0xFF9B59B6)), NavRoutes.APOD),
    ExploreSection(R.string.nav_mars,  "🔴", listOf(Color(0xFFC0392B), Color(0xFFE67E22)), NavRoutes.MARS),
    ExploreSection(R.string.nav_neo,   "☄️", listOf(Color(0xFF16A085), Color(0xFF4B6CF7)), NavRoutes.NEO),
    ExploreSection(R.string.nav_favs,  "⭐", listOf(Color(0xFFF39C12), Color(0xFFE74C3C)), NavRoutes.FAVORITES),
)

@Composable
fun ExploreCard(section: ExploreSection, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(130.dp).height(100.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(section.gradientColors))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text(section.emoji, fontSize = 30.sp, modifier = Modifier.align(Alignment.TopStart))
        Text(
            stringResource(section.titleRes),
            color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomStart),
            maxLines = 2, overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun HomePlanetChip(planet: HomePlanet, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(76.dp).clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(planet.color.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(planet.imageRes).crossfade(true).build(),
                contentDescription = planet.nameEn,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(68.dp).clip(CircleShape)
            )
        }
        Spacer(Modifier.height(5.dp))
        Text(
            planet.nameEn,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}
