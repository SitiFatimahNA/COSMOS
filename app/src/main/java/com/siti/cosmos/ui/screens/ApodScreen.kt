package com.siti.cosmos.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.siti.cosmos.data.model.ApodResponse
import com.siti.cosmos.data.model.FavoriteItem
import com.siti.cosmos.data.model.FavoriteType
import com.siti.cosmos.ui.components.*
import com.siti.cosmos.ui.theme.*
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
            ApodTopBar(
                onBack       = onBack,
                onPickDate   = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            vm.loadApod("%04d-%02d-%02d".format(y, m + 1, d))
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                selectedDate = state.selectedDate ?: ""
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // ── Loading ────────────────────────────────────────────────────
                state.isLoading && state.apod == null -> {
                    ApodLoadingSkeleton()
                }

                // ── Error (no cached data) ────────────────────────────────────
                state.error != null && state.apod == null -> {
                    ErrorState(
                        message = state.error!!,
                        onRetry = { vm.retry() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // ── Content (live or cached) ───────────────────────────────────
                state.apod != null -> {
                    val apod  = state.apod!!
                    val isFav = favVm.isFavorite(apod.date)

                    ApodContent(
                        apod      = apod,
                        isFav     = isFav,
                        fromCache = state.fromCache,
                        cacheLabel = state.cacheLabel,
                        isStale   = state.error != null,  // showing cache but had error
                        staleMsg  = state.error,
                        onFavClick = {
                            favVm.toggle(
                                FavoriteItem(
                                    id          = apod.date,
                                    type        = FavoriteType.APOD,
                                    title       = apod.title,
                                    imageUrl    = apod.url,
                                    date        = apod.date,
                                    description = apod.explanation
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApodTopBar(
    onBack: () -> Unit,
    onPickDate: () -> Unit,
    selectedDate: String
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text  = stringResource(R.string.nav_apod),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (selectedDate.isNotBlank()) {
                    Text(
                        text  = selectedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
            // Date picker action
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

// ── Loading Skeleton ──────────────────────────────────────────────────────────
@Composable
private fun ApodLoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero placeholder
        ShimmerBox(
            modifier     = Modifier.fillMaxWidth().height(360.dp),
            cornerRadius = 0.dp
        )
        Column(
            modifier = Modifier.padding(Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
        ) {
            ShimmerBox(Modifier.fillMaxWidth(0.65f).height(28.dp), Dimens.radiusSm)
            ShimmerBox(Modifier.fillMaxWidth(0.4f).height(16.dp),  Dimens.radiusSm)
            Spacer(Modifier.height(Dimens.spaceSm))
            repeat(5) {
                ShimmerBox(Modifier.fillMaxWidth().height(14.dp), Dimens.radiusXs)
                Spacer(Modifier.height(Dimens.spaceXs))
            }
            ShimmerBox(Modifier.fillMaxWidth(0.7f).height(14.dp), Dimens.radiusXs)
        }
    }
}

// ── Main Content ──────────────────────────────────────────────────────────────
@Composable
private fun ApodContent(
    apod: ApodResponse,
    isFav: Boolean,
    fromCache: Boolean,
    cacheLabel: String,
    isStale: Boolean,
    staleMsg: String?,
    onFavClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Cache/stale banner ─────────────────────────────────────────────────
        if (cacheLabel.isNotBlank()) {
            CacheBanner(label = cacheLabel)
        }
        if (isStale && staleMsg != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = Dimens.screenPaddingMd, vertical = Dimens.spaceSm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs)
            ) {
                Icon(
                    Icons.Outlined.Warning,
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(Dimens.iconSm)
                )
                Text(
                    text  = staleMsg,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Hero ───────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
        ) {
            if (apod.mediaType == "video") {
                ApodVideoHero(url = apod.url, title = apod.title)
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(apod.hdUrl ?: apod.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = apod.title,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            }

            // Bottom gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f   to Color.Transparent,
                                0.5f to Color.Transparent,
                                1f   to MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )

            // Favourite button — top right
            FavouriteButton(
                isFavorite = isFav,
                onClick    = onFavClick,
                modifier   = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimens.spaceMd)
            )
        }

        // ── Metadata ───────────────────────────────────────────────────────────
        Column(
            modifier = Modifier.padding(
                horizontal = Dimens.screenPadding,
                vertical   = Dimens.spaceLg
            )
        ) {
            // Title
            Text(
                text      = apod.title,
                style     = MaterialTheme.typography.headlineSmall,
                color     = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(Dimens.spaceMd))

            // Tags row
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                TagChip(
                    label = apod.date,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor     = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (!apod.copyright.isNullOrBlank()) {
                    TagChip(
                        label = "© ${apod.copyright.trim()}",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor     = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (apod.mediaType == "video") {
                    TagChip(
                        label          = "VIDEO",
                        containerColor = SoftViolet.copy(alpha = 0.2f),
                        labelColor     = SoftViolet
                    )
                }
            }

            SectionDivider()

            // Explanation — expandable
            val displayText = if (expanded || apod.explanation.length <= 320) {
                apod.explanation
            } else {
                apod.explanation.take(320) + "…"
            }

            Text(
                text      = displayText,
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )

            if (apod.explanation.length > 320) {
                Spacer(Modifier.height(Dimens.spaceSm))
                TextButton(
                    onClick         = { expanded = !expanded },
                    contentPadding  = PaddingValues(0.dp)
                ) {
                    Text(
                        text  = if (expanded) stringResource(R.string.show_less)
                                else stringResource(R.string.read_more),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.iconSm)
                    )
                }
            }

            Spacer(Modifier.height(Dimens.spaceLg))
        }
    }
}

// ── Video Hero ────────────────────────────────────────────────────────────────
@Composable
private fun ApodVideoHero(url: String, title: String) {
    Box(
        modifier          = Modifier.fillMaxSize().background(SpaceNavy),
        contentAlignment  = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url).crossfade(true).build(),
            contentDescription = title,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )
        // Semi-transparent overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.4f))
        )
        // Play button
        Box(
            modifier = Modifier
                .size(Dimens.space6xl)
                .clip(CircleShape)
                .background(Color.White.copy(0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.PlayArrow,
                contentDescription = "Video",
                tint     = White,
                modifier = Modifier.size(Dimens.space4xl)
            )
        }
        // Video label bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Dimens.spaceLg)
                .clip(RoundedCornerShape(Dimens.radiusXs))
                .background(SoftViolet.copy(0.85f))
                .padding(horizontal = Dimens.spaceSm, vertical = 3.dp)
        ) {
            Text(
                text  = stringResource(R.string.apod_video_hint),
                style = MaterialTheme.typography.labelSmall,
                color = White
            )
        }
    }
}

// ── Legacy Chip composable kept for backward compat ───────────────────────────
@Composable
fun Chip(label: String) {
    TagChip(label = label)
}
