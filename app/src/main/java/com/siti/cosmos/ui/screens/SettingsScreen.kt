package com.siti.cosmos.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siti.cosmos.BuildConfig
import com.siti.cosmos.R
import com.siti.cosmos.ui.components.CosmosTopBar
import com.siti.cosmos.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel,
    onThemeChange: (String) -> Unit
) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    val language by vm.language.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CosmosTopBar(title = stringResource(R.string.settings), showBack = true, onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Theme ──────────────────────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_theme)) {
                val options = listOf(
                    "light"  to stringResource(R.string.theme_light),
                    "dark"   to stringResource(R.string.theme_dark),
                    "system" to stringResource(R.string.theme_system)
                )
                options.forEach { (key, label) ->
                    SettingsRow(
                        label    = label,
                        selected = theme == key,
                        onClick  = {
                            vm.setTheme(key)
                            onThemeChange(key)
                        }
                    )
                }
            }

            // ── Language ───────────────────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.language)) {
                val langs = listOf("id" to "🇮🇩  Bahasa Indonesia", "en" to "🇺🇸  English")
                langs.forEach { (code, label) ->
                    SettingsRow(
                        label    = label,
                        selected = language == code,
                        onClick  = {
                            vm.setLanguage(code)
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(code)
                            )
                        }
                    )
                }
            }

            // ── About ──────────────────────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_about)) {
                InfoRow(label = stringResource(R.string.about_app_name), value = "COSMOS")
                InfoRow(label = stringResource(R.string.about_version), value = "2.0")
                InfoRow(label = stringResource(R.string.about_data_source), value = "NASA Open APIs")
                InfoRow(label = "api.nasa.gov", value = "")
                // Show whether using real key or demo
                val keyStatus = if (BuildConfig.NASA_API_KEY == "DEMO_KEY")
                    "DEMO_KEY (rate limited)" else "Custom Key ✓"
                InfoRow(label = stringResource(R.string.about_api_key), value = keyStatus)
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary, fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 8.dp))
        Card(
            shape  = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(4.dp), content = content)
        }
    }
}

@Composable
fun SettingsRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface)
        if (selected) Text("✓", color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        if (value.isNotBlank()) {
            Text(value, color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
