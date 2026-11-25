package com.siti.cosmos.data.remote

import com.siti.cosmos.data.local.db.NasaDatabase
import com.siti.cosmos.data.local.db.RateLimitEntity
import kotlinx.coroutines.delay
import retrofit2.HttpException

object Endpoints {
    const val APOD = "apod"
    const val MARS = "mars"
    const val NEO  = "neo"
}

/**
 * Wraps an API call with:
 *  - Rate-limit check (HTTP 429): blocks endpoint until Retry-After or default backoff
 *  - Exponential backoff for transient errors (5xx, timeout): 3 attempts max
 *  - Surfaces a typed [RateLimitedException] when blocked so callers can show cached data
 */
class RateLimitedException(val retryAfterMs: Long) : Exception(
    "Rate limited. Retry after ${retryAfterMs / 1000}s."
)

suspend fun <T> withRetry(
    db: NasaDatabase,
    endpoint: String,
    maxAttempts: Int = 3,
    block: suspend () -> T
): T {
    val rateLimitDao = db.rateLimitDao()

    // ── Clear expired rate-limit records first ──────────────────────────────
    rateLimitDao.clearExpired(System.currentTimeMillis())

    // ── Check if endpoint is currently rate-limited ─────────────────────────
    val existing = rateLimitDao.get(endpoint)
    if (existing != null && existing.blockedUntilMs > System.currentTimeMillis()) {
        throw RateLimitedException(existing.blockedUntilMs - System.currentTimeMillis())
    }

    var attempt = 0
    var delayMs = 2_000L   // initial backoff: 2 s

    while (true) {
        attempt++
        try {
            val result = block()
            // Success — clear any residual rate limit for this endpoint
            rateLimitDao.clear(endpoint)
            return result
        } catch (e: HttpException) {
            when (e.code()) {
                429 -> {
                    // Parse Retry-After header (seconds) if present, default 60 s
                    val retryAfterSec = e.response()
                        ?.headers()
                        ?.get("Retry-After")
                        ?.toLongOrNull() ?: 60L
                    val blockedUntilMs = System.currentTimeMillis() + retryAfterSec * 1000L

                    rateLimitDao.upsert(RateLimitEntity(endpoint, blockedUntilMs))
                    throw RateLimitedException(retryAfterSec * 1000L)
                }
                in 500..599 -> {
                    // Server error — retry with backoff (max attempts)
                    if (attempt >= maxAttempts) throw e
                    delay(delayMs)
                    delayMs = minOf(delayMs * 2, 30_000L) // cap at 30 s
                }
                else -> throw e   // 400, 403, 404 — not retryable
            }
        } catch (e: java.net.SocketTimeoutException) {
            if (attempt >= maxAttempts) throw e
            delay(delayMs)
            delayMs = minOf(delayMs * 2, 30_000L)
        } catch (e: java.net.UnknownHostException) {
            throw e   // No internet — not retryable here
        }
    }
}
