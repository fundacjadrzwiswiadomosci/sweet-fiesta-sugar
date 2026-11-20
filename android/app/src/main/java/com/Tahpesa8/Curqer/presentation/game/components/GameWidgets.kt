package com.Tahpesa8.Curqer.presentation.game.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.theme.CandyPink
import com.Tahpesa8.Curqer.core.theme.GrapePurple
import com.Tahpesa8.Curqer.core.theme.MelonGreen
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.core.theme.SurfaceCard
import com.Tahpesa8.Curqer.core.theme.SurfaceTint
import com.Tahpesa8.Curqer.core.theme.SyrupAmber
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.CandyVisuals
import com.Tahpesa8.Curqer.core.ui.PrimaryButton
import com.Tahpesa8.Curqer.core.ui.ProgressRail
import com.Tahpesa8.Curqer.core.ui.SecondaryButton
import com.Tahpesa8.Curqer.domain.model.Objective
import com.Tahpesa8.Curqer.domain.model.ObjectiveKind

@Composable
fun ObjectiveStrip(
    objectives: List<Objective>,
    largerText: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceCard.copy(alpha = 0.90f), shape)
            .border(2.dp, SurfaceTint, shape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.game_objective),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (objective in objectives) {
                ObjectiveChip(objective = objective, largerText = largerText)
            }
        }
    }
}

@Composable
private fun ObjectiveChip(objective: Objective, largerText: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (objective.kind == ObjectiveKind.SYRUP || objective.type == null) {
            Icon(
                painter = painterResource(R.drawable.ic_syrup),
                contentDescription = null,
                tint = SyrupAmber,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Image(
                painter = painterResource(CandyVisuals.sprite(objective.type)),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        if (objective.done) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = MelonGreen,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Text(
                text = stringResource(R.string.fraction, objective.progress, objective.target),
                style = if (largerText) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.titleSmall
                },
                color = TextPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ComboBadge(chain: Int, visible: Boolean, modifier: Modifier = Modifier) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(dampingRatio = 0.45f),
        label = "comboScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "comboAlpha"
    )
    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }
    ) {
        Text(
            text = stringResource(R.string.game_chain, chain),
            style = MaterialTheme.typography.headlineSmall,
            color = CandyPink,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FloatingControlBar(
    sugar: Float,
    dropText: String,
    menuText: String,
    sugarLabel: String,
    meterDescription: String,
    onDropSugar: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(30.dp)
    val transition = rememberInfiniteTransition(label = "meterShimmer")
    val shimmer by transition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    Column(
        modifier = modifier
            .shadow(20.dp, shape, ambientColor = GrapePurple, spotColor = GrapePurple)
            .background(SurfaceCard.copy(alpha = 0.95f), shape)
            .border(2.dp, SurfaceTint, shape)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = sugarLabel,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.width(10.dp))
            ProgressRail(
                fraction = sugar,
                description = meterDescription,
                colors = listOf(
                    StarGold.copy(alpha = if (sugar >= 1f) shimmer else 1f),
                    CandyPink
                ),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrimaryButton(
                text = dropText,
                onClick = onDropSugar,
                leadingIcon = R.drawable.ic_sugar_pour,
                height = 56.dp,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = menuText,
                onClick = onMenu,
                height = 56.dp,
                modifier = Modifier.width(104.dp)
            )
        }
    }
}

@Composable
fun MovesPill(
    text: String,
    warning: Boolean,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "movesPulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val scale = if (warning && !reducedMotion) pulse else 1f
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(40.dp)
            .background(if (warning) Color(0xFFE8566F) else CandyPink, shape)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_move),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}
