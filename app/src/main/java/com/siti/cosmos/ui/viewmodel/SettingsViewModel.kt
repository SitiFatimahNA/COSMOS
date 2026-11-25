package com.siti.cosmos.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.siti.cosmos.data.local.PreferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val store = PreferencesDataStore(app)

    val theme: StateFlow<String> = store.themeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val language: StateFlow<String> = store.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    fun setTheme(mode: String) {           // "light" | "dark" | "system"
        viewModelScope.launch { store.saveTheme(mode) }
    }

    fun setLanguage(lang: String) {        // "id" | "en"
        viewModelScope.launch { store.saveLanguage(lang) }
    }
}
