package com.Tahpesa8.Curqer.presentation.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tahpesa8.Curqer.core.config.GameConfig
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.model.Objective
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DailyChallengeUiState(
    val stage: Int = 1,
    val moves: Int = GameConfig.MOVES_BASE,
    val objectives: List<Objective> = emptyList(),
    val playedToday: Boolean = false,
    val streak: Int = 0,
    val weekDone: List<Boolean> = List(7) { false },
    val todayIndex: Int = 6,
    val dateLabel: String = "",
    val reward: Int = GameConfig.DAILY_STAR_BONUS
)

class DailyChallengeViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DailyChallengeUiState())
    val state: StateFlow<DailyChallengeUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val today = LocalDate.now()
            val epochDay = today.toEpochDay()
            val spec = SampleData.dailySpec(epochDay)
            val week = (0 until 7).map { offset ->
                progress.dailyDays.contains(epochDay - (6 - offset))
            }
            _state.update {
                it.copy(
                    stage = spec.index,
                    moves = spec.moves,
                    objectives = spec.objectives,
                    playedToday = progress.dailyLastDay == epochDay,
                    streak = progress.dailyStreak,
                    weekDone = week,
                    todayIndex = 6,
                    dateLabel = "${today.dayOfMonth} ${today.month.name.take(3)}"
                )
            }
        }
    }
}
