package com.siti.cosmos.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siti.cosmos.BuildConfig
import com.siti.cosmos.R
import com.siti.cosmos.ui.components.CosmosTopBar
import com.siti.cosmos.ui.components.SectionDivider
import com.siti.cosmos.ui.theme.*
import com.siti.cosmos.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel,
    onThemeChange: (String) -> Unit
) {
    val theme    by vm.theme.collectAsStateWithLifecycle()
    val language by vm.language.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CosmosTopBar(
                title    = stringResource(R.string.settings),
                showBack = true,
                onBack   = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spaceLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceXl)
        ) {

            // ── Theme ──────────────────────────────────────────────────────────
            SettingsGroup(title = stringResource(R.string.settings_theme)) {
                val themeOptions = listOf(
                    Triple("light",  stringResource(R.string.theme_light),  Icons.Outlined.LightMode),
                    Triple("dark",   stringResource(R.string.theme_dark),   Icons.Outlined.DarkMode),
                    Triple("system", stringResource(R.string.theme_system), Icons.Outlined.SettingsBrightness),
                )

                // Visual picker row
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(Dimens.spaceLg),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacing)
                ) {
                    themeOptions.forEach { (key, label, icon) ->
                        ThemeOptionCard(
                            icon     = icon,
                            label    = label,
                            selected = theme == key,
                            onClick  = {
                                vm.setTheme(key)
                                onThemeChange(key)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ── Language ───────────────────────────────────────────────────────
            SettingsGroup(title = stringResource(R.string.language)) {
                val langOptions = listOf(
                    Triple("id", "Bahasa Indonesia", "🇮🇩"),
                    Triple("en", "English",          "🇺🇸"),
                )
                langOptions.forEach { (code, label, flag) ->
                    SettingsRow(
                        leadingContent = {
                            Text(flag, style = MaterialTheme.typography.titleMedium)
                        },
                        label    = label,
                        selected = language == code,
                        onClick  = {
                            vm.setLanguage(code)
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(code)
                            )
                        }
                    )
                    if (code != langOptions.last().first) {
                        HorizontalDivider(
                            modifier  = Modifier.padding(horizontal = Dimens.spaceLg),
                            thickness = 0.5.dp,
                            color     = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }

            // ── About ──────────────────────────────────────────────────────────
            SettingsGroup(title = stringResource(R.string.settings_about)) {
                val apiKeyStatus = if (BuildConfig.NASA_API_KEY == "DEMO_KEY")
                    "DEMO_KEY (rate limited)" else "Custom key ✓"

                val aboutItems = listOf(
                    Triple(Icons.Outlined.Info,       stringResource(R.string.about_app_name),    "COSMOS"),
                    Triple(Icons.Outlined.NewReleases, stringResource(R.string.about_version),    "2.0"),
                    Triple(Icons.Outlined.Dataset,     stringResource(R.string.about_data_source), "NASA Open APIs"),
                    Triple(Icons.Outlined.Link,        "api.nasa.gov",                            ""),
                    Triple(Icons.Outlined.VpnKey,      stringResource(R.string.about_api_key),    apiKeyStatus),
                )
                aboutItems.forEachIndexed { index, (icon, label, value) ->
                    AboutRow(icon = icon, label = label, value = value)
                    if (index < aboutItems.lastIndex) {
                        HorizontalDivider(
                            modifier  = Modifier.padding(horizontal = Dimens.spaceLg),
                            thickness = 0.5.dp,
                            color     = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(Dimens.spaceLg))
        }
    }
}

// ── Settings group container ──────────────────────────────────────────────────
@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text      = title.uppercase(),
            style     = MaterialTheme.typography.labelSmall,
            color     = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier  = Modifier.padding(
                start  = Dimens.spaceXs,
                bottom = Dimens.spaceSm
            )
        )
        Card(
            shape     = RoundedCornerShape(Dimens.radiusLg),
            colors    = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(Dimens.elevationNone)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), content = content)
        }
    }
}

// ── Theme option card ─────────────────────────────────────────────────────────
@Composable
private fun ThemeOptionCard(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val bgColor     = if (selected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(Dimens.radiusMd))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(vertical = Dimens.spaceMd),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = label,
            tint     = if (selected) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.iconMd)
        )
        Text(
            text      = label,
            style     = MaterialTheme.typography.labelSmall,
            color     = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ── Settings row (language selector) ─────────────────────────────────────────
@Composable
private fun SettingsRow(
    leadingContent: @Composable () -> Unit,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceLg),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceLg)
    ) {
        leadingContent()
        Text(
            text      = label,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurface,
            modifier  = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                Icons.Outlined.CheckCircle,
                contentDescription = "Selected",
                tint     = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.iconMd)
            )
        }
    }
}

// ── About row ─────────────────────────────────────────────────────────────────
@Composable
private fun AboutRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceMd),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint     = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.iconSm)
        )
        Text(
            text     = label,
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (value.isNotBlank()) {
            Text(
                text      = value,
                style     = MaterialTheme.typography.labelMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
