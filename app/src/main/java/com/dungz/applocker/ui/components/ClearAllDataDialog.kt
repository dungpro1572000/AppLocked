package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.dungz.applocker.ui.theme.textSubTitleStyle
import com.dungz.applocker.ui.theme.textTitleStyle

@Composable
fun ClearAllDataDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BaseConfirmDialog(
        title = "Clear All Data",
        content = {
           Column {
               Text(
                    text = "Are you sure you want to delete all data?",
                    style = textSubTitleStyle,
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        onConfirm = { onConfirm() },
        confirmButtonContent = "Clear All",
        onDismiss = onDismiss,
        initValue = Unit,
    )
}