package com.muslimbro.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muslimbro.core.domain.model.CalculationMethod
import com.muslimbro.core.domain.model.Madhab
import com.muslimbro.core.domain.model.UserLocation
import com.muslimbro.core.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAboutClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLocationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingScreen(Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
            ) {
                item { SettingsSectionHeader("Location") }
                item {
                    SettingsCard {
                        LocationSettingRow(
                            currentLocation = uiState.savedLocation,
                            onClick = { showLocationDialog = true }
                        )
                    }
                }

                item { SettingsSectionHeader("Prayer Calculation") }
                item {
                    SettingsCard {
                        DropdownSettingRow(
                            label = "Calculation Method",
                            value = uiState.settings.calculationMethod.displayName(),
                            options = CalculationMethod.values().map { it.displayName() },
                            onSelect = { index ->
                                viewModel.updateCalculationMethod(CalculationMethod.values()[index])
                            }
                        )
                        HorizontalDivider()
                        DropdownSettingRow(
                            label = "Madhab (Asr)",
                            value = uiState.settings.madhab.displayName(),
                            options = Madhab.values().map { it.displayName() },
                            onSelect = { index ->
                                viewModel.updateMadhab(Madhab.values()[index])
                            }
                        )
                    }
                }

                item { SettingsSectionHeader("Notifications") }
                item {
                    SettingsCard {
                        SwitchSettingRow(
                            label = "Enable Adhan Notifications",
                            checked = uiState.settings.notificationsEnabled,
                            onCheckedChange = viewModel::updateNotificationsEnabled
                        )
                    }
                }

                item { SettingsSectionHeader("Appearance") }
                item {
                    SettingsCard {
                        DropdownSettingRow(
                            label = "Theme",
                            value = when (uiState.settings.isDarkMode) {
                                true -> "Dark"
                                false -> "Light"
                                null -> "System Default"
                            },
                            options = listOf("System Default", "Light", "Dark"),
                            onSelect = { index ->
                                viewModel.updateDarkMode(when (index) {
                                    1 -> false
                                    2 -> true
                                    else -> null
                                })
                            }
                        )
                    }
                }

                item { SettingsSectionHeader("Language") }
                item {
                    SettingsCard {
                        DropdownSettingRow(
                            label = "App Language",
                            value = when (uiState.settings.language) {
                                "ar" -> "العربية"
                                "ur" -> "اردو"
                                else -> "English"
                            },
                            options = listOf("English", "العربية", "اردو"),
                            onSelect = { index ->
                                viewModel.updateLanguage(listOf("en", "ar", "ur")[index])
                            }
                        )
                    }
                }

                item { SettingsSectionHeader("About") }
                item {
                    SettingsCard {
                        AboutSettingRow(onClick = onAboutClick)
                    }
                }
            }
        }
    }

    if (showLocationDialog) {
        LocationSearchDialog(
            uiState = uiState,
            onQueryChange = viewModel::onLocationQueryChange,
            onSearch = viewModel::searchLocation,
            onSelectLocation = { location ->
                viewModel.saveLocation(location)
                showLocationDialog = false
            },
            onUseGps = {
                viewModel.useGpsLocation()
                showLocationDialog = false
            },
            onDismiss = { showLocationDialog = false }
        )
    }
}

@Composable
private fun LocationSearchDialog(
    uiState: SettingsUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSelectLocation: (UserLocation) -> Unit,
    onUseGps: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Location") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.locationQuery,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Search city or address…") },
                    singleLine = true,
                    trailingIcon = {
                        if (uiState.locationQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(
                    onClick = onSearch,
                    enabled = uiState.locationQuery.isNotBlank() && !uiState.isSearchingLocation,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text("Search")
                }

                if (uiState.isSearchingLocation) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                if (uiState.locationResults.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        uiState.locationResults.forEach { location ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectLocation(location) },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = location.cityName ?: "Unknown",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                if (uiState.locationResults.isEmpty() && !uiState.isSearchingLocation && uiState.locationQuery.isEmpty()) {
                    HorizontalDivider()
                    Spacer(Modifier.height(4.dp))
                    TextButton(
                        onClick = onUseGps,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Use GPS / Device Location")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun LocationSettingRow(
    currentLocation: UserLocation?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Location", style = MaterialTheme.typography.bodyMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = currentLocation?.cityName ?: "Using GPS",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column { content() }
    }
}

@Composable
private fun DropdownSettingRow(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AboutSettingRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text("Application Information", style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun SwitchSettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
