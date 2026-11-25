package com.siti.cosmos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.siti.cosmos.R
import com.siti.cosmos.data.model.NeoObject
import com.siti.cosmos.ui.components.*
import com.siti.cosmos.ui.viewmodel.NeoViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeoScreen(onBack: () -> Unit) {
    val vm: NeoViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CosmosTopBar(
                title    = stringResource(R.string.nav_neo),
                showBack = true,
                onBack   = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(6) {
                        ShimmerBox(Modifier.fillMaxWidth().height(90.dp))
                    }
                }
                state.error != null -> ErrorState(
                    message = state.error!!,
                    onRetry = { vm.retry() },
                    modifier = Modifier.align(Alignment.Center)
                )
                state.objects.isEmpty() -> EmptyState(
                    message = stringResource(R.string.neo_empty),
                    emoji   = "🌌",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            // Summary banner
                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        stringResource(R.string.neo_tracking, state.objects.size),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        stringResource(R.string.neo_source),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                        items(state.objects, key = { it.id }) { neo ->
                            NeoCard(neo = neo)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NeoCard(neo: NeoObject) {
    val approach = neo.closeApproachData.firstOrNull()
    val diamMin  = neo.estimatedDiameter.kilometers.min
    val diamMax  = neo.estimatedDiameter.kilometers.max
    val speed    = approach?.relativeVelocity?.kmPerHour?.toDoubleOrNull()?.roundToInt() ?: 0
    val distLunar = approach?.missDistance?.lunar?.toDoubleOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        neo.name.trim('(', ')'),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (approach != null) {
                        Text(
                            stringResource(R.string.neo_approach_date, approach.closeApproachDate),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HazardBadge(isHazardous = neo.isPotentiallyHazardous)
            }

            Spacer(Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Spacer(Modifier.height(10.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeoStat(
                    label = stringResource(R.string.neo_diameter),
                    value = "%.2f–%.2f km".format(diamMin, diamMax),
                    modifier = Modifier.weight(1f)
                )
                NeoStat(
                    label = stringResource(R.string.neo_speed),
                    value = "%,d km/h".format(speed),
                    modifier = Modifier.weight(1f)
                )
                if (distLunar != null) {
                    NeoStat(
                        label = stringResource(R.string.neo_distance),
                        value = "%.1f LD".format(distLunar),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun NeoStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface)
    }
}
