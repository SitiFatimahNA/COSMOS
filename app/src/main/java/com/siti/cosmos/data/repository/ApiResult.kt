package com.siti.cosmos.data.repository

/**
 * Unified API result type used throughout the app.
 *
 * [Success.fromCache] tells the UI whether the data came from local cache
 * (so it can show "last updated" timestamp and an offline indicator).
 */
sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T,
        val fromCache: Boolean = false,
        val cachedAt: Long = 0L         // epoch ms when cached
    ) : ApiResult<T>()

    data class Error(
        val message: String,
        val code: Int?         = null,
        val cachedData: Any?   = null   // stale data to show alongside the error
    ) : ApiResult<Nothing>()

    object Loading : ApiResult<Nothing>()
}

/** Human-readable "X minutes ago" / "X hours ago" label for cache timestamps */
fun Long.toCacheLabel(): String {
    if (this == 0L) return ""
    val diffMs  = System.currentTimeMillis() - this
    val minutes = diffMs / 60_000
    val hours   = minutes / 60
    val days    = hours   / 24
    return when {
        days    > 0 -> "Updated ${days}d ago"
        hours   > 0 -> "Updated ${hours}h ago"
        minutes > 0 -> "Updated ${minutes}m ago"
        else        -> "Updated just now"
    }
}

@androidx.compose.runtime.Composable
fun Long.toLocalizedCacheLabel(): String {
    if (this == 0L) return ""
    val diffMs  = (System.currentTimeMillis() - this).coerceAtLeast(0L)
    val minutes = diffMs / 60_000
    val hours   = minutes / 60
    val days    = hours   / 24
    return when {
        days    > 0 -> androidx.compose.ui.res.stringResource(com.siti.cosmos.R.string.cache_updated_days, days)
        hours   > 0 -> androidx.compose.ui.res.stringResource(com.siti.cosmos.R.string.cache_updated_hours, hours)
        minutes > 0 -> androidx.compose.ui.res.stringResource(com.siti.cosmos.R.string.cache_updated_minutes, minutes)
        else        -> androidx.compose.ui.res.stringResource(com.siti.cosmos.R.string.cache_updated_just_now)
    }
}
