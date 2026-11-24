
package com.siti.cosmos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.siti.cosmos.data.model.NasaItem
import com.siti.cosmos.data.repository.NasaRepository
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults

@Composable
fun NasaGallery() {

    var images by remember {
        mutableStateOf<List<NasaItem>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        try {
            images = NasaRepository().getSpaceImages("nebula")
        } catch (e: Exception) {
            errorMessage = "Gagal mengambil gambar NASA"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {

        Text(
            text = "NASA Space Gallery",
            color = SpaceWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(20.dp)
                )
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            images.isEmpty() -> {
                Text(
                    text = "Belum ada gambar tersedia.",
                    modifier = Modifier.padding(12.dp),
                    color = Color.White
                )
            }

            else -> {
                LazyRow(
                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 12.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(images) { item ->

                        val imageUrl = item.links.firstOrNull()?.href
                        val title = item.data.firstOrNull()?.title
                            ?: "Space Image"

                        Card(
                            modifier = Modifier
                                .width(260.dp)
                                .height(220.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = SpaceCard
                            )
                        ) {
                            Column {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = title,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SpaceWhite,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
