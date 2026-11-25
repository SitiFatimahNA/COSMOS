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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ExploreScreen(
    planets: List<Planet>,
    darkTheme: Boolean,
    onPlanetClick: (Planet) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredPlanets = planets.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBlack)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.explore_universe),
            color = CosmosWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.search_planets), color = CosmosGrey, fontSize = 13.sp)
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CosmosWhite,
                unfocusedTextColor = CosmosWhite,
                focusedBorderColor = CosmosGrey,
                unfocusedBorderColor = CosmosStroke,
                cursorColor = CosmosWhite,
                focusedContainerColor = CosmosCardDark,
                unfocusedContainerColor = CosmosCardDark
            )
        )

        Spacer(Modifier.height(16.dp))

        if (filteredPlanets.isEmpty()) {
            Text(
                text = stringResource(R.string.no_planets_found),
                color = CosmosGrey,
                modifier = Modifier.padding(top = 20.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredPlanets) { planet ->
                    ExplorePlanetCard(
                        planet = planet,
                        onClick = { onPlanetClick(planet) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExplorePlanetCard(planet: Planet, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CosmosCardDark)
            .clickable { onClick() }
    ) {
        // Planet image fills the top portion
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(planet.imageRes)
                .crossfade(true)
                .build(),
            contentDescription = planet.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
        )

        // Name + type at bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(CosmosCardDark)
                .padding(12.dp)
        ) {
            Text(
                planet.name,
                color = CosmosWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                planet.type,
                color = CosmosGrey,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
