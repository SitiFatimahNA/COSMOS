package com.siti.cosmos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.siti.cosmos.data.model.NasaItem
import com.siti.cosmos.data.repository.NasaRepository

@Composable
fun NasaGallery(darkTheme: Boolean = false) {
    var images by remember { mutableStateOf<List<NasaItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

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
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.nasa_gallery),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(10.dp))

        when {
            isLoading -> Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.loading_gallery),
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }

            hasError -> Text(stringResource(R.string.gallery_error),
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

            images.isEmpty() -> Text(stringResource(R.string.gallery_empty),
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)

            else -> LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(images) { item ->
                    val imageUrl = item.links.firstOrNull()?.href
                    val title = item.data.firstOrNull()?.title ?: "Space Image"
                    Column(
                        modifier = Modifier.width(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl).crossfade(true).build(),
                            contentDescription = title,
                            modifier = Modifier.fillMaxWidth().height(140.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Text(text = title,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                            maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}
