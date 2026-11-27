package com.siti.cosmos.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.siti.cosmos.R
import com.siti.cosmos.ui.components.CosmosTopBar
import com.siti.cosmos.ui.components.EmptyState
import com.siti.cosmos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetsScreen(
    onBack: () -> Unit,
    onPlanetClick: (String) -> Unit
) {
    var query    by remember { mutableStateOf("") }
    val filtered  = allPlanets.filter { it.nameEn.contains(query, ignoreCase = true) }

    Scaffold(
        topBar = {
            CosmosTopBar(
                title    = stringResource(R.string.explore_planets),
                showBack = true,
                onBack   = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Search bar ─────────────────────────────────────────────────────
            PlanetsSearchBar(
                query    = query,
                onChange = { query = it },
                modifier = Modifier.padding(
                    horizontal = Dimens.screenPadding,
                    vertical   = Dimens.spaceMd
                )
            )

            // ── Grid ───────────────────────────────────────────────────────────
            if (filtered.isEmpty()) {
                EmptyState(
                    message  = stringResource(R.string.no_planets_found),
                    icon     = Icons.Outlined.SearchOff,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyVerticalGrid(
                    columns               = GridCells.Fixed(2),
                    contentPadding        = PaddingValues(
                        start  = Dimens.screenPadding,
                        end    = Dimens.screenPadding,
                        bottom = Dimens.space3xl
                    ),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacing),
                    verticalArrangement   = Arrangement.spacedBy(Dimens.itemSpacing),
                    modifier              = Modifier.weight(1f)
                ) {
                    items(filtered, key = { it.nameEn }) { planet ->
                        PlanetGridCard(
                            planet  = planet,
                            onClick = { onPlanetClick(planet.nameEn) }
                        )
                    }
                }
            }
        }
    }
}

// ── Custom search bar ─────────────────────────────────────────────────────────
@Composable
private fun PlanetsSearchBar(
    query: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = null,
            tint     = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = Dimens.spaceLg)
                .size(Dimens.iconMd)
        )
        BasicTextField(
            value        = query,
            onValueChange = onChange,
            modifier     = Modifier
                .weight(1f)
                .padding(horizontal = Dimens.spaceMd),
            singleLine   = true,
            textStyle    = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush  = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            stringResource(R.string.search_planets),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                        )
                    }
                    inner()
                }
            }
        )
        if (query.isNotEmpty()) {
            IconButton(onClick = { onChange("") }) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Clear",
                    tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.iconSm)
                )
            }
        }
    }
}

// ── Planet grid card ──────────────────────────────────────────────────────────
@Composable
private fun PlanetGridCard(planet: PlanetInfo, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "planet_card_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .scale(scale)
            .clip(RoundedCornerShape(Dimens.radiusLg))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) {
                pressed = false
                onClick()
            }
    ) {
        // Planet image — fills the whole card, cropped
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(planet.imageRes).crossfade(true).build(),
            contentDescription = planet.nameEn,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )

        // Gradient overlay — heavier at bottom for text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f   to Color.Black.copy(alpha = 0.08f),
                            0.5f to Color.Transparent,
                            1f   to Color.Black.copy(alpha = 0.82f)
                        )
                    )
                )
        )

        // Type badge — top left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Dimens.spaceSm)
                .clip(RoundedCornerShape(Dimens.radiusXs))
                .background(planet.color.copy(alpha = 0.75f))
                .padding(horizontal = Dimens.spaceSm, vertical = Dimens.spaceXxs + 1.dp)
        ) {
            Text(
                text = stringResource(planet.typeRes),   // "Terrestrial" / "Gas" / "Ice"
                style = MaterialTheme.typography.labelSmall,
                color = White
            )
        }

        // Name + description — bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(Dimens.spaceMd)
        ) {
            Text(
                text      = planet.nameEn,
                style     = MaterialTheme.typography.titleSmall,
                color     = White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(Dimens.spaceXxs))
            Text(
                text = stringResource(planet.descriptionRes),
                style    = MaterialTheme.typography.labelSmall,
                color    = White70,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
