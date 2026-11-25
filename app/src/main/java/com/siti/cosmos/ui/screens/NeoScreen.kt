package com.siti.cosmos.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.siti.cosmos.R
import com.siti.cosmos.data.model.NeoObject
import com.siti.cosmos.ui.components.*
import com.siti.cosmos.ui.theme.*
import com.siti.cosmos.ui.viewmodel.NeoViewModel
import java.util.Calendar
import kotlin.math.roundToInt
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeoScreen(onBack: () -> Unit) {
    val vm    = viewModel<NeoViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            NeoTopBar(
                onBack     = onBack,
                onPickDates = {
                    // Open start-date picker; on confirm opens end-date picker
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, sy, sm, sd ->
                            val start = "%04d-%02d-%02d".format(sy, sm + 1, sd)
                            DatePickerDialog(
                                context,
                                { _, ey, em, ed ->
                                    val end = "%04d-%02d-%02d".format(ey, em + 1, ed)
                                    vm.loadNeo(startDate = start, endDate = end)
                                },
                                sy, sm, sd
                            ).show()
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
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
            when {
                // ── Loading ────────────────────────────────────────────────────
                state.isLoading -> {
                    LazyColumn(
                        contentPadding       = PaddingValues(Dimens.spaceLg),
                        verticalArrangement  = Arrangement.spacedBy(Dimens.itemSpacing)
                    ) {
                        items(5) {
                            ShimmerBox(
                                modifier     = Modifier.fillMaxWidth().height(110.dp),
                                cornerRadius = Dimens.radiusMd
                            )
                        }
                    }
                }

                // ── Error (no data) ────────────────────────────────────────────
                state.error != null && state.objects.isEmpty() -> {
                    ErrorState(
                        message  = state.error!!,
                        onRetry  = { vm.retry() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // ── Empty ──────────────────────────────────────────────────────
                state.objects.isEmpty() -> {
                    EmptyState(
                        message  = stringResource(R.string.neo_empty),
                        icon     = Icons.Outlined.Stars,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // ── Content ────────────────────────────────────────────────────
                else -> {
                    val hazardous    = state.objects.count { it.isPotentiallyHazardous }
                    val nonHazardous = state.objects.size - hazardous

                    LazyColumn(
                        contentPadding      = PaddingValues(Dimens.spaceLg),
                        verticalArrangement = Arrangement.spacedBy(Dimens.itemSpacing)
                    ) {
                        // Cache banner
                        if (state.cacheLabel.isNotBlank()) {
                            item {
                                CacheBanner(
                                    label    = state.cacheLabel,
                                    modifier = Modifier.padding(bottom = Dimens.spaceXs)
                                )
                            }
                        }

                        // Summary dashboard card
                        item {
                            NeoSummaryCard(
                                total        = state.objects.size,
                                hazardous    = hazardous,
                                nonHazardous = nonHazardous
                            )
                        }

                        // Stale error notice (showing cache but had error)
                        if (state.error != null) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(Dimens.radiusSm))
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                        .padding(Dimens.spaceMd),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
                                ) {
                                    Icon(
                                        Icons.Outlined.Warning, null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(Dimens.iconSm)
                                    )
                                    Text(
                                        text  = state.error!!,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Hazardous section header
                        if (hazardous > 0) {
                            item {
                                NeoSectionLabel(
                                    label = stringResource(R.string.neo_hazardous_section, hazardous),
                                    color = AlertRed
                                )
                            }
                            items(state.objects.filter { it.isPotentiallyHazardous }) { neo ->
                                NeoCard(neo = neo, isHazardous = true)
                            }
                        }

                        // Non-hazardous section header
                        if (nonHazardous > 0) {
                            item {
                                NeoSectionLabel(
                                    label = stringResource(R.string.neo_safe_section, nonHazardous),
                                    color = SuccessGreen
                                )
                            }
                            items(state.objects.filter { !it.isPotentiallyHazardous }) { neo ->
                                NeoCard(neo = neo, isHazardous = false)
                            }
                        }

                        item { Spacer(Modifier.height(Dimens.spaceLg)) }
                    }
                }
            }
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NeoTopBar(onBack: () -> Unit, onPickDates: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text  = stringResource(R.string.nav_neo),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
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
            IconButton(onClick = onPickDates) {
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

// ── Summary Dashboard Card ────────────────────────────────────────────────────
@Composable
private fun NeoSummaryCard(
    total: Int,
    hazardous: Int,
    nonHazardous: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.radiusLg))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            )
            .padding(Dimens.spaceXl)
    ) {
        Column {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                Icon(
                    Icons.Outlined.Stars,
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(Dimens.iconMd)
                )
                Text(
                    text  = stringResource(R.string.neo_tracking, total),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(Dimens.spaceLg))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
            ) {
                NeoSummaryStatItem(
                    count = hazardous,
                    label = stringResource(R.string.neo_stat_hazardous),
                    color = AlertRed,
                    modifier = Modifier.weight(1f)
                )
                // Vertical divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(44.dp)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(0.2f))
                )
                NeoSummaryStatItem(
                    count = nonHazardous,
                    label = stringResource(R.string.neo_stat_safe),
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(Dimens.spaceMd))
            Text(
                text  = stringResource(R.string.neo_source),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)
            )
        }
    }
}

@Composable
private fun NeoSummaryStatItem(
    count: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text  = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.Black
        )
        Text(
            text      = label,
            style     = MaterialTheme.typography.labelSmall,
            color     = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
            textAlign = TextAlign.Center
        )
    }
}

// ── Section Label ─────────────────────────────────────────────────────────────
@Composable
private fun NeoSectionLabel(label: String, color: Color) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spaceXs),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text      = label,
            style     = MaterialTheme.typography.labelLarge,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── NEO Card ──────────────────────────────────────────────────────────────────
@Composable
fun NeoCard(neo: NeoObject, isHazardous: Boolean) {
    val approach   = neo.closeApproachData.firstOrNull()
    val diamMin    = neo.estimatedDiameter.kilometers.min
    val diamMax    = neo.estimatedDiameter.kilometers.max
    val speed      = approach?.relativeVelocity?.kmPerHour?.toDoubleOrNull()?.roundToInt() ?: 0
    val distLunar  = approach?.missDistance?.lunar?.toDoubleOrNull()
    val distKm     = approach?.missDistance?.kilometers?.toDoubleOrNull()

    val borderColor = if (isHazardous) AlertRed.copy(0.4f) else Color.Transparent
    val accentColor = if (isHazardous) AlertRed else SuccessGreen

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .then(
                if (isHazardous)
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(Dimens.radiusMd))
                else Modifier
            ),
        shape     = RoundedCornerShape(Dimens.radiusMd),
        colors    = CardDefaults.cardColors(
            containerColor = if (isHazardous)
                AlertRed.copy(alpha = 0.06f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(Dimens.elevationNone)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceLg)) {

            // ── Header row ─────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                // Name + date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text      = neo.name.trim('(', ')'),
                        style     = MaterialTheme.typography.titleSmall,
                        color     = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines  = 1,
                        overflow  = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    if (approach != null) {
                        Text(
                            text  = stringResource(R.string.neo_approach_date, approach.closeApproachDate),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Hazard badge
                HazardBadge(isHazardous = isHazardous)
            }

            Spacer(Modifier.height(Dimens.spaceMd))
            HorizontalDivider(
                color     = MaterialTheme.colorScheme.outlineVariant,
                thickness = 0.5.dp
            )
            Spacer(Modifier.height(Dimens.spaceMd))

            // ── Stats row ──────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                NeoStatCell(
                    icon  = Icons.Outlined.Straighten,
                    label = stringResource(R.string.neo_diameter),
                    value = "%.2f–%.2f km".format(diamMin, diamMax),
                    modifier = Modifier.weight(1f)
                )
                NeoStatCell(
                    icon  = Icons.Outlined.Speed,
                    label = stringResource(R.string.neo_speed),
                    value = "%,d km/h".format(speed),
                    modifier = Modifier.weight(1f)
                )
                if (distLunar != null) {
                    NeoStatCell(
                        icon  = Icons.Outlined.NearMe,
                        label = stringResource(R.string.neo_distance),
                        value = "%.1f LD".format(distLunar),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ── NEO Stat Cell ─────────────────────────────────────────────────────────────
@Composable
private fun NeoStatCell(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(Dimens.radiusSm))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dimens.spaceSm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.primary,
            modifier           = Modifier.size(Dimens.iconSm)
        )
        Spacer(Modifier.height(Dimens.spaceXxs))
        Text(
            text      = value,
            style     = MaterialTheme.typography.labelMedium,
            color     = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines  = 2
        )
        Text(
            text      = label,
            style     = MaterialTheme.typography.labelSmall,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ── Legacy kept for backward compat ───────────────────────────────────────────
@Composable
fun NeoStat(label: String, value: String, modifier: Modifier = Modifier) {
    NeoStatCell(icon = Icons.Outlined.Info, label = label, value = value, modifier = modifier)
}
