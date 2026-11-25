package com.siti.cosmos.ui.screens

import android.app.DatePickerDialog
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.siti.cosmos.R
import com.siti.cosmos.data.model.FavoriteItem
import com.siti.cosmos.data.model.FavoriteType
import com.siti.cosmos.data.model.MarsPhoto
import com.siti.cosmos.ui.components.*
import com.siti.cosmos.ui.viewmodel.FavoritesViewModel
import com.siti.cosmos.ui.viewmodel.MarsViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarsScreen(
    onBack: () -> Unit,
    favVm: FavoritesViewModel
) {
    val vm: MarsViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val rovers = listOf("curiosity", "opportunity", "spirit")

    Scaffold(
        topBar = {
            CosmosTopBar(
                title    = stringResource(R.string.nav_mars),
                showBack = true,
                onBack   = onBack,
                actions  = {
                    TextButton(onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d ->
                            val date = "%04d-%02d-%02d".format(y, m + 1, d)
                            vm.loadPhotos(date = date)
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Text(stringResource(R.string.pick_date),
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ── Rover selector ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rovers.forEach { rover ->
                    FilterChip(
                        selected = state.selectedRover == rover,
                        onClick  = { vm.selectRover(rover) },
                        label    = { Text(rover.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // ── Grid content ───────────────────────────────────────────────────
            when {
                state.isLoading && state.photos.isEmpty() -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(6) { ShimmerBox(Modifier.fillMaxWidth().height(160.dp)) }
                    }
                }
                state.error != null && state.photos.isEmpty() -> ErrorState(
                    message = state.error!!,
                    onRetry = { vm.retry() },
                    modifier = Modifier.fillMaxSize().padding(32.dp)
                )
                state.photos.isEmpty() -> EmptyState(
                    message = stringResource(R.string.mars_empty),
                    emoji = "🤖",
                    modifier = Modifier.fillMaxSize()
                )
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Cache banner as first full-width item
                        if (state.cacheLabel.isNotBlank()) {
                            item(span = { GridItemSpan(2) }) {
                                CacheBanner(label = state.cacheLabel)
                            }
                        }
                        items(state.photos, key = { it.id }) { photo ->
                            MarsPhotoCard(
                                photo = photo,
                                isFav = favVm.isFavorite(photo.id.toString()),
                                onFavClick = {
                                    favVm.toggle(
                                        FavoriteItem(
                                            id = photo.id.toString(),
                                            type = FavoriteType.MARS,
                                            title = "${photo.rover.name} — ${photo.camera.fullName}",
                                            imageUrl = photo.imgSrc,
                                            date = photo.earthDate,
                                            description = "Sol ${photo.sol} | ${photo.camera.fullName}"
                                        )
                                    )
                                }
                            )
                        }
                        // Load more trigger
                        if (!state.isLoading) {
                            item(span = { GridItemSpan(2) }) {
                                Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                                    OutlinedButton(onClick = {
                                        vm.loadPhotos(
                                            rover = state.selectedRover,
                                            date  = state.selectedDate.ifBlank { null },
                                            page  = state.page + 1
                                        )
                                    }) {
                                        Text(stringResource(R.string.load_more))
                                    }
                                }
                            }
                        }
                        if (state.isLoading) {
                            item(span = { GridItemSpan(2) }) {
                                Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarsPhotoCard(photo: MarsPhoto, isFav: Boolean, onFavClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.imgSrc).crossfade(true).build(),
            contentDescription = photo.camera.fullName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(160.dp)
        )
        // Overlay info at bottom
        Column(
            modifier = Modifier.align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(
                            androidx.compose.ui.graphics.Color.Transparent,
                            androidx.compose.ui.graphics.Color.Black.copy(0.75f)
                        )
                    )
                )
                .padding(8.dp)
        ) {
            Text(photo.camera.name,
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 10.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(photo.earthDate,
                color = androidx.compose.ui.graphics.Color.White.copy(0.7f),
                fontSize = 9.sp)
        }
        // Favourite button
        Box(
            modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
                .size(28.dp).clip(RoundedCornerShape(50))
                .background(androidx.compose.ui.graphics.Color.Black.copy(0.5f))
                .clickable { onFavClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(if (isFav) "♥" else "♡",
                color = if (isFav) androidx.compose.ui.graphics.Color(0xFFE74C3C)
                else androidx.compose.ui.graphics.Color.White,
                fontSize = 12.sp)
        }
    }
}
