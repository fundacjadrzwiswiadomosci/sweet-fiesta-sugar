package com.Tahpesa8.Curqer.presentation.puzzlemap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tahpesa8.Curqer.core.config.GameConfig
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PuzzleMapUiState(
    val stageStars: Map<Int, Int> = emptyMap(),
    val openGardens: Int = 1,
    val currentStage: Int = 1,
    val totalStars: Int = 0
)

class PuzzleMapViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PuzzleMapUiState())
    val state: StateFlow<PuzzleMapUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            _state.update {
                it.copy(
                    stageStars = progress.stageStars,
                    openGardens = progress.unlockedGardens(
                        GameConfig.STAGES_PER_GARDEN,
                        GameConfig.GARDENS
                    ),
                    currentStage = progress.nextStage(GameConfig.TOTAL_STAGES),
                    totalStars = progress.lifetimeStars
                )
            }
        }
    }
}
