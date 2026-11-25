package com.siti.cosmos.data.local.db

import androidx.room.*

@Dao
interface ApodCacheDao {
    @Query("SELECT * FROM apod_cache WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): ApodCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ApodCacheEntity)

    @Query("DELETE FROM apod_cache WHERE cachedAt < :olderThanMs")
    suspend fun evictOld(olderThanMs: Long)

    @Query("SELECT COUNT(*) FROM apod_cache")
    suspend fun count(): Int
}

@Dao
interface MarsCacheDao {
    @Query("""
        SELECT * FROM mars_cache
        WHERE rover = :rover AND earthDate = :earthDate AND page = :page
        ORDER BY id ASC
    """)
    suspend fun getByKey(rover: String, earthDate: String, page: Int): List<MarsCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<MarsCacheEntity>)

    /** Keep only last N days per rover to avoid unbounded growth */
    @Query("""
        DELETE FROM mars_cache
        WHERE rover = :rover AND cachedAt < :olderThanMs
    """)
    suspend fun evictOldForRover(rover: String, olderThanMs: Long)

    @Query("SELECT COUNT(*) FROM mars_cache")
    suspend fun count(): Int
}

@Dao
interface NeoCacheDao {
    @Query("""
        SELECT * FROM neo_cache
        WHERE startDate = :startDate AND endDate = :endDate
        ORDER BY isPotentiallyHazardous DESC, closeApproachDate ASC
    """)
    suspend fun getByRange(startDate: String, endDate: String): List<NeoCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<NeoCacheEntity>)

    @Query("DELETE FROM neo_cache WHERE cachedAt < :olderThanMs")
    suspend fun evictOld(olderThanMs: Long)

    @Query("SELECT COUNT(*) FROM neo_cache")
    suspend fun count(): Int
}

@Dao
interface RateLimitDao {
    @Query("SELECT * FROM rate_limit WHERE endpoint = :endpoint LIMIT 1")
    suspend fun get(endpoint: String): RateLimitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RateLimitEntity)

    @Query("DELETE FROM rate_limit WHERE endpoint = :endpoint")
    suspend fun clear(endpoint: String)

    /** Remove expired entries */
    @Query("DELETE FROM rate_limit WHERE blockedUntilMs < :nowMs")
    suspend fun clearExpired(nowMs: Long)
}
