package com.Tahpesa8.Curqer.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.config.GameConfig
import com.Tahpesa8.Curqer.core.di.ServiceLocator
import com.Tahpesa8.Curqer.core.navigation.Screen
import com.Tahpesa8.Curqer.core.theme.CandyPink
import com.Tahpesa8.Curqer.core.theme.GrapePurple
import com.Tahpesa8.Curqer.core.theme.SurfaceCard
import com.Tahpesa8.Curqer.core.theme.SurfaceTint
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.AppBackground
import com.Tahpesa8.Curqer.core.ui.ProgressRail
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.presentation.dialog.PauseDialog
import com.Tahpesa8.Curqer.presentation.game.components.CandyBoard
import com.Tahpesa8.Curqer.presentation.game.components.ComboBadge
import com.Tahpesa8.Curqer.presentation.game.components.FloatingControlBar
import com.Tahpesa8.Curqer.presentation.game.components.MovesPill
import com.Tahpesa8.Curqer.presentation.game.components.ObjectiveStrip
import kotlin.math.floor

@Composable
fun GameScreen(
    stage: Int,
    daily: Boolean,
    token: Long,
    onFinished: (Screen.GameOver) -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: GameViewModel = viewModel(factory = ServiceLocator.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(token) {
        viewModel.start(stage, daily, token)
    }

    LaunchedEffect(Unit) {
        viewModel.outcome.collect { outcome ->
            onFinished(
                Screen.GameOver(
                    stage = outcome.stage,
                    daily = outcome.daily,
                    won = outcome.won,
                    outOfTime = outcome.outOfTime,
                    movesLeft = outcome.movesLeft,
                    bestChain = outcome.bestChain,
                    stars = outcome.stars,
                    collected = outcome.collected,
                    syrupCleared = outcome.syrupCleared,
                    unlockedRecipeId = outcome.unlockedRecipeId,
                    unlockedDecorId = outcome.unlockedDecorId
                )
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.pause() }
    }

    BackHandler(enabled = true) {
        if (state.paused) viewModel.closePause() else viewModel.openPause()
    }

    GameContent(
        state = state,
        onSwap = { from, to -> viewModel.onSwap(from, to) },
        onDropSugar = { viewModel.onDropSugar() },
        onPause = { viewModel.openPause() },
        onResume = { viewModel.closePause() },
        onMenu = onMenu
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onSwap: (Int, Int) -> Unit,
    onDropSugar: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onMenu: () -> Unit
) {
    AppBackground(
        overlay = listOf(
            Color(0xFFF8E8F0).copy(alpha = 0.92f),
            Color(0xFFFFE0EB).copy(alpha = 0.90f),
            Color(0xFFFFF0F5).copy(alpha = 0.95f)
        )
    ) {
        CandyStripes()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 150.dp)
            ) {
                GameHeader(state = state, onPause = onPause)
                Spacer(modifier = Modifier.height(12.dp))
                ObjectiveStrip(
                    objectives = state.objectives,
                    largerText = state.settings.largerText,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                if (state.timeLeft >= 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        ProgressRail(
                            fraction = state.timeLeft.toFloat() / GameConfig.TIMED_SECONDS,
                            thickness = 8.dp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.game_timer, state.timeLeft),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                BoardArea(
                    state = state,
                    onSwap = onSwap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Text(
                    text = stringResource(R.string.game_swap_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
            FloatingControlBar(
                sugar = state.sugar,
                dropText = stringResource(R.string.game_drop_sugar),
                menuText = stringResource(R.string.menu),
                sugarLabel = stringResource(R.string.game_sugar_rush),
                meterDescription = stringResource(
                    R.string.cd_sugar_meter,
                    (state.sugar * 100).toInt()
                ),
                onDropSugar = onDropSugar,
                onMenu = onMenu,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 18.dp, end = 18.dp, bottom = 22.dp)
                    .fillMaxWidth()
            )
        }
        if (state.paused) {
            PauseDialog(onResume = onResume, onMenu = onMenu)
        }
    }
}

@Composable
private fun CandyStripes() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        var y = -size.width
        while (y < size.height + size.width) {
            drawLine(
                color = Color(0x59FFE0EB),
                start = Offset(0f, y),
                end = Offset(size.width, y + size.width * 0.22f),
                strokeWidth = 14f
            )
            y += 86f
        }
    }
}

@Composable
private fun GameHeader(state: GameUiState, onPause: () -> Unit) {
    val shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .shadow(12.dp, shape, ambientColor = GrapePurple, spotColor = GrapePurple)
            .background(SurfaceCard.copy(alpha = 0.88f), shape)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(SurfaceTint, CircleShape)
                .border(2.dp, Color(0x66FFFFFF), CircleShape)
                .clickable(onClick = onPause),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.icon_pause),
                contentDescription = stringResource(R.string.cd_pause),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(26.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.game_stage, state.stage),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = stringResource(
                    R.string.game_garden_line,
                    state.garden,
                    stringResource(SampleData.gardenNames[(state.garden - 1).coerceIn(0, 3)])
                ),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1
            )
        }
        MovesPill(
            text = stringResource(R.string.game_moves, state.movesLeft),
            warning = state.movesLeft <= 2,
            reducedMotion = state.settings.reducedMotion,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
        )
    }
}

@Composable
private fun BoardArea(
    state: GameUiState,
    onSwap: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        val frame = GameConfig.BOARD_FRAME_DP.dp
        val widthBudget = minOf(maxWidth - 32.dp, GameConfig.BOARD_MAX_DP.dp)
        val heightBudget = maxHeight
        val tileFromWidth = floor(((widthBudget - frame * 2) / GameConfig.COLS).value)
        val tileFromHeight = floor(((heightBudget - frame * 2) / GameConfig.ROWS).value)
        val tile = minOf(tileFromWidth, tileFromHeight).dp
        val boardWidth = tile * GameConfig.COLS + frame * 2
        val boardHeight = tile * GameConfig.ROWS + frame * 2
        if (state.board.cells.size == GameConfig.COLS * GameConfig.ROWS) {
            CandyBoard(
                board = state.board,
                tile = tile,
                frame = frame,
                clearing = state.clearing,
                highContrast = state.settings.highContrast,
                reducedMotion = state.settings.reducedMotion,
                enabled = !state.busy && !state.paused,
                onSwap = onSwap,
                modifier = Modifier
                    .width(boardWidth)
                    .height(boardHeight)
            )
        }
        ComboBadge(
            chain = state.chain,
            visible = state.showChain,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
        )
    }
}
