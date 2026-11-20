package com.Tahpesa8.Curqer.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameOverUiState(
    val lifetimeStars: Int = 0,
    val stageStars: Int = 0
)

class GameOverViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    fun refresh(stage: Int) {
        viewModelScope.launch {
            val progress = progressRepository.load()
            _state.update {
                it.copy(
                    lifetimeStars = progress.lifetimeStars,
                    stageStars = progress.stageStars[stage] ?: 0
                )
            }
        }
    }
}
