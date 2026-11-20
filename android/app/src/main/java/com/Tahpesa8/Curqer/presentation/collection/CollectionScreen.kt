package com.Tahpesa8.Curqer.presentation.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.di.ServiceLocator
import com.Tahpesa8.Curqer.core.theme.DisabledSurface
import com.Tahpesa8.Curqer.core.theme.DisabledText
import com.Tahpesa8.Curqer.core.theme.MelonGreen
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.ui.CandyVisuals
import com.Tahpesa8.Curqer.core.ui.ClayCard
import com.Tahpesa8.Curqer.core.ui.StatCardSpec
import com.Tahpesa8.Curqer.core.ui.StatRow
import com.Tahpesa8.Curqer.core.ui.ScreenScaffold
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.model.DecorPiece

@Composable
fun CollectionScreen(onMenu: () -> Unit) {
    val viewModel: CollectionViewModel = viewModel(factory = ServiceLocator.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    ScreenScaffold(
        titleRes = R.string.title_collection,
        overlay = listOf(
            Color(0xFF7FB069).copy(alpha = 0.22f),
            Color(0xFFFFF0F5).copy(alpha = 0.94f)
        ),
        onMenu = onMenu
    ) {
        StatRow(
            specs = listOf(
                StatCardSpec(
                    R.drawable.ic_flower,
                    state.unlocked.size.toString(),
                    R.string.stat_pieces,
                    MelonGreen
                ),
                StatCardSpec(
                    R.drawable.ic_star,
                    state.spendableStars.toString(),
                    R.string.stat_stars,
                    StarGold
                )
            )
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(SampleData.decor) { piece ->
                DecorTile(
                    piece = piece,
                    unlocked = state.unlocked.contains(piece.id),
                    displayed = state.displayed.contains(piece.id),
                    affordable = state.spendableStars >= piece.cost,
                    onTap = { viewModel.onPieceTap(piece.id) }
                )
            }
        }
    }
}

@Composable
private fun DecorTile(
    piece: DecorPiece,
    unlocked: Boolean,
    displayed: Boolean,
    affordable: Boolean,
    onTap: () -> Unit
) {
    val description = stringResource(
        if (unlocked) R.string.cd_unlocked else R.string.cd_locked
    )
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp)
            .semantics { stateDescription = description }
            .clickable(onClick = onTap),
        accent = if (unlocked) MelonGreen else DisabledSurface,
        radius = 20.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (unlocked) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(StarGold.copy(alpha = 0.35f), Color.Transparent)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(CandyVisuals.spriteSlot(piece.spriteSlot)),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(40.dp)
                    )
                }
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = if (affordable) StarGold else DisabledText,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(piece.nameRes),
                style = MaterialTheme.typography.labelSmall,
                color = if (unlocked) TextPrimary else DisabledText,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            if (!unlocked && !piece.fromMilestone) {
                Text(
                    text = stringResource(R.string.star_value, piece.cost),
                    style = MaterialTheme.typography.labelSmall,
                    color = StarGold
                )
            }
            if (displayed) {
                Text(
                    text = stringResource(R.string.collection_displayed),
                    style = MaterialTheme.typography.labelSmall,
                    color = MelonGreen
                )
            }
        }
    }
}
