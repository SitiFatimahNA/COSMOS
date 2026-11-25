package com.siti.cosmos.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.siti.cosmos.R
import com.siti.cosmos.data.model.FavoriteItem
import com.siti.cosmos.data.model.FavoriteType
import com.siti.cosmos.ui.components.*
import com.siti.cosmos.ui.viewmodel.ApodViewModel
import com.siti.cosmos.ui.viewmodel.FavoritesViewModel
import java.util.Calendar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodScreen(
    onBack: () -> Unit,
    favVm: FavoritesViewModel
) {
    val vm: ApodViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CosmosTopBar(
                title    = stringResource(R.string.nav_apod),
                showBack = true,
                onBack   = onBack,
                actions  = {
                    // Date picker button
                    TextButton(onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d ->
                            val date = "%04d-%02d-%02d".format(y, m + 1, d)
                            vm.loadApod(date)
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Text(stringResource(R.string.pick_date), color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ShimmerBox(Modifier.fillMaxWidth().height(280.dp))
                        ShimmerBox(Modifier.fillMaxWidth(0.6f).height(24.dp))
                        ShimmerBox(Modifier.fillMaxWidth().height(16.dp))
                        ShimmerBox(Modifier.fillMaxWidth().height(16.dp))
                        ShimmerBox(Modifier.fillMaxWidth(0.8f).height(16.dp))
                    }
                }
                state.error != null -> ErrorState(
                    message = state.error!!,
                    onRetry = { vm.retry() },
                    modifier = Modifier.align(Alignment.Center)
                )
                state.apod != null -> {
                    val apod = state.apod!!
                    val isFav = favVm.isFavorite(apod.date)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Cache/offline banner
                        if (state.cacheLabel.isNotBlank()) {
                            CacheBanner(label = state.cacheLabel)
                        }
                        // ── Hero image / video ──────────────────────────────────
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                            if (apod.mediaType == "video") {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("▶  ${stringResource(R.string.apod_video_hint)}",
                                        color = Color.White, fontSize = 16.sp)
                                    // Coil cannot render video — show thumbnail from HD URL if available
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(apod.hdUrl ?: apod.url).crossfade(true).build(),
                                        contentDescription = apod.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            } else {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(apod.hdUrl ?: apod.url).crossfade(true).build(),
                                    contentDescription = apod.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            // gradient overlay at bottom
                            Box(
                                modifier = Modifier.fillMaxSize().background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                    )
                                )
                            )
                            // Favourite button
                            Box(
                                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                                    .size(40.dp).clip(RoundedCornerShape(50))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .clickable {
                                        favVm.toggle(
                                            FavoriteItem(
                                                id = apod.date, type = FavoriteType.APOD,
                                                title = apod.title, imageUrl = apod.url,
                                                date = apod.date, description = apod.explanation
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (isFav) "♥" else "♡",
                                    color = if (isFav) Color(0xFFE74C3C) else Color.White,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        // ── Info ────────────────────────────────────────────────
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    apod.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Chip(label = apod.date)
                                if (apod.copyright != null) Chip(label = "© ${apod.copyright.trim()}")
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                apod.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Chip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold)
    }
}

// Extension to allow clicks without ripple (kept for future use)
