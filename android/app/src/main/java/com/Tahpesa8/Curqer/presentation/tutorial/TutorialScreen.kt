package com.Tahpesa8.Curqer.presentation.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.di.ServiceLocator
import com.Tahpesa8.Curqer.core.theme.CandyPink
import com.Tahpesa8.Curqer.core.theme.DisabledSurface
import com.Tahpesa8.Curqer.core.theme.GrapePurple
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.ClayCard
import com.Tahpesa8.Curqer.core.ui.PrimaryButton
import com.Tahpesa8.Curqer.core.ui.ScreenScaffold
import com.Tahpesa8.Curqer.core.ui.SecondaryButton
import com.Tahpesa8.Curqer.core.ui.CandyVisuals
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.model.CandyType

@Composable
fun TutorialScreen(onMenu: () -> Unit) {
    val viewModel: TutorialViewModel = viewModel(factory = ServiceLocator.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    ScreenScaffold(
        titleRes = R.string.title_tutorial,
        overlay = listOf(
            Color(0xFFFFF0C2).copy(alpha = 0.80f),
            Color(0xFFFFF0F5).copy(alpha = 0.94f)
        ),
        onMenu = onMenu
    ) {
        val step = SampleData.tutorialSteps[state.step]
        ClayCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            accent = GrapePurple,
            radius = 28.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TutorialIllustration(step = state.step)
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(step.first),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(step.second),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (index in 0 until state.total) {
                        Box(
                            modifier = Modifier
                                .size(if (index == state.step) 10.dp else 7.dp)
                                .background(
                                    if (index == state.step) CandyPink else DisabledSurface,
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryButton(
                text = stringResource(R.string.back),
                onClick = { viewModel.previous() },
                modifier = Modifier.width(110.dp)
            )
            PrimaryButton(
                text = stringResource(R.string.next),
                onClick = { viewModel.next() },
                height = 48.dp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TutorialIllustration(step: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val types = CandyType.entries
        val shown = when (step) {
            0 -> listOf(types[0], types[1], types[0])
            1 -> listOf(types[2], types[2], types[2])
            2 -> listOf(types[3], types[4], types[3])
            else -> listOf(types[4], types[0], types[1])
        }
        for (type in shown) {
            Image(
                painter = painterResource(CandyVisuals.sprite(type)),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(56.dp)
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_swap),
            contentDescription = null,
            tint = StarGold,
            modifier = Modifier.size(34.dp)
        )
    }
}
