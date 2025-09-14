package com.dungz.applocker.ui.screens.settings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dungz.applocker.ui.components.ChangePasswordDialog
import com.dungz.applocker.ui.components.InputPasswordDialog
import com.dungz.applocker.ui.components.SetEmergencyPasswordDialog
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.util.GlobalSnackbar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        GlobalSnackbar.message.collect { msg ->
            if (msg.isNotEmpty()) {
                scope.launch {
                    snackbarHostState.showSnackbar(msg)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimen.paddingMedium)
            ) {
                item {
                    SettingsSection(title = "Security") {
                        SettingsItem(
                            icon = Icons.Default.Lock,
                            title = "Change Password",
                            subtitle = "Update your app password",
                            onClick = { viewModel.updateShowInputPasswordChangePasswordDialog(true) }
                        )

                        SettingsItem(
                            icon = Icons.Default.Security,
                            title = "Emergency Password",
                            subtitle = if (uiState.value.securitySettings.isEmergencyPasswordSet) {
                                "Emergency password is set"
                            } else {
                                "Set emergency password"
                            },
                            onClick = {
                                viewModel.updateShowInputPasswordEmergencyPasswordDialog(
                                    true
                                )
                            }
                        )
                    }
                }

                item {
                    SettingsSection(title = "App Management") {
                        SettingsItem(
                            icon = Icons.Default.LockOpen,
                            title = "Unlock All Apps",
                            subtitle = "Remove protection from all apps",
                            onClick = {
                                viewModel.unLockAllApps()
                            }
                        )

                        SettingsItem(
                            icon = Icons.Default.Delete,
                            title = "Clear All Data",
                            subtitle = "Remove all passwords and locked apps",
                            onClick = {
                                viewModel.clearAllData()
                            }
                        )
                    }
                }

                item {
                    SettingsSection(title = "About") {
                        SettingsItem(
                            icon = Icons.Default.Info,
                            title = "App Version",
                            subtitle = "1.0.0"
                        )

                        SettingsItem(
                            icon = Icons.Default.PrivacyTip,
                            title = "Privacy Policy",
                            subtitle = "Read our privacy policy",
                            onClick = {
                                // TODO: Open privacy policy
                            }
                        )
                    }
                }
            }
        }
    }

    if (uiState.value.isShowChangePasswordDialog) {
        ChangePasswordDialog(onDismiss = { viewModel.updateShowChangePasswordDialog() }) {
            GlobalSnackbar.setMessage("Password changed")
            viewModel.updatePassword(it)
            viewModel.updateShowChangePasswordDialog()
        }
    }
    if (uiState.value.isShowInputPasswordChangePasswordDialog) {
        InputPasswordDialog(title = "Enter password to next step", onDismiss = {
            viewModel.updateShowInputPasswordChangePasswordDialog(false)
        }) {
            viewModel.validatePassword(password = it, onSuccess = {
                GlobalSnackbar.setMessage("Password verified")
                viewModel.updateShowInputPasswordChangePasswordDialog(false)
                viewModel.updateShowChangePasswordDialog()
            }, onError = { viewModel.updateShowInputPasswordChangePasswordDialog(false)
                GlobalSnackbar.setMessage("Incorrect password")
            })
        }
    }

    if (uiState.value.isShowInputPasswordEmergencyPasswordDialog) {
        InputPasswordDialog(title = "Enter Emergency password to next step", onDismiss = {
            viewModel.updateShowInputPasswordEmergencyPasswordDialog(false)
        }) {
            viewModel.validateEmergencyPassword(password = it, onSuccess = {
                viewModel.updateShowInputPasswordEmergencyPasswordDialog(false)
                viewModel.updateShowEmergencyPasswordDialog()
            }, onError = { viewModel.updateShowInputPasswordEmergencyPasswordDialog(false)
                GlobalSnackbar.setMessage("Incorrect emergency password")
            })
        }
    }

    if (uiState.value.isShowEmergencyPasswordDialog) {
        SetEmergencyPasswordDialog(
            onDismiss = { viewModel.updateShowEmergencyPasswordDialog() },
            onConfirm = { emergencyPassword ->
                GlobalSnackbar.setMessage("Emergency password set")
                viewModel.updateEmergencyPassword(emergencyPassword)
                viewModel.updateShowEmergencyPasswordDialog()
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                horizontal = Dimen.paddingMedium,
                vertical = Dimen.paddingSmall
            )
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = Dimen.elevationSmall)
        ) {
            content()
        }

        Spacer(modifier = Modifier.height(Dimen.spacingMedium))
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimen.iconSizeMedium)
            )

            Spacer(modifier = Modifier.width(Dimen.spacingMedium))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            trailing?.invoke()
        }
    }
}