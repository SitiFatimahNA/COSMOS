
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.siti.cosmos.data.model.NasaItem
import com.siti.cosmos.data.repository.NasaRepository
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

@Composable
fun NasaGallery(darkTheme: Boolean) {
    val context = LocalContext.current
    val galleryCardColor = if (darkTheme) {
        DarkCard
    } else {
        SpaceCard
    }

    val galleryTextColor = if (darkTheme) {
        DarkWhite
    } else {
        SpaceWhite
    }

    var images by remember {
        mutableStateOf<List<NasaItem>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var hasError by remember {
        mutableStateOf(false)
    }

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
            .padding(vertical = 12.dp)
    ) {

        Text(
            text = stringResource(R.string.nasa_gallery),
            color = galleryTextColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(20.dp)
                )
                Text(
                    text = stringResource(R.string.loading_gallery),
                    color = galleryTextColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            hasError -> {
                Text(
                    text = stringResource(R.string.gallery_error),
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            images.isEmpty() -> {
                Text(
                    text = stringResource(R.string.gallery_empty),
                    modifier = Modifier.padding(12.dp),
                    color = galleryTextColor
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
                                containerColor = galleryCardColor
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
                                    color = galleryTextColor,
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
