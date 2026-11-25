package com.siti.cosmos.data.model

/** Generic bookmark that can represent any NASA content item */
data class FavoriteItem(
    val id: String,           // unique key (APOD date, Mars photo id, Neo id)
    val type: FavoriteType,
    val title: String,
    val imageUrl: String,
    val date: String,
    val description: String
)

enum class FavoriteType {
    APOD, MARS, NEO
}
