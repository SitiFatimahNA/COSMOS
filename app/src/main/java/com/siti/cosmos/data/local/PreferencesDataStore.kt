package com.siti.cosmos.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.siti.cosmos.data.model.FavoriteItem
import com.siti.cosmos.data.model.FavoriteType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cosmos_prefs")

class PreferencesDataStore(private val context: Context) {

    private val gson = Gson()

    companion object {
        val KEY_THEME       = stringPreferencesKey("theme_mode")     // "light" | "dark" | "system"
        val KEY_LANGUAGE    = stringPreferencesKey("language")       // "id" | "en"
        val KEY_FAVORITES   = stringPreferencesKey("favorites_json")
    }

    // ── Theme ──────────────────────────────────────────────────────────────────
    val themeFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_THEME] ?: "system" }

    suspend fun saveTheme(mode: String) {
        context.dataStore.edit { it[KEY_THEME] = mode }
    }

    // ── Language ───────────────────────────────────────────────────────────────
    val languageFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_LANGUAGE] ?: "system" }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = lang }
    }

    // ── Favorites ──────────────────────────────────────────────────────────────
    val favoritesFlow: Flow<List<FavoriteItem>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[KEY_FAVORITES] ?: "[]"
            try {
                val type = object : TypeToken<List<FavoriteItemDto>>() {}.type
                val dtos: List<FavoriteItemDto> = gson.fromJson(json, type) ?: emptyList()
                dtos.map { it.toFavoriteItem() }
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun addFavorite(item: FavoriteItem) {
        context.dataStore.edit { prefs ->
            val current = readFavorites(prefs)
            if (current.none { it.id == item.id }) {
                val updated = current + FavoriteItemDto.fromFavoriteItem(item)
                prefs[KEY_FAVORITES] = gson.toJson(updated)
            }
        }
    }

    suspend fun removeFavorite(id: String) {
        context.dataStore.edit { prefs ->
            val current = readFavorites(prefs)
            val updated = current.filter { it.id != id }
            prefs[KEY_FAVORITES] = gson.toJson(updated)
        }
    }

    suspend fun isFavorite(id: String): Boolean {
        var result = false
        context.dataStore.edit { prefs ->
            result = readFavorites(prefs).any { it.id == id }
        }
        return result
    }

    private fun readFavorites(prefs: Preferences): List<FavoriteItemDto> {
        val json = prefs[KEY_FAVORITES] ?: "[]"
        return try {
            val type = object : TypeToken<List<FavoriteItemDto>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Internal DTO to serialize/deserialize with Gson (enum as String)
    private data class FavoriteItemDto(
        val id: String,
        val type: String,
        val title: String,
        val imageUrl: String,
        val date: String,
        val description: String
    ) {
        fun toFavoriteItem() = FavoriteItem(
            id = id, title = title, imageUrl = imageUrl,
            date = date, description = description,
            type = try { FavoriteType.valueOf(type) } catch (e: Exception) { FavoriteType.APOD }
        )

        companion object {
            fun fromFavoriteItem(item: FavoriteItem) = FavoriteItemDto(
                id = item.id, type = item.type.name, title = item.title,
                imageUrl = item.imageUrl, date = item.date, description = item.description
            )
        }
    }
}
