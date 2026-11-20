package com.Tahpesa8.Curqer.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.theme.CandyPink
import com.Tahpesa8.Curqer.core.theme.DisabledSurface
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.core.theme.SurfaceSunken
import com.Tahpesa8.Curqer.core.theme.TextSecondary

@Composable
fun StarRow(
    earned: Int,
    total: Int,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until total) {
            Icon(
                painter = painterResource(
                    if (index < earned) R.drawable.ic_star else R.drawable.ic_star_outline
                ),
                contentDescription = null,
                tint = if (index < earned) StarGold else DisabledSurface,
                modifier = Modifier.size(size)
            )
        }
    }
}

@Composable
fun ProgressRail(
    fraction: Float,
    modifier: Modifier = Modifier,
    description: String? = null,
    colors: List<Color> = listOf(StarGold, CandyPink),
    thickness: Dp = 12.dp
) {
    val clamped = fraction.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = clamped,
        animationSpec = tween(durationMillis = 420),
        label = "railFill"
    )
    val shape = RoundedCornerShape(thickness / 2)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(SurfaceSunken, shape)
            .then(
                if (description != null) {
                    Modifier.semantics {
                        stateDescription = description
                        liveRegion = LiveRegionMode.Polite
                    }
                } else {
                    Modifier
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated)
                .fillMaxHeight()
                .background(Brush.horizontalGradient(colors), shape)
        )
    }
}

@Composable
fun EmptyState(
    spriteRes: Int,
    messageRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(spriteRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(messageRes),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
