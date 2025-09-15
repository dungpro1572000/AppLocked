package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.ui.theme.textErrorStyle
import com.dungz.applocker.ui.theme.textNormalStyle
import com.dungz.applocker.ui.theme.textTitleStyle

@Composable
fun UsageStatsPermissionDialog(
    onGrantPermission: () -> Unit,
    onCancel: () -> Unit
) {
    BaseConfirmDialog(
        title = "Usage-Stats Service Permission",
        initValue = Unit,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "App Locker needs to run in the background to monitor app usage and ensure the app locking mechanism works correctly even when the app is not open.",
                    style = textNormalStyle,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                Text(
                    text = "This permission allows the app to:",
                    style = textTitleStyle,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(Dimen.spacingSmall))

                Text(
                    text = "• Monitor which apps are being opened\n" +
                            "• Show password prompts when locked apps are accessed\n" +
                            "• Ensure app protection works reliably",
                    style = textNormalStyle,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                Text(
                    text = "Without this permission, the app locking feature may not work properly when the app is in the background.",
                    style = textErrorStyle,
                    textAlign = TextAlign.Start,
                )
            }
        },
        onConfirm = { onGrantPermission() },
        confirmButtonContent = "Grant Permission",
        onDismiss = onCancel,
        dismissButtonContent = "Cancel"
    )
}

@Preview
@Composable
fun test() {
    UsageStatsPermissionDialog({}) { }
}