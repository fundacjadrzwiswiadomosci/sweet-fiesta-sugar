package com.Tahpesa8.Curqer.presentation.menu

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.di.ServiceLocator
import com.Tahpesa8.Curqer.core.navigation.Screen
import com.Tahpesa8.Curqer.core.theme.BerryRed
import com.Tahpesa8.Curqer.core.theme.CandyPink
import com.Tahpesa8.Curqer.core.theme.CandyPinkSoft
import com.Tahpesa8.Curqer.core.theme.GrapePurple
import com.Tahpesa8.Curqer.core.theme.MelonGreen
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.core.theme.SurfaceCard
import com.Tahpesa8.Curqer.core.theme.SurfaceSunken
import com.Tahpesa8.Curqer.core.theme.SurfaceTint
import com.Tahpesa8.Curqer.core.theme.TextOnAccent
import com.Tahpesa8.Curqer.core.theme.TextOnDeep
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.AppBackground
import com.Tahpesa8.Curqer.core.ui.CandyVisuals
import com.Tahpesa8.Curqer.core.ui.ClayCard
import com.Tahpesa8.Curqer.core.ui.StatCardSpec
import com.Tahpesa8.Curqer.core.ui.StatRow
import com.Tahpesa8.Curqer.data.sample.SampleData

@Composable
fun MenuScreen(
    onPlay: (Int) -> Unit,
    onRoute: (Screen) -> Unit
) {
    val viewModel: MenuViewModel = viewModel(factory = ServiceLocator.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    MenuContent(state = state, onPlay = onPlay, onRoute = onRoute)
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: (Int) -> Unit,
    onRoute: (Screen) -> Unit
) {
    AppBackground(
        overlay = listOf(
            Color(0xFFFFF0F5).copy(alpha = 0.88f),
            Color(0xFFFFE0EB).copy(alpha = 0.93f),
            Color(0xFFF8E8F0)
        )
    ) {
        MenuBlobs()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 6.dp)
        ) {
            MenuHeader(stars = state.stars)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.menu_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            PlayTile(state = state, onPlay = onPlay)
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RouteTile(
                    labelRes = R.string.route_puzzle_map,
                    iconRes = R.drawable.ic_map,
                    accent = CandyPink,
                    hint = stringResource(R.string.fraction, state.clearedStages, state.totalStages),
                    onClick = { onRoute(Screen.PuzzleMap) }
                )
                RouteTile(
                    labelRes = R.string.route_tutorial,
                    iconRes = R.drawable.ic_book_open,
                    accent = GrapePurple,
                    hint = null,
                    onClick = { onRoute(Screen.Tutorial) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RouteTile(
                    labelRes = R.string.route_daily,
                    iconRes = R.drawable.ic_calendar,
                    accent = StarGold,
                    hint = stringResource(
                        if (state.dailyReady) R.string.ready else R.string.done_today
                    ),
                    onClick = { onRoute(Screen.DailyChallenge) }
                )
                RouteTile(
                    labelRes = R.string.route_recipe_book,
                    iconRes = R.drawable.ic_recipe,
                    accent = BerryRed,
                    hint = stringResource(R.string.fraction, state.recipesUnlocked, state.totalRecipes),
                    onClick = { onRoute(Screen.RecipeBook) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RouteTile(
                    labelRes = R.string.route_collection,
                    iconRes = R.drawable.ic_flower,
                    accent = MelonGreen,
                    hint = stringResource(
                        R.string.fraction,
                        state.collectionUnlocked,
                        state.totalCollection
                    ),
                    onClick = { onRoute(Screen.Collection) }
                )
                RouteTile(
                    labelRes = R.string.route_rewards,
                    iconRes = R.drawable.ic_gift,
                    accent = CandyPink,
                    hint = null,
                    badge = state.rewardsClaimable,
                    onClick = { onRoute(Screen.Rewards) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            WideRouteTile(
                labelRes = R.string.route_settings,
                iconRes = R.drawable.ic_sliders,
                accent = TextSecondary,
                onClick = { onRoute(Screen.Settings) }
            )
            Spacer(modifier = Modifier.height(14.dp))
            StatRow(
                specs = listOf(
                    StatCardSpec(R.drawable.ic_star, state.stars.toString(), R.string.stat_stars, StarGold),
                    StatCardSpec(
                        R.drawable.ic_recipe,
                        state.recipesUnlocked.toString(),
                        R.string.stat_recipes,
                        BerryRed
                    ),
                    StatCardSpec(
                        R.drawable.ic_chain,
                        state.bestChain.toString(),
                        R.string.stat_best_chain,
                        GrapePurple
                    )
                )
            )
            if (state.displayedDecor.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                DecorStrip(ids = state.displayedDecor)
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun MenuBlobs() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = CandyPink.copy(alpha = 0.14f),
            radius = size.minDimension * 0.55f,
            center = Offset(-60f, -40f)
        )
        drawCircle(
            color = GrapePurple.copy(alpha = 0.11f),
            radius = size.minDimension * 0.65f,
            center = Offset(size.width + 60f, size.height)
        )
    }
}

@Composable
private fun MenuHeader(stars: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            maxLines = 2,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Row(
            modifier = Modifier
                .height(44.dp)
                .background(SurfaceCard, RoundedCornerShape(22.dp))
                .border(2.dp, SurfaceTint, RoundedCornerShape(22.dp))
                .padding(horizontal = 14.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                tint = StarGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stars.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun PlayTile(state: MenuUiState, onPlay: (Int) -> Unit) {
    val shape = RoundedCornerShape(30.dp)
    val description = stringResource(R.string.cd_next_stage, state.nextStage, state.garden)
    Button(
        onClick = { onPlay(state.nextStage) },
        shape = shape,
        elevation = null,
        contentPadding = PaddingValues(horizontal = 22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextOnAccent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp)
            .shadow(22.dp, shape, ambientColor = CandyPink, spotColor = CandyPink)
            .background(
                Brush.horizontalGradient(listOf(CandyPink, CandyPinkSoft, GrapePurple)),
                shape
            )
            .semantics { stateDescription = description }
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.width(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.play),
                style = MaterialTheme.typography.displaySmall,
                color = TextOnAccent
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.menu_stage_line, state.nextStage, state.garden),
                style = MaterialTheme.typography.labelMedium,
                color = TextOnDeep
            )
        }
    }
}

@Composable
private fun RowScope.RouteTile(
    labelRes: Int,
    iconRes: Int,
    accent: Color,
    hint: String?,
    badge: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    Button(
        onClick = onClick,
        shape = shape,
        elevation = null,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextPrimary
        ),
        modifier = Modifier
            .weight(1f)
            .height(96.dp)
            .shadow(10.dp, shape, ambientColor = accent, spotColor = accent)
            .background(SurfaceCard.copy(alpha = 0.94f), shape)
            .border(2.dp, SurfaceTint, shape)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(28.dp)
                )
                if (badge) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(BerryRed, CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            if (hint != null) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun WideRouteTile(
    labelRes: Int,
    iconRes: Int,
    accent: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Button(
        onClick = onClick,
        shape = shape,
        elevation = null,
        contentPadding = PaddingValues(horizontal = 18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(8.dp, shape, ambientColor = accent, spotColor = accent)
            .background(SurfaceCard.copy(alpha = 0.94f), shape)
            .border(2.dp, SurfaceTint, shape)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DecorStrip(ids: List<Int>) {
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        accent = MelonGreen,
        radius = 22.dp,
        fill = SurfaceSunken.copy(alpha = 0.92f),
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (id in ids.take(5)) {
                val piece = SampleData.decorById(id)
                if (piece != null) {
                    Image(
                        painter = painterResource(CandyVisuals.spriteSlot(piece.spriteSlot)),
                        contentDescription = stringResource(piece.nameRes),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
        }
    }
}
