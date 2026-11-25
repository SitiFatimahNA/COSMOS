package com.siti.cosmos.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.siti.cosmos.data.local.PreferencesDataStore
import com.siti.cosmos.data.model.FavoriteItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {
    private val store = PreferencesDataStore(app)

    val favorites: StateFlow<List<FavoriteItem>> = store.favoritesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(item: FavoriteItem) {
        viewModelScope.launch { store.addFavorite(item) }
    }

    fun remove(id: String) {
        viewModelScope.launch { store.removeFavorite(id) }
    }

    fun toggle(item: FavoriteItem) {
        if (favorites.value.any { it.id == item.id }) remove(item.id) else add(item)
    }

    fun isFavorite(id: String): Boolean = favorites.value.any { it.id == id }
}
