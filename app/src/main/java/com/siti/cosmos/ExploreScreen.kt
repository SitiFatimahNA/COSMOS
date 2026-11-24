
package com.siti.cosmos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun ExploreScreen(
    planets: List<Planet>,
    onPlanetClick: (Planet) -> Unit
) {
    var searchQuery by remember {
        mutableStateOf("")
    }

    val filteredPlanets = planets.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Explore Universe",
            color = SpaceWhite,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search planets...")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredPlanets.isEmpty()) {
            Text(
                text = "No planets found.",
                color = Color.LightGray
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
                    PlanetCard(
                        planet = planet,
                        onClick = {
                            onPlanetClick(planet)
                        }
                    )
                }
            }
        }
    }
}
