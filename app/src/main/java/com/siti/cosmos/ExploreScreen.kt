package com.siti.cosmos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

data class PlanetData(
    val nameRes: Int,
    val imageRes: Int,
    val descRes: Int,
    val color: Color,
    val distance: String,
    val diameter: String,
    val rotation: String,
    val revolution: String,
    val factRes: Int
)

@Composable
fun getPlanetList(): List<PlanetData> = listOf(
    PlanetData(R.string.mercury_name, R.drawable.planet_mercury, R.string.mercury_description,
        Color(0xFF777C91), "57.9 ${stringResource(R.string.million)} km",
        "4,879 km", "58.6 ${stringResource(R.string.earth_days)}",
        "88 ${stringResource(R.string.earth_days)}", R.string.mercury_fact),
    PlanetData(R.string.venus_name, R.drawable.planet_venus, R.string.venus_description,
        Color(0xFFD58B55), "108.2 ${stringResource(R.string.million)} km",
        "12,104 km", "243 ${stringResource(R.string.earth_days)}",
        "225 ${stringResource(R.string.earth_days)}", R.string.venus_fact),
    PlanetData(R.string.earth_name, R.drawable.planet_earth, R.string.earth_description,
        Color(0xFF348FE0), "149.6 ${stringResource(R.string.million)} km",
        "12,742 km", "24 ${stringResource(R.string.hours)}",
        "365.25 ${stringResource(R.string.days)}", R.string.earth_fact),
    PlanetData(R.string.mars_name, R.drawable.planet_mars, R.string.mars_description,
        Color(0xFFD65B50), "227.9 ${stringResource(R.string.million)} km",
        "6,779 km", "24.6 ${stringResource(R.string.hours)}",
        "687 ${stringResource(R.string.earth_days)}", R.string.mars_fact),
    PlanetData(R.string.jupiter_name, R.drawable.planet_jupiter, R.string.jupiter_description,
        Color(0xFFC58A64), "778.5 ${stringResource(R.string.million)} km",
        "139,820 km", "9.9 ${stringResource(R.string.hours)}",
        "11.86 ${stringResource(R.string.earth_years)}", R.string.jupiter_fact),
    PlanetData(R.string.saturn_name, R.drawable.planet_saturn, R.string.saturn_description,
        Color(0xFFD9B56D), "1,434 ${stringResource(R.string.million)} km",
        "116,460 km", "10.7 ${stringResource(R.string.hours)}",
        "29.45 ${stringResource(R.string.earth_years)}", R.string.saturn_fact),
    PlanetData(R.string.uranus_name, R.drawable.planet_uranus, R.string.uranus_description,
        Color(0xFF65C9D6), "2,871 ${stringResource(R.string.million)} km",
        "50,724 km", "17.2 ${stringResource(R.string.hours)}",
        "84 ${stringResource(R.string.earth_years)}", R.string.uranus_fact),
    PlanetData(R.string.neptune_name, R.drawable.planet_neptune, R.string.neptune_description,
        Color(0xFF4668DB), "4,495 ${stringResource(R.string.million)} km",
        "49,244 km", "16.1 ${stringResource(R.string.hours)}",
        "164.8 ${stringResource(R.string.earth_years)}", R.string.neptune_fact),
)

@Composable
fun ExploreScreen(
    darkTheme: Boolean = false,
    onPlanetClick: (PlanetData) -> Unit
) {
    val planets = getPlanetList()
    var searchQuery by remember { mutableStateOf("") }

    val filteredPlanets = planets.filter { planet ->
        stringResource(planet.nameRes).contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.explore_planets),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search_planets),
                color = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor    = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor  = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor  = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(Modifier.height(16.dp))

        if (filteredPlanets.isEmpty()) {
            Text(stringResource(R.string.no_planets_found),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 20.dp))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredPlanets) { planet ->
                    PlanetGridCard(planet = planet, onClick = { onPlanetClick(planet) })
                }
            }
        }
    }
}

@Composable
fun PlanetGridCard(planet: PlanetData, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(190.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(planet.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(planet.imageRes).crossfade(true).build(),
                    contentDescription = stringResource(planet.nameRes),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(stringResource(planet.nameRes),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(stringResource(planet.descRes),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
