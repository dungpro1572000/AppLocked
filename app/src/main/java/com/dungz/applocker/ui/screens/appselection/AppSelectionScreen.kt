@file:OptIn(ExperimentalMaterial3Api::class)

package com.dungz.applocker.ui.screens.appselection

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dungz.applocker.service.AppLockService
import com.dungz.applocker.ui.components.DismissPermissionDialog
import com.dungz.applocker.ui.components.SystemAlertWindowPermissionDialog
import com.dungz.applocker.ui.components.UsageStatsPermissionDialog
import com.dungz.applocker.ui.navigation.Screen
import com.dungz.applocker.ui.theme.Dimen

@Composable
fun AppSelectionScreen(
    navController: NavController,
    viewModel: AppSelectionViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()
    val context = LocalContext.current
    val launcherSystemOverlayWindow =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!viewModel.isOverlayPermissionGrant()) {
                viewModel.updateIsShowDenyPermissionDialog(true)
            } else {
                viewModel.updateIsShowSystemWindowAlertPermissionDialog(false)
            }
        }

    val launcherUsageStat =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!viewModel.isUsageStatsPermissionGrant()) {
                viewModel.updateIsShowDenyPermissionDialog(true)
            } else {
                viewModel.updateIsShowUsageStatsPermissionDialog(false)
                val intent = Intent(context, AppLockService::class.java).apply {
                    action = "START_MONITORING"
                }
                context.startService(intent)
            }
        }
    LaunchedEffect(key1 = Unit) {
        if (!viewModel.isOverlayPermissionGrant()) {
            viewModel.updateIsShowSystemWindowAlertPermissionDialog(true)
        }
        if (!viewModel.isUsageStatsPermissionGrant()) {
            viewModel.updateIsShowUsageStatsPermissionDialog(true)
        } else if (viewModel.isUsageStatsPermissionGrant()){
            val intent = Intent(context, AppLockService::class.java).apply {
                action = "START_MONITORING"
            }
            context.startService(intent)
        }
    }

    val filteredApps =
        remember(state.value.listLockedApp, state.value.searchApp, state.value.isShowSystemApp) {
            derivedStateOf {
                state.value.listLockedApp.filter { app ->
                    val matchesSearch =
                        app.appName.contains(state.value.searchApp, ignoreCase = true) ||
                                app.packageName.contains(state.value.searchApp, ignoreCase = true)
                    val matchesSystemFilter = state.value.isShowSystemApp || !app.isSystemApp
                    matchesSearch && matchesSystemFilter
                }
            }
        }

    if (state.value.isShowDenyPermissionDialog) {
        DismissPermissionDialog(
            onDismiss = {
                viewModel.updateIsShowDenyPermissionDialog(false)
            }
        )
    }
    if (state.value.isShowUsageStatsPermissionDialog) {
        UsageStatsPermissionDialog(onGrantPermission = {
            // Tạo Intent để chuyển người dùng đến màn hình cài đặt của ứng dụng
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            launcherUsageStat.launch(intent)
        }) {
            viewModel.updateIsShowUsageStatsPermissionDialog(false)
        }
    }

    if (state.value.isShowSystemWindowAlertPermissionDialog) {
        SystemAlertWindowPermissionDialog(onGrantPermission = {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${context.packageName}".toUri()
            )
            launcherSystemOverlayWindow.launch(intent)
        }) {
            viewModel.updateIsShowSystemWindowAlertPermissionDialog(false)
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
                        text = "Apps (${filteredApps.value.size})",
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
                items(items = filteredApps.value, key = { it.packageName }) { app ->
                    AppItem(
                        app = app,
                        onToggleLock = {
                            viewModel.toggleAppLock(
                                app.isLocked,
                                app.packageName,
                                app.appName
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppItem(
    app: AppSelectionInfo,
    onToggleLock: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.spacingSmall)
            .clickable { onToggleLock.invoke() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon - using a placeholder for now
            if (app.isLocked) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Default.Lock,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = app.appName
                )
            } else {
                Image(
                    modifier = Modifier.size(48.dp),
                    bitmap = app.appIcon,
                    contentDescription = app.appName,
                )
            }
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