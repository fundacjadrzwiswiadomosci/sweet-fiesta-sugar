package com.Tahpesa8.Curqer.presentation.tutorial

import androidx.lifecycle.ViewModel
import com.Tahpesa8.Curqer.data.sample.SampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TutorialUiState(
    val step: Int = 0,
    val total: Int = SampleData.tutorialSteps.size
)

class TutorialViewModel : ViewModel() {

    private val _state = MutableStateFlow(TutorialUiState())
    val state: StateFlow<TutorialUiState> = _state.asStateFlow()

    fun next() {
        _state.update {
            it.copy(step = if (it.step + 1 >= it.total) 0 else it.step + 1)
        }
    }

    fun previous() {
        _state.update {
            it.copy(step = if (it.step - 1 < 0) it.total - 1 else it.step - 1)
        }
    }
}
