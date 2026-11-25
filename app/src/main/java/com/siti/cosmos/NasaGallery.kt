package com.siti.cosmos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.siti.cosmos.data.model.NasaItem
import com.siti.cosmos.data.repository.NasaRepository
import com.siti.cosmos.ui.components.SectionHeader
import com.siti.cosmos.ui.components.ShimmerBox
import com.siti.cosmos.ui.theme.*

@Composable
fun NasaGallery(darkTheme: Boolean = false) {
    var images    by remember { mutableStateOf<List<NasaItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError  by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            images = NasaRepository().getSpaceImages("nebula")
        } catch (e: Exception) {
            hasError = true
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spaceXs)
    ) {
        // ── Section header ─────────────────────────────────────────────────────
        SectionHeader(
            title    = stringResource(R.string.nasa_gallery),
            subtitle = stringResource(R.string.nasa_gallery_sub),
            modifier = Modifier.padding(horizontal = Dimens.screenPadding)
        )
        Spacer(Modifier.height(Dimens.spaceMd))

        // ── States ────────────────────────────────────────────────────────────
        when {
            isLoading -> GalleryShimmer()
            hasError  -> GalleryError(onRetry = {
                isLoading = true
                hasError  = false
            })
            images.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding)
                    .height(140.dp)
                    .clip(RoundedCornerShape(Dimens.radiusLg))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = stringResource(R.string.gallery_empty),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            else -> GalleryContent(images = images)
        }
    }
}

@Composable
private fun GalleryShimmer() {
    LazyRow(
        contentPadding       = PaddingValues(horizontal = Dimens.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacing)
    ) {
        items(4) {
            ShimmerBox(
                modifier     = Modifier
                    .width(220.dp)
                    .height(Dimens.imageHeightGallery + 44.dp),
                cornerRadius = Dimens.radiusLg
            )
        }
    }
}

@Composable
private fun GalleryError(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.screenPadding)
            .height(100.dp)
            .clip(RoundedCornerShape(Dimens.radiusLg))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            Text(
                text  = stringResource(R.string.gallery_error),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            IconButton(onClick = onRetry) {
                Icon(
                    Icons.Outlined.Refresh,
                    contentDescription = "Retry",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }
        }
    }
}

@Composable
private fun GalleryContent(images: List<NasaItem>) {
    LazyRow(
        contentPadding       = PaddingValues(horizontal = Dimens.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacing)
    ) {
        items(images) { item ->
            val imageUrl = item.links.firstOrNull()?.href
            val title    = item.data.firstOrNull()?.title ?: "Space Image"
            val date     = item.data.firstOrNull()?.date_created?.take(10) ?: ""
            GalleryCard(imageUrl = imageUrl, title = title, date = date)
        }
    }
}

@Composable
private fun GalleryCard(imageUrl: String?, title: String, date: String) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .height(Dimens.imageHeightGallery + 44.dp)
            .clip(RoundedCornerShape(Dimens.radiusLg))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = {}      // future: open detail
            )
    ) {
        // Image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl).crossfade(true).build(),
            contentDescription = title,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .fillMaxWidth()
                .height(Dimens.imageHeightGallery)
                .clip(
                    RoundedCornerShape(
                        topStart    = Dimens.radiusLg,
                        topEnd      = Dimens.radiusLg,
                        bottomStart = 0.dp,
                        bottomEnd   = 0.dp
                    )
                )
        )

        // Gradient over image bottom for legibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.TopStart)
                .padding(top = (Dimens.imageHeightGallery - 80.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(0.3f))
                    )
                )
        )

        // Metadata row
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm)
        ) {
            Text(
                text      = title,
                style     = MaterialTheme.typography.labelLarge,
                color     = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines  = 2,
                overflow  = TextOverflow.Ellipsis
            )
            if (date.isNotBlank()) {
                Spacer(Modifier.height(Dimens.spaceXxs))
                Text(
                    text  = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
