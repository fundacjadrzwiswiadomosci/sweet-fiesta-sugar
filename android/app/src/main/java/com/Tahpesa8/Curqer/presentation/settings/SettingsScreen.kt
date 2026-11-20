package com.Tahpesa8.Curqer.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.di.ServiceLocator
import com.Tahpesa8.Curqer.core.theme.BerryRed
import com.Tahpesa8.Curqer.core.theme.CandyPink
import com.Tahpesa8.Curqer.core.theme.DisabledSurface
import com.Tahpesa8.Curqer.core.theme.GrapePurple
import com.Tahpesa8.Curqer.core.theme.SurfaceSunken
import com.Tahpesa8.Curqer.core.theme.TextOnAccent
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.ClayCard
import com.Tahpesa8.Curqer.core.ui.ScreenScaffold
import com.Tahpesa8.Curqer.core.ui.SecondaryButton
import com.Tahpesa8.Curqer.presentation.dialog.ConfirmDialog

@Composable
fun SettingsScreen(onMenu: () -> Unit) {
    val viewModel: SettingsViewModel = viewModel(factory = ServiceLocator.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    ScreenScaffold(
        titleRes = R.string.title_settings,
        overlay = listOf(
            Color(0xFFF8E8F0).copy(alpha = 0.92f),
            Color(0xFFFFF0F5).copy(alpha = 0.96f)
        ),
        onMenu = onMenu
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingRow(
                labelRes = R.string.set_high_contrast,
                checked = state.settings.highContrast,
                onToggle = { viewModel.toggleHighContrast() }
            )
            SettingRow(
                labelRes = R.string.set_reduced_motion,
                checked = state.settings.reducedMotion,
                onToggle = { viewModel.toggleReducedMotion() }
            )
            SettingRow(
                labelRes = R.string.set_timed,
                checked = state.settings.timedChallenge,
                onToggle = { viewModel.toggleTimed() }
            )
            SettingRow(
                labelRes = R.string.set_larger_text,
                checked = state.settings.largerText,
                onToggle = { viewModel.toggleLargerText() }
            )
            Spacer(modifier = Modifier.height(4.dp))
            SecondaryButton(
                text = stringResource(R.string.set_reset),
                onClick = { viewModel.askReset() },
                textColor = BerryRed,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.set_version),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = stringResource(R.string.set_version_value),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Text(
                text = stringResource(R.string.set_offline_note),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (state.confirmReset) {
            ConfirmDialog(
                titleRes = R.string.set_reset_confirm,
                confirmRes = R.string.reset,
                onConfirm = { viewModel.confirmReset() },
                onCancel = { viewModel.cancelReset() }
            )
        }
    }
}

@Composable
private fun SettingRow(
    labelRes: Int,
    checked: Boolean,
    onToggle: () -> Unit
) {
    val description = stringResource(if (checked) R.string.set_on else R.string.set_off)
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        accent = GrapePurple,
        radius = 22.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sliders),
                contentDescription = null,
                tint = GrapePurple,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                maxLines = 2,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Switch(
                checked = checked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TextOnAccent,
                    checkedTrackColor = CandyPink,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = SurfaceSunken,
                    uncheckedBorderColor = DisabledSurface
                ),
                modifier = Modifier.semantics { stateDescription = description }
            )
        }
    }
}
