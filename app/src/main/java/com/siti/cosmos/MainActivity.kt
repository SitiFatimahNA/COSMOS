package com.siti.cosmos

import com.siti.cosmos.ui.theme.COSMOSTheme
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.stringResource
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest


val SpaceBackground = Color(0xFFF5F6FA)
val SpacePurple = Color(0xFF7257E8)
val SpaceBlue = Color(0xFFE8E5FF)
val SpaceCard = Color(0xFFFFFFFF)
val SpaceWhite = Color(0xFF20243A)

val DarkBackground = Color(0xFF0B102B)
val DarkCard = Color(0xFF192348)
val DarkWhite = Color(0xFFF5F3FF)
val DarkBlue = Color(0xFF263C78)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()

            var darkTheme by remember {
                mutableStateOf(systemDarkTheme)
            }

            COSMOSTheme(
                darkTheme = darkTheme,
                dynamicColor = false
            ) {
                CosmosApp(
                    darkTheme = darkTheme,
                    onThemeChange = { darkTheme = it },
                    onLanguageChange = { languageTag ->
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(languageTag)
                        )
                    }
                )
            }
        }
    }
}


data class Planet(
    val name: String,
    val imageRes: Int,
    val description: String,
    val color: Color,
    val distance: String = "-",
    val diameter: String = "-",
    val rotation: String = "-",
    val revolution: String = "-",
    val fact: String = "-"
)


@Composable
fun CosmosApp(
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val planets = getPlanets()

    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
    }

    var selectedPlanet by remember {
        mutableStateOf<Planet?>(null)
    }
    val backgroundColor = if (darkTheme) {
        DarkBackground
    } else {
        SpaceBackground
    }

    val cardColor = if (darkTheme) {
        DarkCard
    } else {
        SpaceCard
    }

    val textColor = if (darkTheme) {
        DarkWhite
    } else {
        SpaceWhite
    }

    val blueColor = if (darkTheme) {
        DarkBlue
    } else {
        SpaceBlue
    }


    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            NavigationBar(
                containerColor = cardColor
            ) {
                val tabs = listOf(
                    stringResource(R.string.home) to "Γîé",
                    stringResource(R.string.explore) to "ΓùÄ",
                    stringResource(R.string.settings) to "ΓÜÖ"
                )

                    tabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Text(tab.second, fontSize = 22.sp)
                            },
                            label = { Text(tab.first) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (darkTheme) Color.White else SpacePurple,
                                selectedTextColor = SpacePurple,
                                indicatorColor = if (darkTheme) DarkBlue else SpaceBlue,
                                unselectedIconColor = if (darkTheme) Color.LightGray else Color.Gray,
                                unselectedTextColor = if (darkTheme) Color.LightGray else Color.Gray
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (selectedPlanet != null) {
                    PlanetDetailScreen(
                        planet = selectedPlanet!!,
                        darkTheme = darkTheme,
                        onBack = { selectedPlanet = null }
                    )
                } else {
                    when (selectedTab) {
                        0 -> DashboardScreen(
                            planets = planets,
                            darkTheme = darkTheme,
                            onPlanetClick = { planet ->
                                selectedPlanet = planet
                            }
                        )
                        1 -> ExploreScreen(
                            planets = planets,
                            darkTheme = darkTheme,
                            onPlanetClick = { planet ->
                                selectedPlanet = planet
                            }
                        )
                        else -> SettingsScreen(
                            darkTheme = darkTheme,
                            onThemeChange = onThemeChange,
                            onLanguageChange = onLanguageChange
                        )
                    }
                }
            }
        }

}

@Composable
fun getPlanets(): List<Planet> {
    return listOf(
        Planet(
            name = stringResource(R.string.mercury_name),
            imageRes = R.drawable.planet_mercury,
            description = stringResource(R.string.mercury_description),
            color = Color(0xFF777C91),
            distance = "57.9 ${stringResource(R.string.million)} km",
            diameter = "4,879 km",
            rotation = "58.6 ${stringResource(R.string.earth_days)}",
            revolution = "88 ${stringResource(R.string.earth_days)}",
            fact = stringResource(R.string.mercury_fact)
        ),
        Planet(
            name = stringResource(R.string.venus_name),
            imageRes = R.drawable.planet_venus,
            description = stringResource(R.string.venus_description),
            color = Color(0xFFD58B55),
            distance = "108.2 ${stringResource(R.string.million)} km",
            diameter = "12,104 km",
            rotation = "243 ${stringResource(R.string.earth_days)}",
            revolution = "225 ${stringResource(R.string.earth_days)}",
            fact = stringResource(R.string.venus_fact)
        ),
        Planet(
            name = stringResource(R.string.earth_name),
            imageRes = R.drawable.planet_earth,
            description = stringResource(R.string.earth_description),
            color = Color(0xFF348FE0),
            distance = "149.6 ${stringResource(R.string.million)} km",
            diameter = "12,742 km",
            rotation = "24 ${stringResource(R.string.hours)}",
            revolution = "365.25 ${stringResource(R.string.days)}",
            fact = stringResource(R.string.earth_fact)
        ),
        Planet(
            name = stringResource(R.string.mars_name),
            imageRes = R.drawable.planet_mars,
            description = stringResource(R.string.mars_description),
            color = Color(0xFFD65B50),
            distance = "227.9 million km",
            diameter = "6,779 km",
            rotation = "24.6 ${stringResource(R.string.hours)}",
            revolution = "687 ${stringResource(R.string.earth_days)}",
            fact = stringResource(R.string.mars_fact)
        ),
        Planet(
            name = stringResource(R.string.jupiter_name),
            imageRes = R.drawable.planet_jupiter,
            description = stringResource(R.string.jupiter_description),
            color = Color(0xFFC58A64),
            distance = "778.5 ${stringResource(R.string.million)} km",
            diameter = "139,820 km",
            rotation = "9.9 ${stringResource(R.string.hours)}",
            revolution = "11.86 ${stringResource(R.string.earth_years)}",
            fact = stringResource(R.string.jupiter_fact)
        ),
        Planet(
            name = stringResource(R.string.saturn_name),
            imageRes = R.drawable.planet_saturn,
            description = stringResource(R.string.saturn_description),
            color = Color(0xFFD9B56D),
            distance = "1,434 ${stringResource(R.string.million)} km",
            diameter = "116,460 km",
            rotation = "10.7 ${stringResource(R.string.hours)}",
            revolution = "29.45 ${stringResource(R.string.earth_years)}",
            fact = stringResource(R.string.saturn_fact)
        ),
        Planet(
            name = stringResource(R.string.uranus_name),
            imageRes = R.drawable.planet_uranus,
            description = stringResource(R.string.uranus_description),
            color = Color(0xFF65C9D6),
            distance = "2,871 ${stringResource(R.string.million)} km",
            diameter = "50,724 km",
            rotation = "17.2 ${stringResource(R.string.hours)}",
            revolution = "84 ${stringResource(R.string.earth_years)}",
            fact = stringResource(R.string.uranus_fact)
        ),
        Planet(
            name = stringResource(R.string.neptune_name),
            imageRes = R.drawable.planet_neptune,
            description = stringResource(R.string.neptune_description),
            color = Color(0xFF4668DB),
            distance = "4,495 ${stringResource(R.string.million)} km",
            diameter = "49,244 km",
            rotation = "16.1 ${stringResource(R.string.hours)}",
            revolution = "164.8 ${stringResource(R.string.earth_years)}",
            fact = stringResource(R.string.neptune_fact)
        )
    )
}

@Composable
fun DashboardScreen(
    planets: List<Planet>,
    darkTheme: Boolean,
    onPlanetClick: (Planet) -> Unit
) {
    val textColor = if (darkTheme) DarkWhite else SpaceWhite
    val subtitleColor = if (darkTheme) Color.LightGray else Color.DarkGray
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val isLandscape = maxWidth > maxHeight

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = if (isLandscape) 28.dp else 20.dp,
                vertical = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "COSMOS Γ£ª",
                        color = Color(0xFFB9A8FF),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.explore_universe),
                        color = textColor,
                        fontSize = if (isLandscape) 30.sp else 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.journey_subtitle),
                        color = subtitleColor,
                        fontSize = 14.sp
                    )
                }
            }

            item {
                HeroCard(isLandscape)
            }

            item {
                SectionTitle(
                    title = stringResource(R.string.explore_planets),
                    subtitle = stringResource(R.string.planets_discover),
                    darkTheme = darkTheme
                )
                Spacer(Modifier.height(12.dp))





                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(planets) { planet ->
                        PlanetCard(
                            planet = planet,
                            darkTheme = darkTheme,
                            onClick = {
                                onPlanetClick(planet)
                            }
                        )
                    }
                }
            }


            item {
                NasaGallery(darkTheme = darkTheme)
            }


            item {
                Text(
                    text = stringResource(R.string.made_for_explorers),
                    color = subtitleColor,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun HeroCard(isLandscape: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = SpacePurple
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.special_discovery),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.beyond_stars),
                    color = Color.White,
                    fontSize = if (isLandscape) 25.sp else 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.hero_description),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }

            Text(
                text = "≡ƒ¬É",
                fontSize = if (isLandscape) 75.sp else 65.sp
            )
        }
    }
}


@Composable
fun SectionTitle(
    title: String,
    subtitle: String,
    darkTheme: Boolean
) {
    val textColor = if (darkTheme) DarkWhite else SpaceWhite
    val subtitleColor = if (darkTheme) Color.LightGray else Color.DarkGray

    Column {
        Text(
            text = title,
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            color = subtitleColor,
            fontSize = 12.sp
        )
    }
}



@Composable
fun PlanetCard(
    planet: Planet,
    darkTheme: Boolean = false,
    onClick: () -> Unit
) {
    val cardColor = if (darkTheme) DarkCard else SpaceCard
    val textColor = if (darkTheme) DarkWhite else SpaceWhite
    val descriptionColor = if (darkTheme) Color.LightGray else Color.DarkGray
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(145.dp)
            .height(190.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        planet.color.copy(alpha = 0.2f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(planet.imageRes)
                        .crossfade(true)
                        .build(),
                    contentDescription = planet.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = planet.name,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = planet.description,
                color = descriptionColor,
                fontSize = 11.sp
            )
        }
    }
}


@Composable
fun GalleryCard(
    title: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(emoji, fontSize = 36.sp)
            Text(
                text = title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SimplePage(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = SpaceWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            color = Color.LightGray
        )
    }
}


@Composable
fun PlanetDetailScreen(
    planet: Planet,
    darkTheme: Boolean,
    onBack: () -> Unit
) {
    val backgroundColor = if (darkTheme) DarkBackground else SpaceBackground
    val textColor = if (darkTheme) DarkWhite else SpaceWhite
    val descriptionColor = if (darkTheme) Color.LightGray else Color.DarkGray
    val colors3d = planetColors(planet.imageRes)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            TextButton(onClick = onBack) {
                Text(
                    text = stringResource(R.string.back_to_explore),
                    color = Color(0xFFB9A8FF)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = planet.color.copy(alpha = 0.25f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Planet3D(
                        baseColor  = colors3d.base,
                        darkColor  = colors3d.dark,
                        lightColor = colors3d.light,
                        ringColor  = colors3d.ring,
                        size       = 220.dp
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = planet.name,
                    color = textColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = planet.description,
                    color = descriptionColor,
                    fontSize = 16.sp
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.planet_information),
                color = textColor,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoCard(stringResource(R.string.distance_from_sun), planet.distance, darkTheme)
                InfoCard(stringResource(R.string.diameter), planet.diameter, darkTheme)
                InfoCard(stringResource(R.string.rotation), planet.rotation, darkTheme)
                InfoCard(stringResource(R.string.revolution), planet.revolution, darkTheme)
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SpacePurple
                )
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        text = stringResource(R.string.did_you_know),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = planet.fact,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    darkTheme: Boolean
) {
    val cardColor = if (darkTheme) DarkCard else SpaceCard
    val titleColor = if (darkTheme) Color.LightGray else Color.DarkGray
    val valueColor = if (darkTheme) DarkWhite else SpaceWhite
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = value,
                color = valueColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val textColor = if (darkTheme) DarkWhite else SpaceWhite

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            color = textColor,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Dark Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dark_mode),
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = darkTheme,
                onCheckedChange = onThemeChange
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Language
        Text(
            text = stringResource(R.string.language),
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { onLanguageChange("id") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Bahasa Indonesia")
            }

            OutlinedButton(
                onClick = { onLanguageChange("en") },
                modifier = Modifier.weight(1f)
            ) {
                Text("English")
            }
        }
    }
}


