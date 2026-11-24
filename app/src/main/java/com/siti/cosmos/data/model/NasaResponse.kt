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
    val title: String = "",
    val description: String = "",
    val date_created: String = "",
    val nasa_id: String = ""
)

data class NasaMediaLink(
    val href: String = "",
    val rel: String = ""
)
