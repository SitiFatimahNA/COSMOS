package com.siti.cosmos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.siti.cosmos.R
import com.siti.cosmos.data.model.FavoriteItem
import com.siti.cosmos.data.model.FavoriteType
import com.siti.cosmos.ui.components.CosmosTopBar
import com.siti.cosmos.ui.components.EmptyState
import com.siti.cosmos.ui.theme.*
import com.siti.cosmos.ui.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    favVm: FavoritesViewModel
) {
    val favorites by favVm.favorites.collectAsStateWithLifecycle()

    // Group by type
    val grouped = favorites.groupBy { it.type }
    val typeOrder = listOf(FavoriteType.APOD, FavoriteType.MARS, FavoriteType.NEO)

    Scaffold(
        topBar = {
            CosmosTopBar(
                title    = stringResource(R.string.nav_favs),
                showBack = true,
                onBack   = onBack,
                actions  = {
                    if (favorites.isNotEmpty()) {
                        // Count badge
                        Box(
                            modifier = Modifier.padding(end = Dimens.spaceMd)
                        ) {
                            Text(
                                text  = "${favorites.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (favorites.isEmpty()) {
                // ── Empty state ────────────────────────────────────────────────
                FavoritesEmptyState(modifier = Modifier.align(Alignment.Center))
            } else {
                // ── Grouped list ───────────────────────────────────────────────
                LazyColumn(
                    contentPadding      = PaddingValues(
                        horizontal = Dimens.screenPadding,
                        vertical   = Dimens.spaceLg
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
                    modifier            = Modifier.fillMaxSize()
                ) {
                    typeOrder.forEach { type ->
                        val items = grouped[type] ?: return@forEach
                        if (items.isEmpty()) return@forEach

                        // Section label
                        item(key = "header_${type.name}") {
                            FavSectionHeader(type = type)
                            Spacer(Modifier.height(Dimens.spaceXs))
                        }

                        items(items, key = { it.id }) { item ->
                            var visible by remember { mutableStateOf(true) }
                            AnimatedVisibility(
                                visible  = visible,
                                exit     = shrinkVertically(tween(220)) + fadeOut(tween(220))
                            ) {
                                FavoriteCard(
                                    item     = item,
                                    onDelete = {
                                        visible = false
                                        favVm.remove(item.id)
                                    }
                                )
                            }
                        }

                        item(key = "spacer_${type.name}") {
                            Spacer(Modifier.height(Dimens.spaceMd))
                        }
                    }
                }
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────
@Composable
private fun FavoritesEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.padding(Dimens.space3xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(Dimens.radiusXl))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f),
                modifier = Modifier.size(Dimens.space4xl)
            )
        }
        Text(
            text      = stringResource(R.string.favorites_empty),
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

// ── Section header ────────────────────────────────────────────────────────────
@Composable
private fun FavSectionHeader(type: FavoriteType) {
    val (icon, label, tint) = when (type) {
        FavoriteType.APOD -> Triple(Icons.Outlined.PhotoCamera, "Astronomy Picture", MaterialTheme.colorScheme.primary)
        FavoriteType.MARS -> Triple(Icons.Outlined.Explore,     "Mars Exploration",  Color(0xFFC0392B))
        FavoriteType.NEO  -> Triple(Icons.Outlined.Stars,       "Near Earth Objects",MaterialTheme.colorScheme.secondary)
    }
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
        modifier              = Modifier.padding(vertical = Dimens.spaceXs)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = tint,
            modifier           = Modifier.size(Dimens.iconSm)
        )
        Text(
            text      = label,
            style     = MaterialTheme.typography.labelLarge,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Favorite card ─────────────────────────────────────────────────────────────
@Composable
private fun FavoriteCard(item: FavoriteItem, onDelete: () -> Unit) {
    val typeAccent = when (item.type) {
        FavoriteType.APOD -> MaterialTheme.colorScheme.primary
        FavoriteType.MARS -> Color(0xFFC0392B)
        FavoriteType.NEO  -> MaterialTheme.colorScheme.secondary
    }
    val typeName = when (item.type) {
        FavoriteType.APOD -> "APOD"
        FavoriteType.MARS -> "MARS"
        FavoriteType.NEO  -> "NEO"
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(Dimens.radiusMd),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(Dimens.elevationNone)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceMd),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            // ── Thumbnail ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(Dimens.imageSizeThumbnail)
                    .clip(RoundedCornerShape(Dimens.radiusSm))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (item.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl).crossfade(true).build(),
                        contentDescription = item.title,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                } else {
                    // Vector icon placeholder per type
                    Box(
                        modifier         = Modifier
                            .fillMaxSize()
                            .background(typeAccent.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (item.type) {
                                FavoriteType.APOD -> Icons.Outlined.PhotoCamera
                                FavoriteType.MARS -> Icons.Outlined.Explore
                                FavoriteType.NEO  -> Icons.Outlined.Stars
                            },
                            contentDescription = null,
                            tint     = typeAccent,
                            modifier = Modifier.size(Dimens.iconXl)
                        )
                    }
                }
            }

            // ── Info ───────────────────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                // Type tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.radiusXs))
                        .background(typeAccent.copy(alpha = 0.15f))
                        .padding(horizontal = Dimens.spaceSm, vertical = Dimens.spaceXxs)
                ) {
                    Text(
                        text  = typeName,
                        style = MaterialTheme.typography.labelSmall,
                        color = typeAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(Dimens.spaceXxs + 1.dp))
                Text(
                    text      = item.title,
                    style     = MaterialTheme.typography.titleSmall,
                    color     = MaterialTheme.colorScheme.onSurface,
                    maxLines  = 2,
                    overflow  = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(Dimens.spaceXxs))
                Text(
                    text  = item.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Delete button ──────────────────────────────────────────────────
            IconButton(
                onClick  = onDelete,
                modifier = Modifier.size(Dimens.touchTarget)
            ) {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete",
                    tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f),
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }
        }
    }
}
