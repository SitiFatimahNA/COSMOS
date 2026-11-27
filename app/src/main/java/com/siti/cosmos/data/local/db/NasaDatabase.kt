package com.siti.cosmos.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities  = [
        ApodCacheEntity::class,
        MarsCacheEntity::class,
        NeoCacheEntity::class,
        GalleryCacheEntity::class,
        RateLimitEntity::class
    ],
    version   = 2,
    exportSchema = false
)
abstract class NasaDatabase : RoomDatabase() {
    abstract fun apodDao(): ApodCacheDao
    abstract fun marsDao(): MarsCacheDao
    abstract fun neoDao(): NeoCacheDao
    abstract fun galleryDao(): GalleryCacheDao
    abstract fun rateLimitDao(): RateLimitDao

    companion object {
        @Volatile private var INSTANCE: NasaDatabase? = null

        fun getInstance(context: Context): NasaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NasaDatabase::class.java,
                    "nasa_cache.db"
                )
                    .fallbackToDestructiveMigration()   // safe for cache-only DB
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
