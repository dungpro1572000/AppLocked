package com.dungz.applocker.ui.screens.appselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dungz.applocker.data.model.AppInfo
import com.dungz.applocker.ui.navigation.Screen
import com.dungz.applocker.ui.theme.Dimen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    navController: NavController,
    viewModel: AppSelectionViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

    val filteredApps =
        remember(state.value.listLockedApp, state.value.searchApp, state.value.isShowSystemApp) {
            state.value.listLockedApp.filter { app ->
                val matchesSearch =
                    app.appName.contains(state.value.searchApp, ignoreCase = true) ||
                            app.packageName.contains(state.value.searchApp, ignoreCase = true)
                val matchesSystemFilter = state.value.isShowSystemApp || !app.isSystemApp
                matchesSearch && matchesSystemFilter
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Locker") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search and filter section
            Column(
                modifier = Modifier.padding(Dimen.paddingMedium)
            ) {
                OutlinedTextField(
                    value = state.value.searchApp,
                    onValueChange = {
                        viewModel.updateSearchApp(it)
                    },
                    label = { Text("Search apps") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Apps (${filteredApps.size})",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Show system apps",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.width(Dimen.paddingSmall))
                        Switch(
                            checked = state.value.isShowSystemApp,
                            onCheckedChange = {
                                viewModel.updateIsShowSystemApp(it)
                            }
                        )
                    }
                }
            }

            // Apps list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimen.paddingMedium)
            ) {
                items(filteredApps) { app ->
                    AppItem(
                        app = app,
                        onToggleLock = { viewModel.toggleAppLock(app) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppItem(
    app: AppInfo,
    onToggleLock: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.spacingSmall),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimen.elevationSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon - using a placeholder for now
            Icon(
                imageVector = if (app.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = app.appName,
                modifier = Modifier.size(Dimen.appIconSize),
                tint = if (app.isLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(Dimen.spacingMedium))

            // App info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (app.isSystemApp) {
                    Text(
                        text = "System App",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimen.spacingMedium))

            // Lock toggle
            IconButton(onClick = onToggleLock) {
                Icon(
                    imageVector = if (app.isLocked) {
                        Icons.Default.Lock
                    } else {
                        Icons.Default.LockOpen
                    },
                    contentDescription = if (app.isLocked) "Unlock app" else "Lock app",
                    tint = if (app.isLocked) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }
    }
} 