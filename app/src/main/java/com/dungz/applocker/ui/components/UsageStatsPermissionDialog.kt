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
        title = "Usage stats permission",
        initValue = Unit,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "AppLocker runs in the background to monitor app usage so the lock works even when the app is closed.",
                    style = textNormalStyle,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                Text(
                    text = "This permission allows:",
                    style = textTitleStyle,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(Dimen.spacingSmall))

                Text(
                    text = "• Detect when apps are opened\n" +
                            "• Show a password prompt for locked apps\n" +
                            "• Keep protection reliable",
                    style = textNormalStyle,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                Text(
                    text = "Without it, app locking may not work in the background.",
                    style = textErrorStyle,
                    textAlign = TextAlign.Start,
                )
            }
        },
        onConfirm = { onGrantPermission() },
        confirmButtonContent = "Grant permission",
        onDismiss = onCancel,
        dismissButtonContent = "Cancel"
    )
}

@Preview
@Composable
fun TestPreview() {
    UsageStatsPermissionDialog({}) { }
}