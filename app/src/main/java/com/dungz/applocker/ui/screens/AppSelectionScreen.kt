package com.dungz.applocker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dungz.applocker.data.model.AppInfo
import com.dungz.applocker.ui.navigation.Screen
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val lockedApps by viewModel.lockedApps.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showSystemApps by remember { mutableStateOf(false) }

    val filteredApps = remember(lockedApps, searchQuery, showSystemApps) {
        lockedApps.filter { app ->
            val matchesSearch = app.appName.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)
            val matchesSystemFilter = showSystemApps || !app.isSystemApp
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
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
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
                        Switch(
                            checked = showSystemApps,
                            onCheckedChange = { showSystemApps = it }
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