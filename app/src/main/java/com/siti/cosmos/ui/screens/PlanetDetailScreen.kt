package com.siti.cosmos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siti.cosmos.Planet3D
import com.siti.cosmos.R
import com.siti.cosmos.planetColors
import com.siti.cosmos.ui.components.CosmosTopBar

/** Full detail page for a solar-system planet — drives the Planet3D widget */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetDetailScreen(
    planetName: String,
    onBack: () -> Unit
) {
    val planet = allPlanets.find {
        it.nameEn.equals(planetName, ignoreCase = true)
    } ?: allPlanets.first()

    val colors3d = planetColors(planet.imageRes)

    Scaffold(
        topBar = {
            CosmosTopBar(title = planet.nameEn, showBack = true, onBack = onBack)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── 3D planet hero ─────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(
                                    colors3d.ring.copy(alpha = 0.12f),
                                    MaterialTheme.colorScheme.background
                                ),
                                radius = 700f
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
                }
            }

            // ── Name + type ────────────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        planet.nameEn,
                        fontSize = 36.sp, fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        planet.type,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }

            // ── Description ────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    planet.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            // ── Stats grid ─────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(20.dp))
                Text(
                    stringResource(R.string.planet_information),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PlanetStatCard(stringResource(R.string.distance_from_sun), planet.distance, Modifier.weight(1f))
                    PlanetStatCard(stringResource(R.string.diameter),          planet.diameter,  Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PlanetStatCard(stringResource(R.string.rotation),   planet.rotation,   Modifier.weight(1f))
                    PlanetStatCard(stringResource(R.string.revolution), planet.revolution, Modifier.weight(1f))
                }
            }

            // ── Fun fact ───────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors3d.base.copy(alpha = 0.18f))
                        .padding(18.dp)
                ) {
                    Text(
                        stringResource(R.string.did_you_know),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 15.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        planet.fact,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp, lineHeight = 20.sp
                    )
                }
            }

            // ── 3D hint ────────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "💡 Drag the planet above to rotate the light source. Tilt your phone to use the gyroscope.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

@Composable
fun PlanetStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        Spacer(Modifier.height(5.dp))
        Text(value, color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// ── Static planet data (English names for routing, full info) ──────────────────
data class PlanetInfo(
    val nameEn: String,
    val imageRes: Int,
    val type: String,
    val description: String,
    val distance: String,
    val diameter: String,
    val rotation: String,
    val revolution: String,
    val fact: String,
    val color: Color
)

val allPlanets = listOf(
    PlanetInfo("Mercury", com.siti.cosmos.R.drawable.planet_mercury, "Terrestrial Planet",
        "Mercury is the smallest planet in the Solar System and the closest to the Sun.",
        "57.9 million km", "4,879 km", "58.6 Earth days", "88 Earth days",
        "Mercury has no atmosphere, so temperatures range from -180°C to 430°C.", Color(0xFF777C91)),
    PlanetInfo("Venus", com.siti.cosmos.R.drawable.planet_venus, "Terrestrial Planet",
        "Venus is the hottest planet in the Solar System despite not being closest to the Sun.",
        "108.2 million km", "12,104 km", "243 Earth days", "225 Earth days",
        "Venus rotates backwards compared to most planets.", Color(0xFFD58B55)),
    PlanetInfo("Earth", com.siti.cosmos.R.drawable.planet_earth, "Terrestrial Planet",
        "Earth is the only known planet to support life, with liquid water on its surface.",
        "149.6 million km", "12,742 km", "24 hours", "365.25 days",
        "Earth is the densest planet in the Solar System.", Color(0xFF348FE0)),
    PlanetInfo("Mars", com.siti.cosmos.R.drawable.planet_mars, "Terrestrial Planet",
        "Mars is known as the Red Planet due to iron oxide on its surface.",
        "227.9 million km", "6,779 km", "24.6 hours", "687 Earth days",
        "Mars has the tallest volcano in the Solar System: Olympus Mons at 21 km.", Color(0xFFD65B50)),
    PlanetInfo("Jupiter", com.siti.cosmos.R.drawable.planet_jupiter, "Gas Giant",
        "Jupiter is the largest planet in the Solar System with a Great Red Spot storm.",
        "778.5 million km", "139,820 km", "9.9 hours", "11.86 Earth years",
        "Jupiter has 95 known moons, including Ganymede — the largest moon in the Solar System.", Color(0xFFC58A64)),
    PlanetInfo("Saturn", com.siti.cosmos.R.drawable.planet_saturn, "Gas Giant",
        "Saturn is famous for its stunning ring system made of ice and rock.",
        "1,434 million km", "116,460 km", "10.7 hours", "29.45 Earth years",
        "Saturn is so low in density that it could float on water.", Color(0xFFD9B56D)),
    PlanetInfo("Uranus", com.siti.cosmos.R.drawable.planet_uranus, "Ice Giant",
        "Uranus rotates on its side with an axial tilt of 98 degrees.",
        "2,871 million km", "50,724 km", "17.2 hours", "84 Earth years",
        "Uranus was the first planet discovered with a telescope, in 1781.", Color(0xFF65C9D6)),
    PlanetInfo("Neptune", com.siti.cosmos.R.drawable.planet_neptune, "Ice Giant",
        "Neptune is the farthest planet from the Sun with the strongest winds.",
        "4,495 million km", "49,244 km", "16.1 hours", "164.8 Earth years",
        "Neptune's largest moon Triton orbits in the opposite direction of the planet's rotation.", Color(0xFF4668DB)),
)
