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
import com.siti.cosmos.ui.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    favVm: FavoritesViewModel
) {
    val favorites by favVm.favorites.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CosmosTopBar(
                title    = stringResource(R.string.nav_favs),
                showBack = true,
                onBack   = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (favorites.isEmpty()) {
                EmptyState(
                    message = stringResource(R.string.favorites_empty),
                    emoji = "⭐",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favorites, key = { it.id }) { item ->
                        FavoriteCard(
                            item     = item,
                            onDelete = { favVm.remove(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteCard(item: FavoriteItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (item.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl).crossfade(true).build(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        when (item.type) {
                            FavoriteType.APOD -> "🌠"
                            FavoriteType.MARS -> "🔴"
                            FavoriteType.NEO  -> "☄️"
                        }, fontSize = 28.sp
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(item.type.name, fontSize = 9.sp, color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(item.title, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(item.date, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            IconButton(onClick = onDelete) {
                Text("🗑", fontSize = 18.sp)
            }
        }
    }
}
