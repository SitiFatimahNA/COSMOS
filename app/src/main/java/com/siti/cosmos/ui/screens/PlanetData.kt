package com.siti.cosmos.ui.screens

import androidx.compose.ui.graphics.Color
import com.siti.cosmos.R

/**
 * Static planet data shared between PlanetsScreen and PlanetDetailScreen.
 * English names are used as navigation route keys.
 */
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

val allPlanets: List<PlanetInfo> = listOf(
    PlanetInfo(
        nameEn      = "Mercury",
        imageRes    = R.drawable.planet_mercury,
        type        = "Terrestrial Planet",
        description = "Mercury is the smallest planet in the Solar System and the closest to the Sun.",
        distance    = "57.9 million km",
        diameter    = "4,879 km",
        rotation    = "58.6 Earth days",
        revolution  = "88 Earth days",
        fact        = "Mercury has no atmosphere, so temperatures range from -180 °C at night to 430 °C during the day.",
        color       = Color(0xFF8A8FA8)
    ),
    PlanetInfo(
        nameEn      = "Venus",
        imageRes    = R.drawable.planet_venus,
        type        = "Terrestrial Planet",
        description = "Venus is the hottest planet in the Solar System despite not being the closest to the Sun.",
        distance    = "108.2 million km",
        diameter    = "12,104 km",
        rotation    = "243 Earth days",
        revolution  = "225 Earth days",
        fact        = "Venus rotates backwards compared to most other planets — the Sun rises in the west there.",
        color       = Color(0xFFD4906A)
    ),
    PlanetInfo(
        nameEn      = "Earth",
        imageRes    = R.drawable.planet_earth,
        type        = "Terrestrial Planet",
        description = "Earth is the only known planet to support life, with liquid water covering 71 % of its surface.",
        distance    = "149.6 million km",
        diameter    = "12,742 km",
        rotation    = "24 hours",
        revolution  = "365.25 days",
        fact        = "Earth is the densest planet in the Solar System and the fifth largest.",
        color       = Color(0xFF2E6FA3)
    ),
    PlanetInfo(
        nameEn      = "Mars",
        imageRes    = R.drawable.planet_mars,
        type        = "Terrestrial Planet",
        description = "Mars is known as the Red Planet due to iron oxide (rust) covering its surface.",
        distance    = "227.9 million km",
        diameter    = "6,779 km",
        rotation    = "24.6 hours",
        revolution  = "687 Earth days",
        fact        = "Olympus Mons on Mars is the tallest volcano in the Solar System at 21 km high.",
        color       = Color(0xFFC0392B)
    ),
    PlanetInfo(
        nameEn      = "Jupiter",
        imageRes    = R.drawable.planet_jupiter,
        type        = "Gas Giant",
        description = "Jupiter is the largest planet in the Solar System and has a Great Red Spot storm larger than Earth.",
        distance    = "778.5 million km",
        diameter    = "139,820 km",
        rotation    = "9.9 hours",
        revolution  = "11.86 Earth years",
        fact        = "Jupiter has 95 known moons. Ganymede is the largest moon in the Solar System.",
        color       = Color(0xFFC8956A)
    ),
    PlanetInfo(
        nameEn      = "Saturn",
        imageRes    = R.drawable.planet_saturn,
        type        = "Gas Giant",
        description = "Saturn is famous for its spectacular ring system made of ice and rocky debris.",
        distance    = "1,434 million km",
        diameter    = "116,460 km",
        rotation    = "10.7 hours",
        revolution  = "29.45 Earth years",
        fact        = "Saturn is so low in density it could float on water if there were an ocean large enough.",
        color       = Color(0xFFD4B060)
    ),
    PlanetInfo(
        nameEn      = "Uranus",
        imageRes    = R.drawable.planet_uranus,
        type        = "Ice Giant",
        description = "Uranus rotates on its side with an axial tilt of 98°, making it unique among planets.",
        distance    = "2,871 million km",
        diameter    = "50,724 km",
        rotation    = "17.2 hours",
        revolution  = "84 Earth years",
        fact        = "Uranus was the first planet discovered with a telescope, in 1781 by William Herschel.",
        color       = Color(0xFF5AC8D8)
    ),
    PlanetInfo(
        nameEn      = "Neptune",
        imageRes    = R.drawable.planet_neptune,
        type        = "Ice Giant",
        description = "Neptune is the farthest planet from the Sun and has the strongest winds in the Solar System.",
        distance    = "4,495 million km",
        diameter    = "49,244 km",
        rotation    = "16.1 hours",
        revolution  = "164.8 Earth years",
        fact        = "Neptune's largest moon Triton orbits in the opposite direction of the planet's rotation.",
        color       = Color(0xFF3A5FD8)
    )
)
