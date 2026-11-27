package com.siti.cosmos.data.model


data class NasaResponse(
    val collection: NasaCollection
)

data class NasaCollection(
    val items: List<NasaItem> = emptyList()
)

data class NasaItem(
    val data: List<NasaMediaData> = emptyList(),
    val links: List<NasaMediaLink> = emptyList()
)

data class NasaMediaData(
    val title: String? = null,
    val description: String? = null,
    val date_created: String? = null,
    val nasa_id: String? = null
)

data class NasaMediaLink(
    val href: String? = null,
    val rel: String? = null
)

/**
 * Clean domain/UI model for an item in NASA Gallery and its in-app detail screen.
 */
data class GalleryItem(
    val id: String,
    val nasaId: String,
    val title: String,
    val description: String,
    val dateCreated: String,
    val imageUrl: String
)

fun NasaItem.toGalleryItem(fallbackIndex: Int = 0): GalleryItem? {
    val rawUrl = links.firstOrNull { it.rel == "preview" && !it.href.isNullOrBlank() }?.href
        ?: links.firstOrNull { !it.href.isNullOrBlank() }?.href
        ?: return null

    val secureUrl = if (rawUrl.startsWith("http://")) {
        "https://" + rawUrl.removePrefix("http://")
    } else {
        rawUrl
    }

    val media = data.firstOrNull()
    val nasaId = media?.nasa_id?.trim().orEmpty()
    val rawTitle = media?.title?.trim().orEmpty()
    val rawDesc = media?.description?.trim().orEmpty()
    val rawDate = media?.date_created?.trim()?.take(10).orEmpty()
    val stableId = nasaId.ifBlank { "nasa_item_${secureUrl.hashCode()}_$fallbackIndex" }

    return GalleryItem(
        id = stableId,
        nasaId = nasaId,
        title = rawTitle,
        description = rawDesc,
        dateCreated = rawDate,
        imageUrl = secureUrl
    )
}
