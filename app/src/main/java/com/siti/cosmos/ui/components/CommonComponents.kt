package com.siti.cosmos.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.siti.cosmos.ui.theme.*

// ══════════════════════════════════════════════════════════════════════════════
// COSMOS DESIGN SYSTEM — Reusable UI Components
// All sizes reference Dimens tokens. All colors use MaterialTheme.colorScheme.
// ══════════════════════════════════════════════════════════════════════════════

// ── 1. CacheBanner — offline / stale data indicator ───────────────────────────
@Composable
fun CacheBanner(label: String, modifier: Modifier = Modifier) {
    if (label.isBlank()) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = Dimens.screenPaddingMd, vertical = Dimens.spaceSm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs)
    ) {
        Icon(
            imageVector        = Icons.Outlined.WifiOff,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier           = Modifier.size(Dimens.iconSm)
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

// ── 2. ShimmerBox — animated loading placeholder ──────────────────────────────
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = Dimens.radiusMd
) {
    val baseColor    = MaterialTheme.colorScheme.surfaceVariant
    val shineColor   = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
    val shimmerColors = listOf(baseColor, shineColor, baseColor)

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -600f,
        targetValue  =  600f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colors = shimmerColors,
                    start  = Offset(translateX, 0f),
                    end    = Offset(translateX + 600f, 0f)
                )
            )
    )
}

// ── 3. ShimmerRow — multiple shimmer lines (for text skeletons) ───────────────
@Composable
fun ShimmerTextBlock(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(Dimens.spaceMd),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
    ) {
        ShimmerBox(Modifier.fillMaxWidth(0.55f).height(22.dp))
        ShimmerBox(Modifier.fillMaxWidth().height(14.dp))
        ShimmerBox(Modifier.fillMaxWidth().height(14.dp))
        ShimmerBox(Modifier.fillMaxWidth(0.75f).height(14.dp))
    }
}

// ── 4. ErrorState — network / API error ──────────────────────────────────────
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.space3xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.WifiOff,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onErrorContainer,
                modifier           = Modifier.size(Dimens.iconXl)
            )
        }
        Spacer(Modifier.height(Dimens.spaceLg))
        Text(
            text      = message,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Dimens.spaceXl))
        Button(
            onClick = onRetry,
            shape   = RoundedCornerShape(Dimens.radiusSm),
            colors  = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconSm)
            )
            Spacer(Modifier.width(Dimens.spaceSm))
            Text("Retry", style = MaterialTheme.typography.labelLarge, color = White)
        }
    }
}

// ── 5. EmptyState — no data available ────────────────────────────────────────
@Composable
fun EmptyState(
    message: String,
    icon: ImageVector = Icons.Outlined.SearchOff,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.space3xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier           = Modifier.size(Dimens.space4xl)
            )
        }
        Spacer(Modifier.height(Dimens.spaceLg))
        Text(
            text      = message,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

// Legacy overload — keeps existing callers with emoji param working
@Composable
fun EmptyState(
    message: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    EmptyState(
        message  = message,
        icon     = Icons.Outlined.SearchOff,
        modifier = modifier
    )
}

// ── 6. SectionHeader ─────────────────────────────────────────────────────────
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text      = title,
            style     = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color     = MaterialTheme.colorScheme.onBackground
        )
        if (subtitle != null) {
            Spacer(Modifier.height(Dimens.spaceXxs))
            Text(
                text  = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── 7. SpaceImageCard — standard content card with image ─────────────────────
@Composable
fun SpaceImageCard(
    imageUrl: String,
    title: String,
    subtitle: String? = null,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        onClick   = onClick,
        shape     = RoundedCornerShape(Dimens.radiusLg),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevationSm)
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl).crossfade(true).build(),
                contentDescription = title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(Dimens.imageHeightWide)
                    .clip(
                        RoundedCornerShape(
                            topStart = Dimens.radiusLg,
                            topEnd   = Dimens.radiusLg,
                            bottomStart = 0.dp,
                            bottomEnd   = 0.dp
                        )
                    )
            )

            // Bottom gradient for legibility
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.imageHeightWide)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f   to Color.Transparent,
                                0.6f to Color.Transparent,
                                1f   to Color.Black.copy(alpha = 0.35f)
                            )
                        )
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = Dimens.radiusLg,
                            topEnd   = Dimens.radiusLg
                        )
                    )
            )

            // Favourite button — top-right, Dimens.touchTarget for a11y
            if (onFavoriteClick != null) {
                FavouriteButton(
                    isFavorite = isFavorite,
                    onClick    = onFavoriteClick,
                    modifier   = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimens.spaceSm)
                )
            }
        }

        // Metadata row
        Column(
            modifier = Modifier.padding(
                horizontal = Dimens.spaceMd,
                vertical   = Dimens.spaceMd
            )
        ) {
            Text(
                text      = title,
                style     = MaterialTheme.typography.titleSmall,
                color     = MaterialTheme.colorScheme.onSurface,
                maxLines  = 2,
                overflow  = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(Modifier.height(Dimens.spaceXxs))
                Text(
                    text    = subtitle,
                    style   = MaterialTheme.typography.labelMedium,
                    color   = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ── 8. FavouriteButton — reusable heart icon button ──────────────────────────
@Composable
fun FavouriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = Dimens.touchTarget
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.38f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite)
                Icons.Outlined.Favorite
            else
                Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint     = if (isFavorite) AlertRed else White,
            modifier = Modifier.size(Dimens.iconMd)
        )
    }
}

// ── 9. CosmosTopBar — unified top app bar ────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosmosTopBar(
    title: String,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text  = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(
                    onClick  = onBack,
                    modifier = Modifier.size(Dimens.touchTarget)
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = MaterialTheme.colorScheme.onSurface,
                        modifier           = Modifier.size(Dimens.iconMd)
                    )
                }
            }
        },
        actions = actions,
        colors  = TopAppBarDefaults.topAppBarColors(
            containerColor       = MaterialTheme.colorScheme.surface,
            titleContentColor    = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

// ── 10. HazardBadge — NEO potentially hazardous indicator ───────────────────
@Composable
fun HazardBadge(isHazardous: Boolean, modifier: Modifier = Modifier) {
    if (!isHazardous) return
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radiusXs))
            .background(AlertRed.copy(alpha = 0.15f))
            .padding(horizontal = Dimens.spaceSm, vertical = Dimens.spaceXxs + Dimens.spaceXxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXxs)
    ) {
        Icon(
            imageVector        = Icons.Outlined.Warning,
            contentDescription = null,
            tint               = AlertRed,
            modifier           = Modifier.size(Dimens.iconSm)
        )
        Text(
            text  = "Hazardous",
            style = MaterialTheme.typography.labelSmall,
            color = AlertRed,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── 11. SpaceGradientBackground — legacy compat helper ───────────────────────
@Composable
fun SpaceGradientBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        content = content
    )
}

// ── 12. TagChip — small label pill (date, copyright, type) ───────────────────
@Composable
fun TagChip(
    label: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    labelColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radiusFull))
            .background(containerColor)
            .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceXxs + 2.dp)
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelMedium,
            color = labelColor
        )
    }
}

// ── 13. SectionDivider — subtle horizontal rule between sections ──────────────
@Composable
fun SectionDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier  = modifier.padding(vertical = Dimens.spaceLg),
        thickness = 1.dp,
        color     = MaterialTheme.colorScheme.outlineVariant
    )
}
