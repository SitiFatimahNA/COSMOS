package com.siti.cosmos

import com.siti.cosmos.ui.theme.COSMOSTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
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


val SpaceBackground = Color(0xFFF5F6FA)
val SpacePurple = Color(0xFF7257E8)
val SpaceBlue = Color(0xFFE8E5FF)
val SpaceCard = Color(0xFFFFFFFF)
val SpaceWhite = Color(0xFF20243A)

val DarkBackground = Color(0xFF0B102B)
val DarkCard = Color(0xFF192348)
val DarkWhite = Color(0xFFF5F3FF)
val DarkBlue = Color(0xFF263C78)

class MainActivity : ComponentActivity() {
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
                    onThemeChange = { darkTheme = it }
                )
            }
        }
    }
}


data class Planet(
    val name: String,
    val emoji: String,
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
    onThemeChange: (Boolean) -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
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
                        "Home" to "⌂",
                        "Explore" to "◎",
                        "Settings" to "⚙"
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
                            onThemeChange = onThemeChange
                        )
                    }
                }
            }
        }

}

val planets = listOf(
    Planet(
        "Mercury", "🌑", "The smallest planet",
        Color(0xFF777C91),
        "57.9 million km", "4,879 km",
        "58.6 Earth days", "88 Earth days",
        "Mercury is the closest planet to the Sun."
    ),
    Planet(
        "Venus", "🟠", "The hottest planet",
        Color(0xFFD58B55),
        "108.2 million km", "12,104 km",
        "243 Earth days", "225 Earth days",
        "Venus has an extremely hot atmosphere."
    ),
    Planet(
        "Earth", "🌍", "Our home planet",
        Color(0xFF348FE0),
        "149.6 million km", "12,742 km",
        "24 hours", "365.25 days",
        "Earth is the only planet known to support life."
    ),
    Planet(
        "Mars", "🔴", "The red planet",
        Color(0xFFD65B50),
        "227.9 million km", "6,779 km",
        "24.6 hours", "687 Earth days",
        "Mars has the largest volcano in the Solar System."
    ),
    Planet(
        "Jupiter", "🪐", "The gas giant",
        Color(0xFFC58A64),
        "778.6 million km", "139,820 km",
        "9.9 hours", "11.86 Earth years",
        "Jupiter is the largest planet in our Solar System."
    ),
    Planet(
        "Saturn", "🪐", "The ringed planet",
        Color(0xFFD9B56D),
        "1.43 billion km", "116,460 km",
        "10.7 hours", "29.4 Earth years",
        "Saturn is famous for its spectacular rings."
    ),
    Planet(
        "Uranus", "🔵", "The ice giant",
        Color(0xFF65C9D6),
        "2.87 billion km", "50,724 km",
        "17.2 hours", "84 Earth years",
        "Uranus rotates on its side."
    ),
    Planet(
        "Neptune", "🔵", "The distant planet",
        Color(0xFF4668DB),
        "4.50 billion km", "49,244 km",
        "16.1 hours", "165 Earth years",
        "Neptune has extremely powerful winds."
    )
)
@Composable
fun DashboardScreen(
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
                        text = "COSMOS ✦",
                        color = Color(0xFFB9A8FF),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Explore the Universe",
                        color = textColor,
                        fontSize = if (isLandscape) 30.sp else 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your journey through space starts here.",
                        color = subtitleColor,
                        fontSize = 14.sp
                    )
                }
            }

            item {
                HeroCard(isLandscape)
            }

            item {
                SectionTitle("Explore Planets", "8 planets to discover")
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
                    text = "Made for curious explorers ✦",
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
                    text = "SPECIAL DISCOVERY",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Beyond the Stars",
                    color = Color.White,
                    fontSize = if (isLandscape) 25.sp else 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Explore the mysteries of our universe.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }

            Text(
                text = "🪐",
                fontSize = if (isLandscape) 75.sp else 65.sp
            )
        }
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            color = SpaceWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            color = Color.LightGray,
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
                Text(
                    text = planet.emoji,
                    fontSize = 48.sp
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
    val infoCardColor = if (darkTheme) DarkCard else SpaceCard
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
                    text = "← Back to Explore",
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
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = planet.emoji,
                        fontSize = 110.sp
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
                text = "Planet Information",
                color = textColor,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoCard("Distance from Sun", planet.distance, darkTheme)
                InfoCard("Diameter", planet.diameter, darkTheme)
                InfoCard("Rotation", planet.rotation, darkTheme)
                InfoCard("Revolution", planet.revolution, darkTheme)
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (darkTheme) SpacePurple else SpacePurple
                )
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        text = "✦ Did You Know?",
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
    onThemeChange: (Boolean) -> Unit
) {
    val textColor = if (darkTheme) DarkWhite else SpaceWhite
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Settings",
            color = textColor,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dark Mode",
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = darkTheme,
                onCheckedChange = onThemeChange
            )
        }
    }
}

