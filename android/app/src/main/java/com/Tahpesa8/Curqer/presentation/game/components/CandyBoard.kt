package com.Tahpesa8.Curqer.presentation.game.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.theme.SurfaceCard
import com.Tahpesa8.Curqer.core.theme.SurfaceTint
import com.Tahpesa8.Curqer.core.theme.SyrupAmber
import com.Tahpesa8.Curqer.core.theme.SyrupGloss
import com.Tahpesa8.Curqer.core.theme.SyrupRim
import com.Tahpesa8.Curqer.core.theme.GrapePurple
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.core.ui.CandyVisuals
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.model.BoardState
import com.Tahpesa8.Curqer.domain.model.Cell
import kotlin.math.abs

@Composable
fun CandyBoard(
    board: BoardState,
    tile: Dp,
    frame: Dp,
    clearing: Set<Int>,
    highContrast: Boolean,
    reducedMotion: Boolean,
    enabled: Boolean,
    onSwap: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(28.dp)
    val tilePx = with(LocalDensity.current) { tile.toPx() }
    val framePx = with(LocalDensity.current) { frame.toPx() }
    Box(
        modifier = modifier
            .shadow(18.dp, shape, ambientColor = GrapePurple, spotColor = GrapePurple)
            .background(SurfaceCard.copy(alpha = 0.90f), shape)
            .border(2.dp, SurfaceTint, shape)
            .padding(frame)
            .pointerInput(board, enabled) {
                if (!enabled) return@pointerInput
                var startIndex = -1
                var total = Offset.Zero
                detectDragGestures(
                    onDragStart = { offset ->
                        total = Offset.Zero
                        val col = ((offset.x - framePx) / tilePx).toInt()
                        val row = ((offset.y - framePx) / tilePx).toInt()
                        startIndex = if (col in 0 until board.cols && row in 0 until board.rows) {
                            row * board.cols + col
                        } else {
                            -1
                        }
                    },
                    onDrag = { change, amount ->
                        change.consume()
                        total += amount
                    },
                    onDragEnd = {
                        val origin = startIndex
                        startIndex = -1
                        if (origin >= 0) {
                            val threshold = tilePx * 0.35f
                            val target = when {
                                abs(total.x) > abs(total.y) && total.x > threshold -> origin + 1
                                abs(total.x) > abs(total.y) && total.x < -threshold -> origin - 1
                                abs(total.y) >= abs(total.x) && total.y > threshold -> origin + board.cols
                                abs(total.y) >= abs(total.x) && total.y < -threshold -> origin - board.cols
                                else -> -1
                            }
                            if (target in board.cells.indices) {
                                val sameRow = origin / board.cols == target / board.cols
                                val horizontal = abs(origin - target) == 1
                                if (!horizontal || sameRow) onSwap(origin, target)
                            }
                        }
                    }
                )
            }
    ) {
        Column(verticalArrangement = Arrangement.Top) {
            for (row in 0 until board.rows) {
                Row(horizontalArrangement = Arrangement.Start) {
                    for (col in 0 until board.cols) {
                        val index = row * board.cols + col
                        CandyTile(
                            cell = board.cells[index],
                            tile = tile,
                            clearing = clearing.contains(index),
                            highContrast = highContrast,
                            reducedMotion = reducedMotion
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CandyTile(
    cell: Cell,
    tile: Dp,
    clearing: Boolean,
    highContrast: Boolean,
    reducedMotion: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (clearing) 0.1f else 1f,
        animationSpec = if (reducedMotion) tween(90) else spring(dampingRatio = 0.62f),
        label = "tileScale"
    )
    val puff = if (highContrast) CandyVisuals.accent(cell.type) else CandyVisuals.puff(cell.type)
    val accent = CandyVisuals.accent(cell.type)
    val shape = RoundedCornerShape(18.dp)
    val description = stringResource(
        R.string.cd_candy_cell,
        stringResource(SampleData.candyNames.getValue(cell.type)),
        cell.syrup
    )
    Box(
        modifier = Modifier
            .size(tile)
            .padding(3.dp)
            .semantics { stateDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(6.dp, shape, ambientColor = accent, spotColor = accent)
                .background(puff, shape)
                .border(if (highContrast) 3.dp else 2.dp, accent.copy(alpha = 0.45f), shape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(CandyVisuals.sprite(cell.type)),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(tile - 20.dp)
            )
            if (highContrast) {
                ShapeMarker(type = CandyVisuals.marker(cell.type), accent = accent)
            }
            if (cell.spark) {
                SparkOverlay(reducedMotion = reducedMotion)
            }
            if (cell.syrup > 0) {
                SyrupOverlay(layers = cell.syrup, shape = shape)
            }
        }
    }
}

@Composable
private fun ShapeMarker(type: Int, accent: Color) {
    val markerShape = when (type) {
        0 -> CircleShape
        1 -> RectangleShape
        2 -> RoundedCornerShape(4.dp)
        3 -> CutCornerShape(5.dp)
        else -> CutCornerShape(topStart = 7.dp, bottomEnd = 7.dp, topEnd = 0.dp, bottomStart = 0.dp)
    }
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(12.dp)
            .background(Color.White.copy(alpha = 0.9f), markerShape)
            .border(1.dp, accent, markerShape)
    )
}

@Composable
private fun SparkOverlay(reducedMotion: Boolean) {
    val transition = rememberInfiniteTransition(label = "sparkGlow")
    val glow by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkAlpha"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(
                        Color.White.copy(alpha = if (reducedMotion) 0.8f else glow),
                        Color.Transparent
                    )
                ),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_spark),
            contentDescription = null,
            tint = StarGold,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun SyrupOverlay(layers: Int, shape: RoundedCornerShape) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        SyrupGloss.copy(alpha = if (layers > 1) 0.92f else 0.72f),
                        SyrupAmber.copy(alpha = if (layers > 1) 0.86f else 0.64f)
                    )
                ),
                shape
            )
            .border(2.dp, SyrupRim, shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_syrup),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(18.dp)
        )
    }
}
