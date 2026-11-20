package com.Tahpesa8.Curqer.presentation.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RewardsUiState(
    val lifetimeStars: Int = 0,
    val claimed: Set<Int> = emptySet(),
    val nextMilestone: Int = 0,
    val progressToNext: Float = 0f
)

class RewardsViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RewardsUiState())
    val state: StateFlow<RewardsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val next = SampleData.milestones.firstOrNull {
                !progress.claimedMilestones.contains(it.stars)
            }
            val target = next?.stars ?: 0
            _state.update {
                it.copy(
                    lifetimeStars = progress.lifetimeStars,
                    claimed = progress.claimedMilestones,
                    nextMilestone = target,
                    progressToNext = if (target > 0) {
                        (progress.lifetimeStars.toFloat() / target).coerceIn(0f, 1f)
                    } else {
                        1f
                    }
                )
            }
        }
    }

    fun claim(stars: Int) {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val milestone = SampleData.milestones.firstOrNull { it.stars == stars } ?: return@launch
            if (progress.lifetimeStars < stars) return@launch
            if (progress.claimedMilestones.contains(stars)) return@launch
            val updated = progress.copy(
                claimedMilestones = progress.claimedMilestones + stars,
                unlockedDecor = progress.unlockedDecor + milestone.decorId
            )
            progressRepository.save(updated)
            refresh()
        }
    }
}
