package com.dungz.applocker.ui.screens.settings

import android.app.Activity
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dungz.applocker.ui.components.ChangePasswordDialog
import com.dungz.applocker.ui.components.ClearAllDataDialog
import com.dungz.applocker.ui.components.InputPasswordDialog
import com.dungz.applocker.ui.components.SetEmergencyPasswordDialog
import com.dungz.applocker.ui.components.UnlockAllAppDialog
import com.dungz.applocker.ui.navigation.Screen
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.ui.theme.textNormalStyle
import com.dungz.applocker.ui.theme.textSubTitleStyle
import com.dungz.applocker.ui.theme.textTitleStyle
import com.dungz.applocker.util.GlobalSnackbar
import kotlinx.coroutines.delay
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
    val context = LocalContext.current
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                            title = "Change password",
                            subtitle = "Update your app password.",
                            onClick = { viewModel.updateShowInputPasswordChangePasswordDialog(true) }
                        )

                        SettingsItem(
                            icon = Icons.Default.Security,
                            title = "Emergency password",
                            subtitle = if (!uiState.value.securitySettings.isEmergencyPasswordSet) {
                                "Set a new emergency password."
                            } else {
                                "Change the emergency password."
                            },
                            onClick = {
                                if (uiState.value.securitySettings.isEmergencyPasswordSet) {
                                    viewModel.updateShowInputPasswordEmergencyPasswordDialog(
                                        true
                                    )
                                } else {
                                    viewModel.updateShowEmergencyPasswordDialog()
                                }
                            }
                        )
                    }
                }

                item {
                    SettingsSection(title = "App management") {
                        SettingsItem(
                            icon = Icons.Default.LockOpen,
                            title = "Unlock all apps",
                            subtitle = "Remove protection from all apps.",
                            onClick = {
                                viewModel.updateShowUnlockAllAppDialog()
                            }
                        )

                        SettingsItem(
                            icon = Icons.Default.Delete,
                            title = "Clear all data",
                            subtitle = "Remove all passwords and locked apps.",
                            onClick = {
                                viewModel.updateShowClearAllDataDialog()
                            }
                        )
                    }
                }

                item {
                    SettingsSection(title = "About") {
                        SettingsItem(
                            icon = Icons.Default.Info,
                            title = "App Version",
                            subtitle = context.packageManager.getPackageInfo(
                                context.packageName,
                                0
                            ).versionName ?: "1.0.0"
                        )

                        SettingsItem(
                            icon = Icons.Default.PrivacyTip,
                            title = "Privacy policy",
                            subtitle = "Read our privacy policy.",
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
            GlobalSnackbar.setMessage("Password changed.")
            viewModel.updatePassword(it)
            viewModel.updateShowChangePasswordDialog()
        }
    }
    if (uiState.value.isShowInputPasswordChangePasswordDialog) {
        InputPasswordDialog(title = "Enter your password to continue", onDismiss = {
            viewModel.updateShowInputPasswordChangePasswordDialog(false)
        }) {
            viewModel.validatePassword(password = it, onSuccess = {
                GlobalSnackbar.setMessage("Password verified.")
                viewModel.updateShowInputPasswordChangePasswordDialog(false)
                viewModel.updateShowChangePasswordDialog()
            }, onError = {
                viewModel.updateShowInputPasswordChangePasswordDialog(false)
                GlobalSnackbar.setMessage("Incorrect password.")
            })
        }
    }

    if (uiState.value.isShowInputClearAllDataDialog) {
        InputPasswordDialog(title = "Enter your password to clear all data", onDismiss = {
            viewModel.updateShowInputClearAllDataConfirmationDialog(false)
        }) {
            viewModel.updateShowInputClearAllDataConfirmationDialog(false)
            viewModel.validatePassword(password = it, onSuccess = {
                GlobalSnackbar.setMessage("All data cleared.")
                viewModel.clearAllData()
                scope.launch {
                    delay(1000)
                    restartActivity(navController.context as Activity)
                }
            }, onError = {
                GlobalSnackbar.setMessage("Incorrect password.")
            })
        }
    }

    if (uiState.value.isShowInputPasswordEmergencyPasswordDialog) {
        InputPasswordDialog(title = "Enter your emergency password to continue", onDismiss = {
            viewModel.updateShowInputPasswordEmergencyPasswordDialog(false)
        }) {
            viewModel.validateEmergencyPassword(password = it, onSuccess = {
                viewModel.updateShowInputPasswordEmergencyPasswordDialog(false)
                viewModel.updateShowEmergencyPasswordDialog()
            }, onError = {
                viewModel.updateShowInputPasswordEmergencyPasswordDialog(false)
                GlobalSnackbar.setMessage("Incorrect emergency password.")
            })
        }
    }

    if (uiState.value.isShowEmergencyPasswordDialog) {
        navController.navigate(Screen.EmergencyPasswordSetup.route)
//        SetEmergencyPasswordDialog(
//            onDismiss = { viewModel.updateShowEmergencyPasswordDialog() },
//            onConfirm = { emergencyPassword ->
//                GlobalSnackbar.setMessage("Emergency password set.")
//                viewModel.updateEmergencyPassword(emergencyPassword)
//                viewModel.updateShowEmergencyPasswordDialog()
//            }
//        )
    }
    if (uiState.value.isShowClearAllDataDialog) {
        ClearAllDataDialog(onDismiss = { viewModel.updateShowClearAllDataDialog() }) {
            viewModel.updateShowInputClearAllDataConfirmationDialog(true)
        }
    }
    if (uiState.value.isShowUnlockAllAppDialog) {
        UnlockAllAppDialog(onDismiss = { viewModel.updateShowUnlockAllAppDialog() }) {
            viewModel.unlockAllApps()
        }
    }
}

fun restartActivity(activity: Activity) {
    val intent = activity.intent
    activity.finish()
    activity.startActivity(intent)
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
            style = textTitleStyle,
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
                    style = textSubTitleStyle
                )

                Text(
                    text = subtitle,
                    style = textNormalStyle
                )
            }

            trailing?.invoke()
        }
    }
}