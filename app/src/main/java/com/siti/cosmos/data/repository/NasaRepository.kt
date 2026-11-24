package com.siti.cosmos.data.repository

import com.siti.cosmos.data.model.NasaItem
import com.siti.cosmos.data.remote.RetrofitClient

class NasaRepository {

    private val api = RetrofitClient.api

    suspend fun getSpaceImages(query: String): List<NasaItem> {
        return api.searchImages(query).collection.items
    }
}
