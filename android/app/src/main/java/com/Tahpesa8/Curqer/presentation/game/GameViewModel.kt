package com.Tahpesa8.Curqer.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tahpesa8.Curqer.core.config.GameConfig
import com.Tahpesa8.Curqer.data.sample.SampleData
import com.Tahpesa8.Curqer.domain.model.BoardState
import com.Tahpesa8.Curqer.domain.model.CandyType
import com.Tahpesa8.Curqer.domain.model.GameSettings
import com.Tahpesa8.Curqer.domain.model.Objective
import com.Tahpesa8.Curqer.domain.model.ResolveStep
import com.Tahpesa8.Curqer.domain.model.RoundOutcome
import com.Tahpesa8.Curqer.domain.model.StageSpec
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import com.Tahpesa8.Curqer.domain.repository.SettingsRepository
import com.Tahpesa8.Curqer.domain.usecase.AwardStarsUseCase
import com.Tahpesa8.Curqer.domain.usecase.BoardEngine
import com.Tahpesa8.Curqer.domain.usecase.EvaluateObjectivesUseCase
import com.Tahpesa8.Curqer.domain.usecase.GenerateBoardUseCase
import com.Tahpesa8.Curqer.domain.usecase.ResolveRoundUseCase
import com.Tahpesa8.Curqer.domain.usecase.UnlockRecipesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

data class GameUiState(
    val stage: Int = 1,
    val garden: Int = 1,
    val daily: Boolean = false,
    val board: BoardState = BoardState(emptyList(), GameConfig.COLS, GameConfig.ROWS),
    val movesLeft: Int = 0,
    val objectives: List<Objective> = emptyList(),
    val chain: Int = 0,
    val showChain: Boolean = false,
    val sugar: Float = 0f,
    val busy: Boolean = true,
    val paused: Boolean = false,
    val clearing: Set<Int> = emptySet(),
    val timeLeft: Int = -1,
    val settings: GameSettings = GameSettings(),
    val ready: Boolean = false
)

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _outcome = Channel<RoundOutcome>(Channel.BUFFERED)
    val outcome: Flow<RoundOutcome> = _outcome.receiveAsFlow()

    private val generateBoard = GenerateBoardUseCase()
    private val resolveRound = ResolveRoundUseCase()
    private val evaluateObjectives = EvaluateObjectivesUseCase()
    private val awardStars = AwardStarsUseCase()
    private val unlockRecipes = UnlockRecipesUseCase()

    private var rng = Random(1L)
    private var activeToken = -1L
    private var spec: StageSpec = SampleData.stage(1)
    private var bestChain = 0
    private var collected = mutableMapOf<CandyType, Int>()
    private var syrupTotal = 0
    private var resolveJob: Job? = null
    private var timerJob: Job? = null
    private var roundClosed = false

    fun start(stage: Int, daily: Boolean, token: Long) {
        if (token == activeToken) return
        activeToken = token
        roundClosed = false
        bestChain = 0
        syrupTotal = 0
        collected = mutableMapOf()
        resolveJob?.cancel()
        timerJob?.cancel()
        viewModelScope.launch {
            val settings = settingsRepository.load()
            val epochDay = LocalDate.now().toEpochDay()
            spec = if (daily) SampleData.dailySpec(epochDay) else SampleData.stage(stage)
            val seed = if (daily) {
                epochDay * 2654435761L
            } else {
                spec.index * 7919L xor (token * 104729L)
            }
            rng = Random(seed)
            val board = generateBoard(spec, seed)
            _state.update {
                GameUiState(
                    stage = spec.index,
                    garden = spec.garden,
                    daily = daily,
                    board = board,
                    movesLeft = spec.moves,
                    objectives = spec.objectives,
                    sugar = 0f,
                    busy = false,
                    ready = true,
                    settings = settings,
                    timeLeft = if (settings.timedChallenge) GameConfig.TIMED_SECONDS else -1
                )
            }
            if (settings.timedChallenge) runTimer()
        }
    }

    fun onSwap(from: Int, to: Int) {
        val current = _state.value
        if (current.busy || current.paused || roundClosed || !current.ready) return
        val steps = resolveRound.swap(current.board, from, to, rng)
        if (steps.isEmpty()) return
        _state.update { it.copy(movesLeft = it.movesLeft - 1) }
        playSteps(steps)
    }

    fun onDropSugar() {
        val current = _state.value
        if (current.busy || current.paused || roundClosed || !current.ready) return
        val charged = current.sugar >= 1f
        val steps = resolveRound.pour(current.board, charged, rng)
        _state.update {
            it.copy(
                movesLeft = it.movesLeft - 1,
                sugar = if (charged) 0f else it.sugar
            )
        }
        if (steps.isEmpty()) {
            checkEnd()
            return
        }
        playSteps(steps)
    }

    fun openPause() {
        if (roundClosed) return
        timerJob?.cancel()
        _state.update { it.copy(paused = true) }
    }

    fun closePause() {
        val current = _state.value
        if (!current.paused) return
        _state.update { it.copy(paused = false) }
        if (current.settings.timedChallenge && current.timeLeft > 0) runTimer()
    }

    fun pause() {
        timerJob?.cancel()
        resolveJob?.cancel()
    }

    private fun runTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val current = _state.value
                if (current.paused || roundClosed) break
                val next = current.timeLeft - 1
                _state.update { it.copy(timeLeft = next) }
                if (next <= 0) {
                    finishRound(won = false, outOfTime = true)
                    break
                }
            }
        }
    }

    private fun playSteps(steps: List<ResolveStep>) {
        resolveJob?.cancel()
        resolveJob = viewModelScope.launch {
            _state.update { it.copy(busy = true) }
            for (step in steps) {
                if (step.cleared.isNotEmpty()) {
                    _state.update {
                        it.copy(
                            clearing = step.cleared,
                            chain = step.chain,
                            showChain = step.chain >= 2
                        )
                    }
                    delay(if (_state.value.settings.reducedMotion) 90L else GameConfig.CLEAR_ANIM_MS)
                }
                applyStep(step)
                delay(if (_state.value.settings.reducedMotion) 60L else GameConfig.CASCADE_STEP_MS)
            }
            _state.update { it.copy(clearing = emptySet(), showChain = false, busy = false) }
            checkEnd()
        }
    }

    private fun applyStep(step: ResolveStep) {
        for (entry in step.collected) {
            collected[entry.key] = (collected[entry.key] ?: 0) + entry.value
        }
        syrupTotal += step.syrupCleared
        if (step.chain > bestChain) bestChain = step.chain
        _state.update { current ->
            current.copy(
                board = step.board,
                clearing = emptySet(),
                objectives = evaluateObjectives(current.objectives, step.collected, step.syrupCleared),
                sugar = (current.sugar + step.chain * GameConfig.SUGAR_PER_CHAIN).coerceIn(0f, 1f)
            )
        }
    }

    private fun checkEnd() {
        if (roundClosed) return
        val current = _state.value
        if (current.objectives.all { it.done }) {
            finishRound(won = true, outOfTime = false)
            return
        }
        if (current.movesLeft <= 0) {
            viewModelScope.launch {
                delay(GameConfig.ROUND_END_DELAY_MS)
                finishRound(won = false, outOfTime = false)
            }
            return
        }
        if (!BoardEngine.hasAnyMove(current.board)) {
            val reshuffled = generateBoard(spec, rng.nextLong())
            _state.update { it.copy(board = reshuffled) }
        }
    }

    private fun finishRound(won: Boolean, outOfTime: Boolean) {
        if (roundClosed) return
        roundClosed = true
        timerJob?.cancel()
        val current = _state.value
        val stars = awardStars(won, current.movesLeft)
        viewModelScope.launch {
            val before = progressRepository.load()
            val beforeRecipes = unlockRecipes(SampleData.recipes, before.ingredientCounts)
            val mergedCounts = before.ingredientCounts.toMutableMap()
            for (entry in collected) {
                mergedCounts[entry.key] = (mergedCounts[entry.key] ?: 0) + entry.value
            }
            val afterRecipes = unlockRecipes(SampleData.recipes, mergedCounts)
            val newRecipe = (afterRecipes - beforeRecipes).minOrNull() ?: 0
            val today = LocalDate.now().toEpochDay()
            val dailyWin = won && current.daily
            val bonus = if (dailyWin) GameConfig.DAILY_STAR_BONUS else 0
            val stageStars = before.stageStars.toMutableMap()
            if (won && !current.daily) {
                val old = stageStars[current.stage] ?: 0
                if (stars > old) stageStars[current.stage] = stars
            }
            val gainedStars = if (current.daily) bonus else stars
            val totalStars = before.lifetimeStars + gainedStars
            val newMilestone = SampleData.milestones.firstOrNull {
                it.stars in (before.lifetimeStars + 1)..totalStars
            }
            val streak = when {
                !dailyWin -> before.dailyStreak
                before.dailyLastDay == today - 1L -> before.dailyStreak + 1
                else -> 1
            }
            val updated = before.copy(
                lifetimeStars = totalStars,
                stageStars = stageStars,
                ingredientCounts = mergedCounts,
                bestChain = if (bestChain > before.bestChain) bestChain else before.bestChain,
                dailyLastDay = if (dailyWin) today else before.dailyLastDay,
                dailyStreak = streak,
                dailyDays = if (dailyWin) before.dailyDays + today else before.dailyDays
            )
            progressRepository.save(updated)
            _outcome.send(
                RoundOutcome(
                    stage = current.stage,
                    daily = current.daily,
                    won = won,
                    outOfTime = outOfTime,
                    movesLeft = current.movesLeft,
                    bestChain = bestChain,
                    stars = if (current.daily) bonus else stars,
                    collected = CandyType.entries.map { collected[it] ?: 0 },
                    syrupCleared = syrupTotal,
                    unlockedRecipeId = newRecipe,
                    unlockedDecorId = newMilestone?.decorId ?: 0
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        resolveJob?.cancel()
    }
}
