package com.siti.cosmos.ui.screens

import androidx.compose.ui.graphics.Color
import com.siti.cosmos.R
import androidx.annotation.StringRes

/**
 * Static planet data shared between PlanetsScreen and PlanetDetailScreen.
 * English names are used as navigation route keys.
 */
data class PlanetInfo(
    val nameEn: String,
    @StringRes val nameRes: Int,
    val imageRes: Int,
    @StringRes val typeRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val distanceRes: Int,
    @StringRes val diameterRes: Int,
    @StringRes val rotationRes: Int,
    @StringRes val revolutionRes: Int,
    @StringRes val factRes: Int,
    val color: Color
)

val allPlanets: List<PlanetInfo> = listOf(
    PlanetInfo(
        nameEn = "Mercury",
        nameRes = R.string.mercury_name,
        imageRes = R.drawable.planet_mercury,
        typeRes = R.string.planet_type_terrestrial,
        descriptionRes = R.string.planet_mercury_desc,
        distanceRes = R.string.planet_mercury_distance,
        diameterRes = R.string.planet_mercury_diameter,
        rotationRes = R.string.planet_mercury_rotation,
        revolutionRes = R.string.planet_mercury_revolution,
        factRes = R.string.planet_mercury_fact,
        color = Color(0xFF8A8FA8)
    ),
    PlanetInfo(
        nameEn = "Venus",
        nameRes = R.string.venus_name,
        imageRes = R.drawable.planet_venus,
        typeRes = R.string.planet_type_terrestrial,
        descriptionRes = R.string.planet_venus_desc,
        distanceRes = R.string.planet_venus_distance,
        diameterRes = R.string.planet_venus_diameter,
        rotationRes = R.string.planet_venus_rotation,
        revolutionRes = R.string.planet_venus_revolution,
        factRes = R.string.planet_venus_fact,
        color = Color(0xFFD4906A)
    ),
    PlanetInfo(
        nameEn = "Earth",
        nameRes = R.string.earth_name,
        imageRes = R.drawable.planet_earth,
        typeRes = R.string.planet_type_terrestrial,
        descriptionRes = R.string.planet_earth_desc,
        distanceRes = R.string.planet_earth_distance,
        diameterRes = R.string.planet_earth_diameter,
        rotationRes = R.string.planet_earth_rotation,
        revolutionRes = R.string.planet_earth_revolution,
        factRes = R.string.planet_earth_fact,
        color = Color(0xFF2E6FA3)
    ),
    PlanetInfo(
        nameEn = "Mars",
        nameRes = R.string.mars_name,
        imageRes = R.drawable.planet_mars,
        typeRes = R.string.planet_type_terrestrial,
        descriptionRes = R.string.planet_mars_desc,
        distanceRes = R.string.planet_mars_distance,
        diameterRes = R.string.planet_mars_diameter,
        rotationRes = R.string.planet_mars_rotation,
        revolutionRes = R.string.planet_mars_revolution,
        factRes = R.string.planet_mars_fact,
        color = Color(0xFFC0392B)
    ),
    PlanetInfo(
        nameEn = "Jupiter",
        nameRes = R.string.jupiter_name,
        imageRes = R.drawable.planet_jupiter,
        typeRes = R.string.planet_type_gas_giant,
        descriptionRes = R.string.planet_jupiter_desc,
        distanceRes = R.string.planet_jupiter_distance,
        diameterRes = R.string.planet_jupiter_diameter,
        rotationRes = R.string.planet_jupiter_rotation,
        revolutionRes = R.string.planet_jupiter_revolution,
        factRes = R.string.planet_jupiter_fact,
        color = Color(0xFFC8956A)
    ),
    PlanetInfo(
        nameEn = "Saturn",
        nameRes = R.string.saturn_name,
        imageRes = R.drawable.planet_saturn,
        typeRes = R.string.planet_type_gas_giant,
        descriptionRes = R.string.planet_saturn_desc,
        distanceRes = R.string.planet_saturn_distance,
        diameterRes = R.string.planet_saturn_diameter,
        rotationRes = R.string.planet_saturn_rotation,
        revolutionRes = R.string.planet_saturn_revolution,
        factRes = R.string.planet_saturn_fact,
        color = Color(0xFFD4B060)
    ),
    PlanetInfo(
        nameEn = "Uranus",
        nameRes = R.string.uranus_name,
        imageRes = R.drawable.planet_uranus,
        typeRes = R.string.planet_type_ice_giant,
        descriptionRes = R.string.planet_uranus_desc,
        distanceRes = R.string.planet_uranus_distance,
        diameterRes = R.string.planet_uranus_diameter,
        rotationRes = R.string.planet_uranus_rotation,
        revolutionRes = R.string.planet_uranus_revolution,
        factRes = R.string.planet_uranus_fact,
        color = Color(0xFF5AC8D8)
    ),
    PlanetInfo(
        nameEn = "Neptune",
        nameRes = R.string.neptune_name,
        imageRes = R.drawable.planet_neptune,
        typeRes = R.string.planet_type_ice_giant,
        descriptionRes = R.string.planet_neptune_desc,
        distanceRes = R.string.planet_neptune_distance,
        diameterRes = R.string.planet_neptune_diameter,
        rotationRes = R.string.planet_neptune_rotation,
        revolutionRes = R.string.planet_neptune_revolution,
        factRes = R.string.planet_neptune_fact,
        color = Color(0xFF3A5FD8)
    )
)
