package com.dungz.applocker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dungz.applocker.ui.navigation.Screen
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEmergencyPasswordDialog by remember { mutableStateOf(false) }

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
                            onClick = { showChangePasswordDialog = true }
                        )
                        
                        SettingsItem(
                            icon = Icons.Default.Security,
                            title = "Emergency Password",
                            subtitle = if (uiState.securitySettings.isEmergencyPasswordSet) {
                                "Emergency password is set"
                            } else {
                                "Set emergency password"
                            },
                            onClick = { showEmergencyPasswordDialog = true }
                        )
                        
                        SettingsItem(
                            icon = Icons.Default.Fingerprint,
                            title = "Biometric Authentication",
                            subtitle = "Use fingerprint or face unlock",
                            trailing = {
                                Switch(
                                    checked = uiState.securitySettings.isBiometricEnabled,
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
                                // TODO: Implement unlock all apps
                            }
                        )
                        
                        SettingsItem(
                            icon = Icons.Default.Delete,
                            title = "Clear All Data",
                            subtitle = "Remove all passwords and locked apps",
                            onClick = {
                                // TODO: Implement clear all data
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
    
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { newPassword ->
                viewModel.setPassword(newPassword)
                showChangePasswordDialog = false
            }
        )
    }
    
    if (showEmergencyPasswordDialog) {
        EmergencyPasswordDialog(
            onDismiss = { showEmergencyPasswordDialog = false },
            onConfirm = { emergencyPassword ->
                viewModel.setEmergencyPassword(emergencyPassword)
                showEmergencyPasswordDialog = false
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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