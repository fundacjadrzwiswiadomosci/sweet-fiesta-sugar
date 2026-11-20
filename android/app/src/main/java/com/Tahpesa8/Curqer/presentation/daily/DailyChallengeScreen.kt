package com.Tahpesa8.Curqer.presentation.daily

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.Tahpesa8.Curqer.core.theme.TextOnAccent
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.ClayCard
import com.Tahpesa8.Curqer.core.ui.PrimaryButton
import com.Tahpesa8.Curqer.core.ui.ScreenScaffold
import com.Tahpesa8.Curqer.core.ui.StatCardSpec
import com.Tahpesa8.Curqer.core.ui.StatRow
import com.Tahpesa8.Curqer.presentation.game.components.ObjectiveStrip

@Composable
fun DailyChallengeScreen(
    onPlayDaily: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: DailyChallengeViewModel = viewModel(factory = ServiceLocator.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    ScreenScaffold(
        titleRes = R.string.title_daily,
        overlay = listOf(
            Color(0xFFFFB03A).copy(alpha = 0.26f),
            Color(0xFFFFF0F5).copy(alpha = 0.94f)
        ),
        onMenu = onMenu
    ) {
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            accent = StarGold,
            radius = 28.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.daily_today, state.dateLabel),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.daily_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))
                ObjectiveStrip(objectives = state.objectives, largerText = false)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.game_moves, state.moves),
                        style = MaterialTheme.typography.titleMedium,
                        color = GrapePurple
                    )
                    Text(
                        text = stringResource(R.string.daily_reward, state.reward),
                        style = MaterialTheme.typography.titleMedium,
                        color = StarGold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        WeekStrip(week = state.weekDone, todayIndex = state.todayIndex)
        Spacer(modifier = Modifier.height(14.dp))
        StatRow(
            specs = listOf(
                StatCardSpec(
                    R.drawable.ic_calendar,
                    state.streak.toString(),
                    R.string.daily_streak,
                    CandyPink
                ),
                StatCardSpec(
                    R.drawable.ic_star,
                    state.reward.toString(),
                    R.string.stat_stars_won,
                    StarGold
                )
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        if (state.playedToday) {
            Text(
                text = stringResource(R.string.daily_done),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            PrimaryButton(
                text = stringResource(R.string.daily_play),
                onClick = { onPlayDaily(state.stage) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WeekStrip(week: List<Boolean>, todayIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in week.indices) {
            val done = week[index]
            val today = index == todayIndex
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        if (done) CandyPink else DisabledSurface,
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = if (today) 3.dp else 0.dp,
                        color = if (today) GrapePurple else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (done) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = TextOnAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
