package com.Tahpesa8.Curqer.presentation.gameover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.Tahpesa8.Curqer.core.navigation.Screen
import com.Tahpesa8.Curqer.core.theme.BerryRed
import com.Tahpesa8.Curqer.core.theme.GrapePurple
import com.Tahpesa8.Curqer.core.theme.MelonGreen
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.core.theme.TextOnAccent
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.AppBackground
import com.Tahpesa8.Curqer.core.ui.CandyVisuals
import com.Tahpesa8.Curqer.core.ui.ClayCard
import com.Tahpesa8.Curqer.core.ui.PrimaryButton
import com.Tahpesa8.Curqer.core.ui.SecondaryButton
import com.Tahpesa8.Curqer.core.ui.StarRow
import com.Tahpesa8.Curqer.core.ui.StatCardSpec
import com.Tahpesa8.Curqer.core.ui.StatRow
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.model.CandyType

@Composable
fun GameOverScreen(
    result: Screen.GameOver,
    onPlayAgain: () -> Unit,
    onPuzzleMap: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: GameOverViewModel = viewModel(factory = ServiceLocator.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        viewModel.refresh(result.stage)
    }

    GameOverContent(
        result = result,
        state = state,
        onPlayAgain = onPlayAgain,
        onPuzzleMap = onPuzzleMap,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    result: Screen.GameOver,
    state: GameOverUiState,
    onPlayAgain: () -> Unit,
    onPuzzleMap: () -> Unit,
    onMenu: () -> Unit
) {
    val overlay = if (result.won) {
        listOf(
            Color(0xFFFFF0F5).copy(alpha = 0.86f),
            Color(0xFFFFD9E8).copy(alpha = 0.94f)
        )
    } else {
        listOf(
            Color(0xFF6E2F86).copy(alpha = 0.80f),
            Color(0xFF9B59B6).copy(alpha = 0.88f)
        )
    }
    val titleRes = when {
        result.won -> R.string.result_win
        result.outOfTime -> R.string.result_time
        else -> R.string.result_lose
    }
    val titleColor = if (result.won) BerryRed else TextOnAccent
    val bodyColor = if (result.won) TextSecondary else Color(0xFFFFE0EB)

    AppBackground(overlay = overlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.displaySmall,
                color = titleColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            StarRow(earned = result.stars, total = 3, size = 44.dp)
            Spacer(modifier = Modifier.height(18.dp))
            StatRow(
                specs = listOf(
                    StatCardSpec(
                        R.drawable.ic_move,
                        result.movesLeft.toString(),
                        R.string.stat_moves_left,
                        GrapePurple
                    ),
                    StatCardSpec(
                        R.drawable.ic_chain,
                        result.bestChain.toString(),
                        R.string.stat_best_chain,
                        BerryRed
                    ),
                    StatCardSpec(
                        R.drawable.ic_star,
                        result.stars.toString(),
                        R.string.stat_stars_won,
                        StarGold
                    )
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            CollectedStrip(collected = result.collected, labelColor = bodyColor)
            if (result.unlockedRecipeId > 0 || result.unlockedDecorId > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                UnlockBanner(
                    recipeId = result.unlockedRecipeId,
                    decorId = result.unlockedDecorId
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.star_value, state.lifetimeStars),
                style = MaterialTheme.typography.titleMedium,
                color = if (result.won) TextPrimary else TextOnAccent
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                text = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.route_puzzle_map),
                onClick = onPuzzleMap,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CollectedStrip(collected: List<Int>, labelColor: Color) {
    val visible = CandyType.entries.filter { (collected.getOrNull(it.ordinal) ?: 0) > 0 }
    if (visible.isEmpty()) return
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.result_collected),
            style = MaterialTheme.typography.labelSmall,
            color = labelColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (type in visible) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(CandyVisuals.sprite(type)),
                        contentDescription = stringResource(
                            SampleData.candyNames.getValue(type)
                        ),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(34.dp)
                    )
                    Text(
                        text = (collected.getOrNull(type.ordinal) ?: 0).toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = labelColor
                    )
                }
            }
        }
    }
}

@Composable
private fun UnlockBanner(recipeId: Int, decorId: Int) {
    val recipe = SampleData.recipeById(recipeId)
    val decor = SampleData.decorById(decorId)
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        accent = MelonGreen,
        radius = 24.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    if (recipe != null) {
                        CandyVisuals.sprite(recipe.unlockType)
                    } else {
                        CandyVisuals.spriteSlot(decor?.spriteSlot ?: 0)
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(46.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = stringResource(
                        if (recipe != null) R.string.result_new_recipe else R.string.result_new_decor
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(recipe?.nameRes ?: decor?.nameRes ?: R.string.app_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 2
                )
            }
        }
    }
}
