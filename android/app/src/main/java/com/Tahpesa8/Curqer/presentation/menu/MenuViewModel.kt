package com.Tahpesa8.Curqer.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tahpesa8.Curqer.core.config.GameConfig
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import com.Tahpesa8.Curqer.domain.usecase.UnlockRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class MenuUiState(
    val stars: Int = 0,
    val nextStage: Int = 1,
    val garden: Int = 1,
    val clearedStages: Int = 0,
    val totalStages: Int = GameConfig.TOTAL_STAGES,
    val recipesUnlocked: Int = 0,
    val totalRecipes: Int = 0,
    val collectionUnlocked: Int = 0,
    val totalCollection: Int = 0,
    val bestChain: Int = 0,
    val dailyReady: Boolean = true,
    val rewardsClaimable: Boolean = false,
    val displayedDecor: List<Int> = emptyList()
)

class MenuViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    private val unlockRecipes = UnlockRecipesUseCase()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val today = LocalDate.now().toEpochDay()
            val recipes = unlockRecipes(SampleData.recipes, progress.ingredientCounts)
            val claimable = SampleData.milestones.any {
                progress.lifetimeStars >= it.stars && !progress.claimedMilestones.contains(it.stars)
            }
            val nextStage = progress.nextStage(GameConfig.TOTAL_STAGES)
            _state.update {
                it.copy(
                    stars = progress.lifetimeStars,
                    nextStage = nextStage,
                    garden = GameConfig.gardenOf(nextStage),
                    clearedStages = progress.clearedStages,
                    recipesUnlocked = recipes.size,
                    totalRecipes = SampleData.recipes.size,
                    collectionUnlocked = progress.unlockedDecor.size,
                    totalCollection = SampleData.decor.size,
                    bestChain = progress.bestChain,
                    dailyReady = progress.dailyLastDay != today,
                    rewardsClaimable = claimable,
                    displayedDecor = progress.displayedDecor.sorted()
                )
            }
        }
    }
}
