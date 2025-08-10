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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dungz.applocker.ui.theme.Dimen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsState()

    Scaffold(
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
                            onClick = { viewModel.updateShowChangePasswordDialog(true) }
                        )

                        SettingsItem(
                            icon = Icons.Default.Security,
                            title = "Emergency Password",
                            subtitle = if (uiState.value.securitySettings.isEmergencyPasswordSet) {
                                "Emergency password is set"
                            } else {
                                "Set emergency password"
                            },
                            onClick = { viewModel.updateShowEmergencyPasswordDialog(true) }
                        )

                        SettingsItem(
                            icon = Icons.Default.Fingerprint,
                            title = "Biometric Authentication",
                            subtitle = "Use fingerprint or face unlock",
                            trailing = {
                                Switch(
                                    checked = uiState.value.securitySettings.isBiometricEnabled,
                                    onCheckedChange = { /* TODO: Implement biometric toggle */ }
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
        ChangePasswordDialog(
            onDismiss = { viewModel.updateShowChangePasswordDialog(false) },
            onConfirm = { newPassword ->
                viewModel.updatePassword(newPassword)
                viewModel.updateShowChangePasswordDialog(false)
            }
        )
    }

    if (uiState.value.isShowEmergencyPasswordDialog) {
        EmergencyPasswordDialog(
            onDismiss = { viewModel.updateShowEmergencyPasswordDialog(false) },
            onConfirm = { emergencyPassword ->
                viewModel.updateEmergencyPassword(emergencyPassword)
                viewModel.updateShowEmergencyPasswordDialog(false)
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

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        error = null
                    },
                    label = { Text("New Password") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        error = null
                    },
                    label = { Text("Confirm Password") },
                    singleLine = true
                )

                error?.let { errorMessage ->
                    Spacer(modifier = Modifier.height(Dimen.spacingMedium))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        newPassword.length < 4 -> {
                            error = "Password must be at least 4 characters"
                        }

                        newPassword != confirmPassword -> {
                            error = "Passwords do not match"
                        }

                        else -> {
                            onConfirm(newPassword)
                        }
                    }
                }
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EmergencyPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var emergencyPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Emergency Password") },
        text = {
            Column {
                Text(
                    text = "Emergency password will unlock all apps for 24 hours when used.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                OutlinedTextField(
                    value = emergencyPassword,
                    onValueChange = {
                        emergencyPassword = it
                        error = null
                    },
                    label = { Text("Emergency Password") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        error = null
                    },
                    label = { Text("Confirm Password") },
                    singleLine = true
                )

                error?.let { errorMessage ->
                    Spacer(modifier = Modifier.height(Dimen.spacingMedium))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        emergencyPassword.length < 4 -> {
                            error = "Password must be at least 4 characters"
                        }

                        emergencyPassword != confirmPassword -> {
                            error = "Passwords do not match"
                        }

                        else -> {
                            onConfirm(emergencyPassword)
                        }
                    }
                }
            ) {
                Text("Set")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 