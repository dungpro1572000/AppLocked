package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.ui.theme.textErrorStyle
import com.dungz.applocker.ui.theme.textNormalStyle

@Composable
fun DismissPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BaseConfirmDialog(
        title = "Permission Denied",
        initValue = Unit,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "You have denied the permission. Some features may not work properly without this permission.",
                    style = textNormalStyle,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(Dimen.spacingMedium))
                Text(
                    text = "You can grant the permission later in the app settings if you change your mind.",
                    style = textErrorStyle,
                    textAlign = TextAlign.Start,
                )
            }
        },
        onConfirm = { onConfirm() },
        confirmButtonContent = "Grant permission",
        onDismiss = onDismiss,
    )
}