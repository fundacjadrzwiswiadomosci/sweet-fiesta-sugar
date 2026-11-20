package com.Tahpesa8.Curqer.presentation.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CollectionUiState(
    val unlocked: Set<Int> = emptySet(),
    val displayed: Set<Int> = emptySet(),
    val spendableStars: Int = 0,
    val total: Int = SampleData.decor.size
)

class CollectionViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionUiState())
    val state: StateFlow<CollectionUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            _state.update {
                it.copy(
                    unlocked = progress.unlockedDecor,
                    displayed = progress.displayedDecor,
                    spendableStars = progress.spendableStars
                )
            }
        }
    }

    fun onPieceTap(id: Int) {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val piece = SampleData.decorById(id) ?: return@launch
            val updated = if (progress.unlockedDecor.contains(id)) {
                val displayed = if (progress.displayedDecor.contains(id)) {
                    progress.displayedDecor - id
                } else {
                    progress.displayedDecor + id
                }
                progress.copy(displayedDecor = displayed)
            } else {
                if (piece.fromMilestone || progress.spendableStars < piece.cost) return@launch
                progress.copy(
                    unlockedDecor = progress.unlockedDecor + id,
                    spentStars = progress.spentStars + piece.cost
                )
            }
            progressRepository.save(updated)
            _state.update {
                it.copy(
                    unlocked = updated.unlockedDecor,
                    displayed = updated.displayedDecor,
                    spendableStars = updated.spendableStars
                )
            }
        }
    }
}
