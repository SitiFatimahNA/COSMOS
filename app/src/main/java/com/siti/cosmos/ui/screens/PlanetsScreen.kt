package com.siti.cosmos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.siti.cosmos.R
import com.siti.cosmos.ui.components.CosmosTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetsScreen(
    onBack: () -> Unit,
    onPlanetClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = allPlanets.filter { it.nameEn.contains(query, ignoreCase = true) }

    Scaffold(
        topBar = {
            CosmosTopBar(
                title    = stringResource(R.string.explore_planets),
                showBack = true,
                onBack   = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value           = query,
                onValueChange   = { query = it },
                modifier        = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder     = { Text(stringResource(R.string.search_planets),
                    color = MaterialTheme.colorScheme.onSurfaceVariant) },
                singleLine      = true,
                shape           = RoundedCornerShape(14.dp),
                colors          = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor     = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                )
            )

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_planets_found),
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyVerticalGrid(
                    columns            = GridCells.Fixed(2),
                    contentPadding     = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp),
                    modifier           = Modifier.weight(1f)
                ) {
                    items(filtered, key = { it.nameEn }) { planet ->
                        PlanetCard3D(planet = planet, onClick = { onPlanetClick(planet.nameEn) })
                    }
                }
            }
        }
    }
}

@Composable
fun PlanetCard3D(planet: PlanetInfo, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier  = Modifier.fillMaxWidth().height(200.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(planet.color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(planet.imageRes).crossfade(true).build(),
                    contentDescription = planet.nameEn,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(planet.nameEn,
                fontWeight = FontWeight.Bold, fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface)
            Text(planet.type,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
