package com.Tahpesa8.Curqer.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.theme.SurfaceCard
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.SecondaryButton

@Composable
fun PauseDialog(onResume: () -> Unit, onMenu: () -> Unit) {
    AlertDialog(
        onDismissRequest = onResume,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        shape = RoundedCornerShape(28.dp),
        containerColor = SurfaceCard,
        title = {
            Text(
                text = stringResource(R.string.paused),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = stringResource(R.string.game_swap_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        },
        confirmButton = {
            SecondaryButton(
                text = stringResource(R.string.resume),
                onClick = onResume,
                modifier = Modifier.width(140.dp)
            )
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.End) {
                SecondaryButton(
                    text = stringResource(R.string.menu),
                    onClick = onMenu,
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    )
}

@Composable
fun ConfirmDialog(
    titleRes: Int,
    confirmRes: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        shape = RoundedCornerShape(28.dp),
        containerColor = SurfaceCard,
        title = {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        },
        confirmButton = {
            SecondaryButton(
                text = stringResource(confirmRes),
                onClick = onConfirm,
                modifier = Modifier.width(130.dp)
            )
        },
        dismissButton = {
            SecondaryButton(
                text = stringResource(R.string.cancel),
                onClick = onCancel,
                modifier = Modifier.width(130.dp)
            )
        }
    )
}
