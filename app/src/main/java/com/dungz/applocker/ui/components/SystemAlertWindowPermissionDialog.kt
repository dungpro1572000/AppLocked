package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import com.dungz.applocker.ui.theme.Dimen

@Composable
fun SystemAlertWindowPermissionDialog(
    onGrantPermission: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true,
        ),
        title = {
            Text(
                text = "System Alert Window Permission",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "App Locker needs permission to display over other apps to show password prompts and lock screens when accessing protected apps.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                Text(
                    text = "This permission allows the app to:",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(Dimen.spacingSmall))

                Text(
                    text = "• Display password prompts over any app\n" +
                            "• Show lock screens when protected apps are opened\n" +
                            "• Display security notifications and alerts\n" +
                            "• Ensure app protection works across all applications",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                Text(
                    text = "Without this permission, the app cannot display lock screens over other apps and the protection feature will not work.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onGrantPermission,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.padding(Dimen.paddingMedium)
    )
}