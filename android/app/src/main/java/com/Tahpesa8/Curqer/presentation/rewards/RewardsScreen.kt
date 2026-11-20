package com.Tahpesa8.Curqer.presentation.rewards

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.Tahpesa8.Curqer.core.theme.DisabledText
import com.Tahpesa8.Curqer.core.theme.MelonGreen
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.core.theme.TextOnAccent
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.CandyVisuals
import com.Tahpesa8.Curqer.core.ui.ClayCard
import com.Tahpesa8.Curqer.core.ui.PrimaryButton
import com.Tahpesa8.Curqer.core.ui.ProgressRail
import com.Tahpesa8.Curqer.core.ui.ScreenScaffold
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.model.RewardMilestone

@Composable
fun RewardsScreen(onMenu: () -> Unit) {
    val viewModel: RewardsViewModel = viewModel(factory = ServiceLocator.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    ScreenScaffold(
        titleRes = R.string.title_rewards,
        overlay = listOf(
            Color(0xFFFF69B4).copy(alpha = 0.22f),
            Color(0xFFFFB03A).copy(alpha = 0.16f),
            Color(0xFFFFF0F5).copy(alpha = 0.94f)
        ),
        onMenu = onMenu
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.rewards_next),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = stringResource(
                        R.string.fraction,
                        state.lifetimeStars,
                        state.nextMilestone
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = CandyPink
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            ProgressRail(fraction = state.progressToNext)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(SampleData.milestones) { milestone ->
                MilestoneRow(
                    milestone = milestone,
                    lifetimeStars = state.lifetimeStars,
                    claimed = state.claimed.contains(milestone.stars),
                    onClaim = { viewModel.claim(milestone.stars) }
                )
            }
        }
    }
}

@Composable
private fun MilestoneRow(
    milestone: RewardMilestone,
    lifetimeStars: Int,
    claimed: Boolean,
    onClaim: () -> Unit
) {
    val piece = SampleData.decorById(milestone.decorId)
    val reached = lifetimeStars >= milestone.stars
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        accent = if (reached) StarGold else DisabledSurface,
        radius = 24.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (reached) StarGold else DisabledSurface,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = milestone.stars.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (reached) TextOnAccent else DisabledText
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                painter = painterResource(CandyVisuals.spriteSlot(piece?.spriteSlot ?: 0)),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(38.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(piece?.nameRes ?: R.string.title_rewards),
                style = MaterialTheme.typography.labelLarge,
                color = if (reached) TextPrimary else DisabledText,
                maxLines = 2,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            when {
                claimed -> Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = MelonGreen,
                    modifier = Modifier.size(26.dp)
                )

                reached -> PrimaryButton(
                    text = stringResource(R.string.claim),
                    onClick = onClaim,
                    height = 48.dp,
                    modifier = Modifier.width(104.dp)
                )

                else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null,
                        tint = DisabledText,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(
                            R.string.rewards_more,
                            milestone.stars - lifetimeStars
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = DisabledText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
