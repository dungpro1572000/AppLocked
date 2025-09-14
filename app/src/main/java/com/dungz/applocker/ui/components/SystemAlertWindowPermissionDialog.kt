package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dungz.applocker.ui.theme.Dimen

@Composable
fun SystemAlertWindowPermissionDialog(
    onGrantPermission: () -> Unit,
    onCancel: () -> Unit
) {
    BaseConfirmDialog(
        title = "System Alert Window Permission",
        initValue = Unit,
        content = {
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
        onConfirm = { onGrantPermission() },
        confirmButtonContent = "Grant Permission",
        onDismiss = onCancel,
    )
}