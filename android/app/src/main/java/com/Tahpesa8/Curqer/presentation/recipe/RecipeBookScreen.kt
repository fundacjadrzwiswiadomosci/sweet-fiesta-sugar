package com.Tahpesa8.Curqer.presentation.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
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
import com.Tahpesa8.Curqer.core.theme.BerryRed
import com.Tahpesa8.Curqer.core.theme.DisabledSurface
import com.Tahpesa8.Curqer.core.theme.DisabledText
import com.Tahpesa8.Curqer.core.theme.TextPrimary
import com.Tahpesa8.Curqer.core.theme.TextSecondary
import com.Tahpesa8.Curqer.core.ui.CandyVisuals
import com.Tahpesa8.Curqer.core.ui.ClayCard
import com.Tahpesa8.Curqer.core.ui.EmptyState
import com.Tahpesa8.Curqer.core.ui.ScreenScaffold
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.model.Recipe

@Composable
fun RecipeBookScreen(onMenu: () -> Unit) {
    val viewModel: RecipeBookViewModel = viewModel(factory = ServiceLocator.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    ScreenScaffold(
        titleRes = R.string.title_recipe_book,
        overlay = listOf(
            Color(0xFFE8566F).copy(alpha = 0.20f),
            Color(0xFFFFF0F5).copy(alpha = 0.94f)
        ),
        onMenu = onMenu
    ) {
        Text(
            text = stringResource(
                R.string.fraction,
                state.unlocked.size,
                state.total
            ) + " " + stringResource(R.string.stat_recipes),
            style = MaterialTheme.typography.titleMedium,
            color = BerryRed,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        if (state.unlocked.isEmpty()) {
            EmptyState(
                spriteRes = CandyVisuals.spriteSlot(2),
                messageRes = R.string.recipe_book_empty,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(SampleData.recipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        unlocked = state.unlocked.contains(recipe.id)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(recipe: Recipe, unlocked: Boolean) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        accent = if (unlocked) BerryRed else DisabledSurface,
        radius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (unlocked) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (ingredient in recipe.ingredients) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(CandyVisuals.sprite(ingredient.first)),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = ingredient.second.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(recipe.nameRes),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 3
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = DisabledText,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.recipe_locked_hint,
                        recipe.unlockCount,
                        stringResource(SampleData.candyPlurals.getValue(recipe.unlockType))
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = DisabledText,
                    textAlign = TextAlign.Center,
                    maxLines = 3
                )
            }
        }
    }
}
