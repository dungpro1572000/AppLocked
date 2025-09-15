package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dungz.applocker.ui.theme.textSubTitleStyle
import com.dungz.applocker.ui.theme.textTitleStyle

@Composable
fun UnlockAllAppDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BaseConfirmDialog(
        title = "Unlock All Apps",
        content = {
            Column {
                Text(
                    text = "Are you sure you want to unlock all apps?",
                    style = textSubTitleStyle,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        },
        onConfirm = { onConfirm() },
        confirmButtonContent = "Unlock All",
        onDismiss = onDismiss,
        initValue = Unit,
    )
}