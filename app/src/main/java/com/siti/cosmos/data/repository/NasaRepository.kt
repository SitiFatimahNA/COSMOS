package com.siti.cosmos.data.repository

import android.content.Context
import com.siti.cosmos.data.local.db.CacheTtl
import com.siti.cosmos.data.local.db.NasaDatabase
import com.siti.cosmos.data.local.db.toEntity
import com.siti.cosmos.data.local.db.toGalleryItem
import com.siti.cosmos.data.model.GalleryItem
import com.siti.cosmos.data.model.NasaItem
import com.siti.cosmos.data.model.toGalleryItem
import com.siti.cosmos.data.remote.Endpoints
import com.siti.cosmos.data.remote.RateLimitedException
import com.siti.cosmos.data.remote.RetrofitClient
import com.siti.cosmos.data.remote.withRetry
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class GalleryErrorType {
    data object NoInternet : GalleryErrorType()
    data object Timeout : GalleryErrorType()
    data class RateLimited(val retryAfterSeconds: Long) : GalleryErrorType()
    data class ServerError(val code: Int) : GalleryErrorType()
    data object Generic : GalleryErrorType()
}

data class GalleryFetchResult(
    val items: List<GalleryItem> = emptyList(),
    val fromCache: Boolean = false,
    val cachedAt: Long = 0L,
    val errorType: GalleryErrorType? = null
)

class NasaRepository(private val context: Context? = null) {

    private val legacyApi = RetrofitClient.api
    private val imagesApi = RetrofitClient.imagesApi

    suspend fun getSpaceImages(query: String): List<NasaItem> {
        return legacyApi.searchImages(query).collection.items
    }

    /**
     * Cache-first NASA Images API fetch with RateLimitGuard and stale-cache fallback.
     */
    suspend fun getGalleryImages(
        query: String,
        forceRefresh: Boolean = false
    ): GalleryFetchResult {
        val normalizedQuery = query.trim().lowercase().ifEmpty { "nebula" }
        val appContext = context?.applicationContext
        val db = appContext?.let { NasaDatabase.getInstance(it) }
        val dao = db?.galleryDao()

        val cached = dao?.getByQuery(normalizedQuery).orEmpty()
        val cachedAt = cached.firstOrNull()?.cachedAt ?: 0L
        val isFresh = cached.isNotEmpty() &&
            (System.currentTimeMillis() - cachedAt) < CacheTtl.GALLERY_MS

        if (!forceRefresh && isFresh) {
            return GalleryFetchResult(
                items = cached.map { it.toGalleryItem() },
                fromCache = true,
                cachedAt = cachedAt
            )
        }

        return try {
            val response = if (db != null) {
                withRetry(db, Endpoints.GALLERY) {
                    imagesApi.searchImages(query = normalizedQuery, pageSize = 24)
                }
            } else {
                imagesApi.searchImages(query = normalizedQuery, pageSize = 24)
            }

            val mapped = response.collection.items
                .mapIndexedNotNull { index, item -> item.toGalleryItem(index) }
                .distinctBy { it.id }

            if (dao != null && mapped.isNotEmpty()) {
                val now = System.currentTimeMillis()
                dao.deleteByQuery(normalizedQuery)
                dao.insertAll(
                    mapped.mapIndexed { idx, item ->
                        item.toEntity(normalizedQuery, idx).copy(cachedAt = now)
                    }
                )
                dao.evictOld(now - 7 * 24 * 60 * 60 * 1000L)
            }

            GalleryFetchResult(items = mapped, fromCache = false)
        } catch (e: RateLimitedException) {
            val fallback = cached.map { it.toGalleryItem() }
            GalleryFetchResult(
                items = fallback,
                fromCache = fallback.isNotEmpty(),
                cachedAt = cachedAt,
                errorType = GalleryErrorType.RateLimited((e.retryAfterMs / 1000L).coerceAtLeast(1L))
            )
        } catch (e: HttpException) {
            val fallback = cached.map { it.toGalleryItem() }
            GalleryFetchResult(
                items = fallback,
                fromCache = fallback.isNotEmpty(),
                cachedAt = cachedAt,
                errorType = GalleryErrorType.ServerError(e.code())
            )
        } catch (e: UnknownHostException) {
            val fallback = cached.map { it.toGalleryItem() }
            GalleryFetchResult(
                items = fallback,
                fromCache = fallback.isNotEmpty(),
                cachedAt = cachedAt,
                errorType = if (fallback.isEmpty()) GalleryErrorType.NoInternet else null
            )
        } catch (e: SocketTimeoutException) {
            val fallback = cached.map { it.toGalleryItem() }
            GalleryFetchResult(
                items = fallback,
                fromCache = fallback.isNotEmpty(),
                cachedAt = cachedAt,
                errorType = if (fallback.isEmpty()) GalleryErrorType.Timeout else null
            )
        } catch (e: Exception) {
            val fallback = cached.map { it.toGalleryItem() }
            GalleryFetchResult(
                items = fallback,
                fromCache = fallback.isNotEmpty(),
                cachedAt = cachedAt,
                errorType = if (fallback.isEmpty()) GalleryErrorType.Generic else null
            )
        }
    }
}
