package com.Tahpesa8.Curqer.domain.usecase

import com.Tahpesa8.Curqer.core.config.GameConfig
import com.Tahpesa8.Curqer.domain.model.BoardState
import com.Tahpesa8.Curqer.domain.model.Cell
import com.Tahpesa8.Curqer.domain.model.StageSpec
import kotlin.random.Random

object FallbackBoard {

    private val pattern = intArrayOf(
        0, 1, 2, 0, 1, 2,
        2, 0, 1, 2, 0, 1,
        1, 2, 0, 1, 2, 0,
        0, 1, 3, 0, 4, 2,
        3, 4, 0, 3, 1, 4,
        4, 3, 1, 4, 3, 0
    )

    fun stageSafe(spec: StageSpec): BoardState {
        val cols = GameConfig.COLS
        val rows = GameConfig.ROWS
        val cells = ArrayList<Cell>(rows * cols)
        for (i in 0 until rows * cols) {
            cells.add(Cell(BoardEngine.types[pattern[i] % BoardEngine.types.size]))
        }
        var placed = 0
        var slot = 0
        val inner = mutableListOf<Int>()
        for (r in 1 until rows - 1) {
            for (c in 1 until cols - 1) inner.add(r * cols + c)
        }
        while (placed < spec.syrupCells && slot < inner.size) {
            val idx = inner[slot]
            cells[idx] = cells[idx].copy(syrup = if (placed % 2 == 0) 2 else 1)
            placed += 1
            slot += 2
        }
        return BoardState(cells, cols, rows)
    }
}

class GenerateBoardUseCase {

    operator fun invoke(spec: StageSpec, seed: Long): BoardState {
        val rng = Random(seed)
        var best: BoardState? = null
        repeat(GameConfig.GENERATOR_ATTEMPTS) {
            val candidate = build(spec, rng)
            val clean = BoardEngine.findMatches(candidate).isEmpty()
            val playable = BoardEngine.hasAnyMove(candidate)
            if (clean && playable) return candidate
            if (best == null && playable) best = candidate
        }
        return best ?: FallbackBoard.stageSafe(spec)
    }

    private fun build(spec: StageSpec, rng: Random): BoardState {
        val cols = GameConfig.COLS
        val rows = GameConfig.ROWS
        val cells = ArrayList<Cell>(rows * cols)
        for (i in 0 until rows * cols) cells.add(BoardEngine.randomCell(rng))
        val inner = mutableListOf<Int>()
        for (r in 1 until rows - 1) {
            for (c in 1 until cols - 1) inner.add(r * cols + c)
        }
        inner.shuffle(rng)
        val count = if (spec.syrupCells > inner.size) inner.size else spec.syrupCells
        for (k in 0 until count) {
            val idx = inner[k]
            cells[idx] = cells[idx].copy(syrup = if (k % 2 == 0) 2 else 1)
        }
        return BoardState(cells, cols, rows)
    }
}
