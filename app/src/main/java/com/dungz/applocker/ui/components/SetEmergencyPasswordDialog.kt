package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dungz.applocker.ui.theme.Dimen

@Composable
fun SetEmergencyPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var emergencyPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    BaseConfirmDialog(
        title = "Set Emergency Password",
        initValue = emergencyPassword,
        content = {
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
        onConfirm = {
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
        },
        confirmButtonContent = "Set",
        onDismiss = onDismiss,
    )
}