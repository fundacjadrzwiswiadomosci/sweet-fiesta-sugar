package com.Tahpesa8.Curqer.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.di.ServiceLocator
import com.Tahpesa8.Curqer.core.theme.CandyPink
import com.Tahpesa8.Curqer.core.theme.GrapePurple
import com.Tahpesa8.Curqer.core.theme.SplashDeep
import com.Tahpesa8.Curqer.core.theme.SplashDeeper
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.core.theme.TextOnAccent
import com.Tahpesa8.Curqer.core.theme.TextOnDeep
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val viewModel: SplashViewModel = viewModel(factory = ServiceLocator.factory)
    val finished by viewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "splashLoops")
    val breathe by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift"
    )
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            GrapePurple.copy(alpha = 0.94f),
                            SplashDeep.copy(alpha = 0.97f),
                            SplashDeeper
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                SparkleRing(angle = spin, drift = drift)
                Box(
                    modifier = Modifier
                        .size(188.dp)
                        .graphicsLayer {
                            scaleX = breathe
                            scaleY = breathe
                        }
                        .shadow(
                            elevation = 28.dp,
                            shape = RoundedCornerShape(36.dp),
                            ambientColor = CandyPink,
                            spotColor = CandyPink
                        )
                        .background(Color(0x26FFFFFF), RoundedCornerShape(36.dp))
                        .border(2.dp, Color(0x59FFE0EB), RoundedCornerShape(36.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(132.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = TextOnAccent,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelLarge,
                color = TextOnDeep.copy(alpha = 0.88f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(36.dp))
            LinearProgressIndicator(
                color = CandyPink,
                trackColor = Color(0x33FFFFFF),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                modifier = Modifier
                    .width(188.dp)
                    .height(8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = TextOnDeep.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SparkleRing(angle: Float, drift: Float) {
    val dotColors = listOf(CandyPink, GrapePurple, StarGold)
    Canvas(modifier = Modifier.size(300.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2.6f
        for (i in 0 until 12) {
            val theta = Math.toRadians((angle + i * 30f).toDouble())
            val x = center.x + radius * cos(theta).toFloat()
            val y = center.y + radius * sin(theta).toFloat()
            drawCircle(
                color = Color(0x80FFE0EB),
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
        for (i in 0 until 6) {
            val phase = (drift + i / 6f) % 1f
            val x = center.x + (i - 2.5f) * 34f
            val y = center.y + radius * 1.25f - phase * 160f
            drawCircle(
                color = dotColors[i % dotColors.size].copy(alpha = (1f - phase) * 0.8f),
                radius = (5 + i % 3 * 2).dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}
