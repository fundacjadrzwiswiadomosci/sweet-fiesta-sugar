package com.Tahpesa8.Curqer.presentation.puzzlemap

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.config.GameConfig
import com.Tahpesa8.Curqer.core.di.ServiceLocator
import com.Tahpesa8.Curqer.core.theme.CandyPink
import com.Tahpesa8.Curqer.core.theme.CandyPinkSoft
import com.Tahpesa8.Curqer.core.theme.DisabledSurface
import com.Tahpesa8.Curqer.core.theme.DisabledText
import com.Tahpesa8.Curqer.core.theme.GrapePurple
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.core.theme.TextOnAccent
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.ClayCard
import com.Tahpesa8.Curqer.core.ui.ScreenScaffold
import com.Tahpesa8.Curqer.core.ui.StarRow
import com.Tahpesa8.Curqer.data.sample.SampleData

@Composable
fun PuzzleMapScreen(
    onPlayStage: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: PuzzleMapViewModel = viewModel(factory = ServiceLocator.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    ScreenScaffold(
        titleRes = R.string.title_puzzle_map,
        overlay = listOf(
            Color(0xFFD9F0D3).copy(alpha = 0.82f),
            Color(0xFFFFE0EB).copy(alpha = 0.92f)
        ),
        onMenu = onMenu
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items((1..GameConfig.GARDENS).toList()) { garden ->
                GardenCard(
                    garden = garden,
                    open = garden <= state.openGardens,
                    stageStars = state.stageStars,
                    currentStage = state.currentStage,
                    onPlayStage = onPlayStage
                )
            }
        }
    }
}

@Composable
private fun GardenCard(
    garden: Int,
    open: Boolean,
    stageStars: Map<Int, Int>,
    currentStage: Int,
    onPlayStage: (Int) -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        accent = if (open) CandyPink else DisabledSurface,
        radius = 28.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(SampleData.gardenNames[garden - 1]),
                style = MaterialTheme.typography.titleMedium,
                color = if (open) TextPrimary else DisabledText
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (open) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (slot in 0 until GameConfig.STAGES_PER_GARDEN) {
                        val stage = (garden - 1) * GameConfig.STAGES_PER_GARDEN + slot + 1
                        StageNode(
                            stage = stage,
                            stars = stageStars[stage] ?: 0,
                            current = stage == currentStage,
                            onPlayStage = onPlayStage
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(
                        R.string.map_locked_garden,
                        stringResource(SampleData.gardenNames[(garden - 2).coerceAtLeast(0)])
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StageNode(
    stage: Int,
    stars: Int,
    current: Boolean,
    onPlayStage: (Int) -> Unit
) {
    val transition = rememberInfiniteTransition(label = "nodePulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nodeScale"
    )
    val cleared = stars > 0
    val description = stringResource(R.string.cd_stage_node, stage, stars)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .graphicsLayer {
                    val value = if (current) pulse else 1f
                    scaleX = value
                    scaleY = value
                }
                .background(
                    brush = if (cleared || current) {
                        Brush.verticalGradient(listOf(CandyPink, CandyPinkSoft))
                    } else {
                        Brush.verticalGradient(listOf(DisabledSurface, DisabledSurface))
                    },
                    shape = CircleShape
                )
                .border(
                    width = if (current) 3.dp else 2.dp,
                    color = if (current) GrapePurple else Color(0x66FFFFFF),
                    shape = CircleShape
                )
                .semantics { stateDescription = description }
                .clickable(onClick = { onPlayStage(stage) }),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.map_stage_node, stage),
                style = MaterialTheme.typography.titleSmall,
                color = if (cleared || current) TextOnAccent else DisabledText
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        if (cleared) {
            StarRow(earned = stars, total = 3, size = 11.dp)
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = null,
                tint = if (current) StarGold else DisabledText,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
