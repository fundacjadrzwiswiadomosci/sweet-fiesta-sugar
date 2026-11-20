package com.Tahpesa8.Curqer.presentation.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.model.CandyType
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import com.Tahpesa8.Curqer.domain.usecase.UnlockRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeBookUiState(
    val unlocked: Set<Int> = emptySet(),
    val counts: Map<CandyType, Int> = emptyMap(),
    val total: Int = SampleData.recipes.size
)

class RecipeBookViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeBookUiState())
    val state: StateFlow<RecipeBookUiState> = _state.asStateFlow()

    private val unlockRecipes = UnlockRecipesUseCase()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            _state.update {
                it.copy(
                    unlocked = unlockRecipes(SampleData.recipes, progress.ingredientCounts),
                    counts = progress.ingredientCounts
                )
            }
        }
    }
}
