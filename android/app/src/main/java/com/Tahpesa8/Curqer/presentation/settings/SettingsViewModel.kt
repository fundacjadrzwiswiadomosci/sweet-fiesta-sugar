package com.Tahpesa8.Curqer.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tahpesa8.Curqer.domain.model.GameSettings
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import com.Tahpesa8.Curqer.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: GameSettings = GameSettings(),
    val confirmReset: Boolean = false,
    val lifetimeStars: Int = 0
)

class SettingsViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val settings = settingsRepository.load()
            val progress = progressRepository.load()
            _state.update {
                it.copy(settings = settings, lifetimeStars = progress.lifetimeStars)
            }
        }
    }

    fun toggleHighContrast() = updateSettings { current -> current.copy(highContrast = !current.highContrast) }

    fun toggleReducedMotion() = updateSettings { current -> current.copy(reducedMotion = !current.reducedMotion) }

    fun toggleTimed() = updateSettings { current -> current.copy(timedChallenge = !current.timedChallenge) }

    fun toggleLargerText() = updateSettings { current -> current.copy(largerText = !current.largerText) }

    fun askReset() {
        _state.update { it.copy(confirmReset = true) }
    }

    fun cancelReset() {
        _state.update { it.copy(confirmReset = false) }
    }

    fun confirmReset() {
        viewModelScope.launch {
            progressRepository.clear()
            _state.update { it.copy(confirmReset = false, lifetimeStars = 0) }
        }
    }

    private fun updateSettings(change: (GameSettings) -> GameSettings) {
        viewModelScope.launch {
            val updated = change(_state.value.settings)
            settingsRepository.save(updated)
            _state.update { it.copy(settings = updated) }
        }
    }
}
