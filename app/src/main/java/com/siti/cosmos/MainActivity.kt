package com.siti.cosmos

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.siti.cosmos.data.local.PreferencesDataStore
import com.siti.cosmos.ui.navigation.AppNavigation
import com.siti.cosmos.ui.theme.COSMOSTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStore = PreferencesDataStore(applicationContext)

        // Read persisted theme synchronously on first launch to avoid flash
        val savedTheme = runBlocking { dataStore.themeFlow.first() }

        setContent {
            val systemDark = isSystemInDarkTheme()
            var themeMode by remember { mutableStateOf(savedTheme) }

            val isDark = when (themeMode) {
                "dark"  -> true
                "light" -> false
                else    -> systemDark  // "system"
            }

            COSMOSTheme(darkTheme = isDark, dynamicColor = false) {
                AppNavigation(
                    onThemeChange = { mode ->
                        themeMode = mode
                        // Persist asynchronously
                        lifecycleScope.launch { dataStore.saveTheme(mode) }
                    }
                )
            }
        }
    }
}
