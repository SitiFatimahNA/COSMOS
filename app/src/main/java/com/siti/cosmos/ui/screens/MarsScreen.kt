package com.siti.cosmos.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.siti.cosmos.R
import com.siti.cosmos.data.model.FavoriteItem
import com.siti.cosmos.data.model.FavoriteType
import com.siti.cosmos.data.model.MarsPhoto
import com.siti.cosmos.ui.components.*
import com.siti.cosmos.ui.theme.*
import com.siti.cosmos.ui.viewmodel.FavoritesViewModel
import com.siti.cosmos.ui.viewmodel.MarsViewModel
import java.util.Calendar

// ── Rover metadata ─────────────────────────────────────────────────────────────
private data class RoverInfo(
    val id: String,
    val displayName: String,
    val accentColor: Color,
    val statusLabel: String
)

private val roverList = listOf(
    RoverInfo("curiosity",   "Curiosity",   Color(0xFFC0392B), "Active"),
    RoverInfo("opportunity", "Opportunity", Color(0xFFE67E22), "Mission complete"),
    RoverInfo("spirit",      "Spirit",      Color(0xFFD4A017), "Mission complete"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarsScreen(
    onBack: () -> Unit,
    favVm: FavoritesViewModel
) {
    val vm: MarsViewModel = viewModel()
    val state    by vm.state.collectAsStateWithLifecycle()
    val context  = LocalContext.current

    Scaffold(
        topBar = {
            MarsTopBar(
                onBack     = onBack,
                onPickDate = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            vm.loadPhotos(date = "%04d-%02d-%02d".format(y, m + 1, d))
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                selectedDate  = state.selectedDate,
                selectedRover = state.selectedRover
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Cache banner ───────────────────────────────────────────────────
            if (state.cacheLabel.isNotBlank()) {
                CacheBanner(label = state.cacheLabel)
            }

            // ── Rover selector ─────────────────────────────────────────────────
            RoverSelector(
                rovers        = roverList,
                selectedRover = state.selectedRover,
                onSelect      = { vm.selectRover(it) }
            )

            // ── Grid content ───────────────────────────────────────────────────
            when {
                state.isLoading && state.photos.isEmpty() -> {
                    LazyVerticalGrid(
                        columns               = GridCells.Fixed(2),
                        contentPadding        = PaddingValues(Dimens.spaceLg),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacing),
                        verticalArrangement   = Arrangement.spacedBy(Dimens.itemSpacing),
                        modifier              = Modifier.fillMaxSize()
                    ) {
                        items(6) {
                            ShimmerBox(
                                modifier     = Modifier.fillMaxWidth().height(180.dp),
                                cornerRadius = Dimens.radiusMd
                            )
                        }
                    }
                }

                state.error != null && state.photos.isEmpty() -> {
                    ErrorState(
                        message  = state.error!!,
                        onRetry  = { vm.retry() },
                        modifier = Modifier.fillMaxSize().padding(Dimens.space3xl)
                    )
                }

                state.photos.isEmpty() -> {
                    EmptyState(
                        message  = stringResource(R.string.mars_empty),
                        icon     = Icons.Outlined.PhotoCamera,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns               = GridCells.Fixed(2),
                        contentPadding        = PaddingValues(Dimens.spaceLg),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacing),
                        verticalArrangement   = Arrangement.spacedBy(Dimens.itemSpacing),
                        modifier              = Modifier.weight(1f)
                    ) {
                        items(state.photos, key = { it.id }) { photo ->
                            MarsPhotoCard(
                                photo      = photo,
                                isFav      = favVm.isFavorite(photo.id.toString()),
                                onFavClick = {
                                    favVm.toggle(
                                        FavoriteItem(
                                            id          = photo.id.toString(),
                                            type        = FavoriteType.MARS,
                                            title       = "${photo.rover.name} — ${photo.camera.fullName}",
                                            imageUrl    = photo.imgSrc,
                                            date        = photo.earthDate,
                                            description = "Sol ${photo.sol} | ${photo.camera.fullName}"
                                        )
                                    )
                                }
                            )
                        }

                        // Load more
                        if (!state.isLoading) {
                            item(span = { GridItemSpan(2) }) {
                                LoadMoreButton(
                                    onClick = {
                                        vm.loadPhotos(
                                            rover = state.selectedRover,
                                            date  = state.selectedDate.ifBlank { null },
                                            page  = state.page + 1
                                        )
                                    }
                                )
                            }
                        }

                        // Pagination loading
                        if (state.isLoading && state.photos.isNotEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    modifier         = Modifier.fillMaxWidth().padding(Dimens.spaceLg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier    = Modifier.size(Dimens.space3xl),
                                        strokeWidth = 2.dp,
                                        color       = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarsTopBar(
    onBack: () -> Unit,
    onPickDate: () -> Unit,
    selectedDate: String,
    selectedRover: String
) {
    val rover = roverList.find { it.id == selectedRover }
    TopAppBar(
        title = {
            Column {
                Text(
                    text  = stringResource(R.string.nav_mars),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val subtitle = buildString {
                    append(rover?.displayName ?: selectedRover.replaceFirstChar { it.uppercase() })
                    if (selectedDate.isNotBlank()) append(" · $selectedDate")
                }
                Text(
                    text  = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_to_explore),
                    tint     = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }
        },
        actions = {
            IconButton(onClick = onPickDate) {
                Icon(
                    Icons.Outlined.DateRange,
                    contentDescription = stringResource(R.string.pick_date),
                    tint     = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

// ── Rover Selector ────────────────────────────────────────────────────────────
@Composable
private fun RoverSelector(
    rovers: List<RoverInfo>,
    selectedRover: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spaceSm),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
    ) {
        rovers.forEach { rover ->
            val selected = selectedRover == rover.id
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.radiusFull))
                    .background(
                        if (selected) rover.accentColor
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = { onSelect(rover.id) }
                    )
                    .padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceXs + 2.dp)
            ) {
                Text(
                    text  = rover.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ── Mars Photo Card ───────────────────────────────────────────────────────────
@Composable
fun MarsPhotoCard(
    photo: MarsPhoto,
    isFav: Boolean,
    onFavClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Photo
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.imgSrc).crossfade(true).build(),
            contentDescription = photo.camera.fullName,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )

        // Bottom gradient + metadata
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Transparent,
                            1f to Color.Black.copy(0.80f)
                        )
                    )
                )
                .padding(Dimens.spaceSm)
        ) {
            Column {
                Text(
                    text      = photo.camera.name,
                    style     = MaterialTheme.typography.labelMedium,
                    color     = White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines  = 1,
                    overflow  = TextOverflow.Ellipsis
                )
                Text(
                    text  = photo.earthDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = White70
                )
            }
        }

        // Sol badge — top left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Dimens.spaceXs)
                .clip(RoundedCornerShape(Dimens.radiusXs))
                .background(Color.Black.copy(0.55f))
                .padding(horizontal = Dimens.spaceSm, vertical = Dimens.spaceXxs + 1.dp)
        ) {
            Text(
                text  = "Sol ${photo.sol}",
                style = MaterialTheme.typography.labelSmall,
                color = White70
            )
        }

        // Favourite button — min 48dp touch target
        FavouriteButton(
            isFavorite = isFav,
            onClick    = onFavClick,
            modifier   = Modifier
                .align(Alignment.TopEnd)
                .padding(Dimens.spaceXs),
            size       = Dimens.touchTarget
        )
    }
}

// ── Load More Button ──────────────────────────────────────────────────────────
@Composable
private fun LoadMoreButton(onClick: () -> Unit) {
    Box(
        modifier         = Modifier.fillMaxWidth().padding(vertical = Dimens.spaceSm),
        contentAlignment = Alignment.Center
    ) {
        OutlinedButton(
            onClick = onClick,
            shape   = RoundedCornerShape(Dimens.radiusFull),
            border  = ButtonDefaults.outlinedButtonBorder(enabled = true)
        ) {
            Icon(
                Icons.Outlined.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconSm)
            )
            Spacer(Modifier.width(Dimens.spaceXs))
            Text(
                text  = stringResource(R.string.load_more),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
