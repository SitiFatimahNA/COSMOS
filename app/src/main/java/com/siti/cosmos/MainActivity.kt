package com.siti.cosmos

import com.siti.cosmos.ui.theme.COSMOSTheme
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog

// ── Colour palette ─────────────────────────────────────────────────────────────
val CosmosBlack    = Color(0xFF080808)
val CosmosCardDark = Color(0xFF111111)
val CosmosCard2    = Color(0xFF181818)
val CosmosStroke   = Color(0xFF242424)
val CosmosWhite    = Color(0xFFFFFFFF)
val CosmosGrey     = Color(0xFF888888)
val CosmosGreyDark = Color(0xFF444444)

// Legacy aliases used in ExploreScreen / NasaGallery
val SpaceBackground = Color(0xFFF5F6FA)
val SpacePurple     = Color(0xFF7257E8)
val SpaceBlue       = Color(0xFFE8E5FF)
val SpaceCard       = Color(0xFFFFFFFF)
val SpaceWhite      = Color(0xFF20243A)
val DarkBackground  = CosmosBlack
val DarkCard        = CosmosCardDark
val DarkWhite       = CosmosWhite
val DarkBlue        = Color(0xFF263C78)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            COSMOSTheme(darkTheme = true, dynamicColor = false) {
                CosmosApp(
                    onLanguageChange = { tag ->
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(tag)
                        )
                    }
                )
            }
        }
    }
}

// ── Data model ─────────────────────────────────────────────────────────────────
data class Planet(
    val name: String,
    val imageRes: Int,
    val description: String,
    val color: Color,
    val distance: String = "-",
    val diameter: String = "-",
    val rotation: String = "-",
    val revolution: String = "-",
    val fact: String = "-",
    val type: String = "Terrestrial Planet",
    val wikiSlug: String = ""          // e.g. "Mars" → opens Wikipedia
)

// ── App root ───────────────────────────────────────────────────────────────────
@Composable
fun CosmosApp(onLanguageChange: (String) -> Unit) {
    val planets = getPlanets()

    // Shared navigation state
    var selectedTab    by rememberSaveable { mutableIntStateOf(0) }
    var selectedPlanet by remember { mutableStateOf<Planet?>(null) }

    // Shared search dialog state (reachable from Home top-bar "🔍" button)
    var showSearch     by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBlack)
    ) {
        if (selectedPlanet != null) {
            PlanetDetailScreen(
                planet = selectedPlanet!!,
                onBack = { selectedPlanet = null }
            )
        } else {
            Scaffold(
                containerColor = CosmosBlack,
                bottomBar = {
                    CosmosBottomBar(
                        selectedTab  = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    when (selectedTab) {
                        0 -> HomeScreen(
                            planets       = planets,
                            onPlanetClick = { selectedPlanet = it },
                            onStartExplore = { selectedTab = 1 },
                            onSearchClick  = { showSearch = true }
                        )
                        1 -> ExploreScreen(
                            planets       = planets,
                            darkTheme     = true,
                            onPlanetClick = { selectedPlanet = it }
                        )
                        else -> SettingsScreen(onLanguageChange = onLanguageChange)
                    }
                }
            }
        }

        // ── Global Search Dialog ──────────────────────────────────────────────
        if (showSearch) {
            SearchDialog(
                planets   = planets,
                onDismiss = { showSearch = false },
                onSelect  = { planet ->
                    showSearch     = false
                    selectedPlanet = planet
                }
            )
        }
    }
}

// ── Bottom Navigation ──────────────────────────────────────────────────────────
@Composable
fun CosmosBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf("⌂" to "Home", "◎" to "Explore", "⚙" to "Settings")
    NavigationBar(
        containerColor = CosmosCardDark,
        tonalElevation  = 0.dp,
        modifier        = Modifier.height(64.dp)
    ) {
        items.forEachIndexed { index, (icon, label) ->
            val selected = selectedTab == index
            NavigationBarItem(
                selected = selected,
                onClick  = { onTabSelected(index) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(icon, fontSize = 20.sp, color = if (selected) CosmosWhite else CosmosGreyDark)
                        if (selected) {
                            Spacer(Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .width(16.dp).height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(CosmosWhite)
                            )
                        }
                    }
                },
                label  = null,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = CosmosWhite,
                    unselectedIconColor = CosmosGreyDark,
                    indicatorColor      = Color.Transparent
                )
            )
        }
    }
}

// ── Search Dialog ──────────────────────────────────────────────────────────────
@Composable
fun SearchDialog(
    planets: List<Planet>,
    onDismiss: () -> Unit,
    onSelect: (Planet) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val results = planets.filter { it.name.contains(query, ignoreCase = true) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CosmosCardDark)
                .padding(20.dp)
        ) {
            Text("Search Planets", color = CosmosWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value           = query,
                onValueChange   = { query = it },
                placeholder     = { Text("e.g. Mars", color = CosmosGrey) },
                singleLine      = true,
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor      = CosmosWhite,
                    unfocusedTextColor    = CosmosWhite,
                    focusedBorderColor    = CosmosGrey,
                    unfocusedBorderColor  = CosmosStroke,
                    cursorColor           = CosmosWhite,
                    focusedContainerColor   = CosmosCard2,
                    unfocusedContainerColor = CosmosCard2
                )
            )
            Spacer(Modifier.height(10.dp))
            results.forEach { planet ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelect(planet) }
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(planet.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(planet.imageRes).crossfade(true).build(),
                            contentDescription = planet.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(planet.name, color = CosmosWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(planet.type, color = CosmosGrey, fontSize = 11.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Text("→", color = CosmosGrey, fontSize = 16.sp)
                }
                Divider(color = CosmosStroke, thickness = 0.5.dp)
            }
        }
    }
}

// ── Home Screen ────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    planets: List<Planet>,
    onPlanetClick:  (Planet) -> Unit,
    onStartExplore: () -> Unit,
    onSearchClick:  () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(CosmosBlack),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // ── Top bar ────────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp).clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF8E44AD), Color(0xFFC0392B)))
                        ),
                    contentAlignment = Alignment.Center
                ) { Text("👤", fontSize = 16.sp) }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hey,",   color = CosmosGrey,  fontSize = 11.sp)
                    Text("Explorer", color = CosmosWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // 🔍 Search button — opens search dialog
                    SmallIconBtn("🔍", onClick = onSearchClick)
                }
            }
        }

        // ── Hero title ─────────────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Your personal",  color = CosmosWhite,    fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp)
                Text("guide to the",   color = CosmosGreyDark, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp)
                Text("universe",       color = CosmosGreyDark, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp)
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    // "Start Explore" button → navigates to Explore tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CosmosWhite)
                            .clickable { onStartExplore() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Start Explore →", color = CosmosBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "Perfect tool for\ndiscovering the\nwonders of the\nuniverse!",
                        color = CosmosGrey, fontSize = 11.sp, lineHeight = 16.sp
                    )
                }
            }
        }

        // ── Planets row ────────────────────────────────────────────────────────
        item { Spacer(Modifier.height(24.dp)) }
        item {
            Text("Planets", color = CosmosWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(planets) { planet ->
                    HomePlanetThumb(planet = planet, onClick = { onPlanetClick(planet) })
                }
            }
        }

        // ── NASA Gallery ───────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            NasaGallery(darkTheme = true)
        }

        // ── Explore links ──────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(8.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                ExploreLink(
                    label = "Explore more about",
                    value = "Asteroids, comets & meteors",
                    url   = "https://en.wikipedia.org/wiki/Asteroid"
                )
                ExploreLink(
                    label = "Learn more about",
                    value = "Moons",
                    url   = "https://en.wikipedia.org/wiki/Natural_satellite"
                )
            }
        }
    }
}

// ── Small icon button ──────────────────────────────────────────────────────────
@Composable
fun SmallIconBtn(icon: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(CosmosCard2)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(icon, fontSize = 15.sp, color = CosmosGrey)
    }
}

// ── Home planet thumbnail ──────────────────────────────────────────────────────
@Composable
fun HomePlanetThumb(planet: Planet, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(76.dp).clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(planet.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(planet.imageRes).crossfade(true).build(),
                contentDescription = planet.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(68.dp).clip(CircleShape)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(planet.name,  color = CosmosWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(planet.type,  color = CosmosGrey,  fontSize = 9.sp,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// ── Explore link row (opens browser) ──────────────────────────────────────────
@Composable
fun ExploreLink(label: String, value: String, url: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = CosmosGrey,  fontSize = 10.sp)
            Spacer(Modifier.height(3.dp))
            Text(value, color = CosmosWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Text("→", color = CosmosGrey, fontSize = 16.sp)
    }
    Divider(color = CosmosStroke, thickness = 1.dp)
}

// ── Planet Detail Screen ───────────────────────────────────────────────────────
@Composable
fun PlanetDetailScreen(planet: Planet, onBack: () -> Unit) {
    val context   = LocalContext.current
    var isFav     by remember { mutableStateOf(false) }
    val colors3d  = planetColors(planet.imageRes)

    Box(
        modifier = Modifier.fillMaxSize().background(CosmosBlack)
    ) {
        // ── Scrollable content ─────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 3D planet hero
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(colors3d.ring.copy(alpha = 0.08f), CosmosBlack),
                                radius = 600f
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

            // Planet name + type
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        planet.name,
                        color        = CosmosWhite,
                        fontSize     = 48.sp,
                        fontWeight   = FontWeight.Black,
                        letterSpacing = (-1.5).sp
                    )
                    Text(planet.type, color = CosmosGrey, fontSize = 13.sp)
                }
            }

            // Distance + action buttons
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Text("Distance from Earth", color = CosmosGrey, fontSize = 10.sp)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(planet.distance, color = CosmosWhite, fontSize = 15.sp,
                            fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Share button — shares planet info
                            ActionChip(label = "Share") {
                                val shareText = "🪐 ${planet.name} (${planet.type})\n" +
                                        "Distance: ${planet.distance}\n" +
                                        "Diameter: ${planet.diameter}\n\n" +
                                        planet.description
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type    = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share ${planet.name}"))
                            }
                            // Visit button — opens Wikipedia
                            ActionChip(label = "Visit →", filled = true) {
                                val slug   = planet.wikiSlug.ifBlank { planet.name }
                                val intent = Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://en.wikipedia.org/wiki/$slug"))
                                context.startActivity(intent)
                            }
                        }
                    }
                }
            }

            // Description
            item {
                Text(
                    planet.description,
                    color = CosmosGrey,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            // Stats grid
            item {
                Spacer(Modifier.height(20.dp))
                Text("Planet Information",
                    color = CosmosWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailStatCard("Diameter",   planet.diameter,   Modifier.weight(1f))
                    DetailStatCard("Rotation",   planet.rotation,   Modifier.weight(1f))
                    DetailStatCard("Revolution", planet.revolution, Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailStatCard("Distance", planet.distance, Modifier.weight(1f))
                    DetailStatCard("Type",     planet.type,     Modifier.weight(1f))
                }
            }

            // Fun fact card
            item {
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors3d.base.copy(alpha = 0.18f))
                        .padding(18.dp)
                ) {
                    Text("✦  Did you know?",
                        color = CosmosWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(planet.fact, color = CosmosGrey, fontSize = 13.sp, lineHeight = 19.sp)
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        // ── Overlay top bar (back + favourite) ────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            Box(
                modifier = Modifier
                    .size(38.dp).clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) { Text("←", color = CosmosWhite, fontSize = 18.sp) }

            Spacer(Modifier.weight(1f))

            // Favourite toggle
            Box(
                modifier = Modifier
                    .size(38.dp).clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { isFav = !isFav },
                contentAlignment = Alignment.Center
            ) {
                Text(if (isFav) "♥" else "♡",
                    color = if (isFav) Color(0xFFE74C3C) else CosmosWhite,
                    fontSize = 18.sp)
            }
        }
    }
}

// ── Reusable chips for the detail screen ──────────────────────────────────────
@Composable
fun ActionChip(label: String, filled: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (filled) CosmosWhite else CosmosCard2)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            color      = if (filled) CosmosBlack else CosmosWhite,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DetailStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CosmosCard2)
            .padding(12.dp)
    ) {
        Text(title, color = CosmosGrey,  fontSize = 10.sp)
        Spacer(Modifier.height(5.dp))
        Text(value, color = CosmosWhite, fontSize = 12.sp,
            fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

// ── Planet data ────────────────────────────────────────────────────────────────
@Composable
fun getPlanets(): List<Planet> = listOf(
    Planet(
        name        = stringResource(R.string.mercury_name),
        imageRes    = R.drawable.planet_mercury,
        description = stringResource(R.string.mercury_description),
        color       = Color(0xFF777C91),
        distance    = "77 million km",
        diameter    = "4,879 km",
        rotation    = "58.6 days",
        revolution  = "88 days",
        fact        = stringResource(R.string.mercury_fact),
        type        = "Terrestrial Planet",
        wikiSlug    = "Mercury_(planet)"
    ),
    Planet(
        name        = stringResource(R.string.venus_name),
        imageRes    = R.drawable.planet_venus,
        description = stringResource(R.string.venus_description),
        color       = Color(0xFFD58B55),
        distance    = "261 million km",
        diameter    = "12,104 km",
        rotation    = "243 days",
        revolution  = "225 days",
        fact        = stringResource(R.string.venus_fact),
        type        = "Terrestrial Planet",
        wikiSlug    = "Venus"
    ),
    Planet(
        name        = stringResource(R.string.earth_name),
        imageRes    = R.drawable.planet_earth,
        description = stringResource(R.string.earth_description),
        color       = Color(0xFF348FE0),
        distance    = "Home planet",
        diameter    = "12,742 km",
        rotation    = "24 hours",
        revolution  = "365.25 days",
        fact        = stringResource(R.string.earth_fact),
        type        = "Terrestrial Planet",
        wikiSlug    = "Earth"
    ),
    Planet(
        name        = stringResource(R.string.mars_name),
        imageRes    = R.drawable.planet_mars,
        description = stringResource(R.string.mars_description),
        color       = Color(0xFFD65B50),
        distance    = "1.88 Earth Years",
        diameter    = "6,779 km",
        rotation    = "24.6 hours",
        revolution  = "687 days",
        fact        = stringResource(R.string.mars_fact),
        type        = "Terrestrial Planet",
        wikiSlug    = "Mars"
    ),
    Planet(
        name        = stringResource(R.string.jupiter_name),
        imageRes    = R.drawable.planet_jupiter,
        description = stringResource(R.string.jupiter_description),
        color       = Color(0xFFC58A64),
        distance    = "628 million km",
        diameter    = "139,820 km",
        rotation    = "9.9 hours",
        revolution  = "11.86 years",
        fact        = stringResource(R.string.jupiter_fact),
        type        = "Gas Giant",
        wikiSlug    = "Jupiter"
    ),
    Planet(
        name        = stringResource(R.string.saturn_name),
        imageRes    = R.drawable.planet_saturn,
        description = stringResource(R.string.saturn_description),
        color       = Color(0xFFD9B56D),
        distance    = "1.2 billion km",
        diameter    = "116,460 km",
        rotation    = "10.7 hours",
        revolution  = "29.45 years",
        fact        = stringResource(R.string.saturn_fact),
        type        = "Gas Giant",
        wikiSlug    = "Saturn"
    ),
    Planet(
        name        = stringResource(R.string.uranus_name),
        imageRes    = R.drawable.planet_uranus,
        description = stringResource(R.string.uranus_description),
        color       = Color(0xFF65C9D6),
        distance    = "2.7 billion km",
        diameter    = "50,724 km",
        rotation    = "17.2 hours",
        revolution  = "84 years",
        fact        = stringResource(R.string.uranus_fact),
        type        = "Ice Giant",
        wikiSlug    = "Uranus"
    ),
    Planet(
        name        = stringResource(R.string.neptune_name),
        imageRes    = R.drawable.planet_neptune,
        description = stringResource(R.string.neptune_description),
        color       = Color(0xFF4668DB),
        distance    = "4.3 billion km",
        diameter    = "49,244 km",
        rotation    = "16.1 hours",
        revolution  = "164.8 years",
        fact        = stringResource(R.string.neptune_fact),
        type        = "Ice Giant",
        wikiSlug    = "Neptune"
    )
)

// ── Settings Screen ────────────────────────────────────────────────────────────
@Composable
fun SettingsScreen(onLanguageChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBlack)
            .padding(20.dp)
    ) {
        Text(stringResource(R.string.settings),
            color = CosmosWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Customise your experience", color = CosmosGrey, fontSize = 13.sp)
        Spacer(Modifier.height(28.dp))

        Text(stringResource(R.string.language),
            color = CosmosGrey, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick  = { onLanguageChange("id") },
                modifier = Modifier.weight(1f),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = CosmosWhite),
                border   = androidx.compose.foundation.BorderStroke(1.dp, CosmosStroke)
            ) { Text("🇮🇩  Indonesia") }

            OutlinedButton(
                onClick  = { onLanguageChange("en") },
                modifier = Modifier.weight(1f),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = CosmosWhite),
                border   = androidx.compose.foundation.BorderStroke(1.dp, CosmosStroke)
            ) { Text("🇺🇸  English") }
        }
    }
}
