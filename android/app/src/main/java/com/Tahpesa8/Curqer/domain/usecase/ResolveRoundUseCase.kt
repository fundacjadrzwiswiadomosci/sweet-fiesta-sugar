package com.Tahpesa8.Curqer.domain.usecase

import com.Tahpesa8.Curqer.core.config.GameConfig
import com.Tahpesa8.Curqer.domain.model.BoardState
import com.Tahpesa8.Curqer.domain.model.CandyType
import com.Tahpesa8.Curqer.domain.model.ResolveStep
import kotlin.random.Random

class ResolveRoundUseCase {

    fun swap(board: BoardState, a: Int, b: Int, rng: Random): List<ResolveStep> {
        if (!BoardEngine.isLegalSwap(board, a, b)) return emptyList()
        if (board.cells[a].spark || board.cells[b].spark) return spark(board, a, b, rng)
        val moved = BoardEngine.swapped(board, a, b)
        if (BoardEngine.findMatches(moved).isEmpty()) return emptyList()
        return cascade(moved, a, rng, creditChain = true, forcedFirst = null)
    }

    fun pour(board: BoardState, charged: Boolean, rng: Random): List<ResolveStep> {
        if (charged) {
            val bottom = BoardEngine.bottomRowPlayable(board)
            if (bottom.isEmpty()) return emptyList()
            return cascade(board, -1, rng, creditChain = true, forcedFirst = bottom.toSet())
        }
        val syrup = BoardEngine.syrupCells(board)
        if (syrup.isNotEmpty()) {
            val target = syrup[rng.nextInt(syrup.size)]
            val cells = board.cells.toMutableList()
            cells[target] = cells[target].copy(syrup = cells[target].syrup - 1)
            val next = board.copy(cells = cells)
            val first = ResolveStep(
                cleared = setOf(target),
                board = next,
                chain = 0,
                collected = emptyMap(),
                syrupCleared = 1
            )
            return listOf(first) + cascade(next, -1, rng, creditChain = false, forcedFirst = null)
        }
        val bottom = BoardEngine.bottomRowPlayable(board)
        if (bottom.isEmpty()) return emptyList()
        val victim = bottom[rng.nextInt(bottom.size)]
        return cascade(board, -1, rng, creditChain = false, forcedFirst = setOf(victim))
    }

    private fun spark(board: BoardState, a: Int, b: Int, rng: Random): List<ResolveStep> {
        val cellA = board.cells[a]
        val cellB = board.cells[b]
        val wanted = mutableSetOf<CandyType>()
        if (cellA.spark && cellB.spark) {
            val counts = mutableMapOf<CandyType, Int>()
            for (cell in board.cells) {
                if (BoardEngine.playable(cell)) counts[cell.type] = (counts[cell.type] ?: 0) + 1
            }
            counts.entries.sortedByDescending { it.value }.take(2).forEach { wanted.add(it.key) }
        } else if (cellA.spark) {
            wanted.add(cellB.type)
        } else {
            wanted.add(cellA.type)
        }
        val cleared = mutableSetOf(a, b)
        for (i in board.cells.indices) {
            val cell = board.cells[i]
            if (BoardEngine.playable(cell) && wanted.contains(cell.type)) cleared.add(i)
        }
        return cascade(board, -1, rng, creditChain = true, forcedFirst = cleared)
    }

    private fun cascade(
        start: BoardState,
        origin: Int,
        rng: Random,
        creditChain: Boolean,
        forcedFirst: Set<Int>?
    ): List<ResolveStep> {
        val steps = mutableListOf<ResolveStep>()
        var current = start
        var chain = 0
        var pending = forcedFirst ?: BoardEngine.findMatches(current)
        while (pending.isNotEmpty() && steps.size < 24) {
            chain += 1
            var cleared = pending
            if (chain == 1 && forcedFirst == null) {
                val longRun = BoardEngine.matchRuns(current).firstOrNull { it.size >= GameConfig.SPARK_RUN }
                if (longRun != null) {
                    val sparkIndex = if (longRun.contains(origin)) origin else longRun[longRun.size / 2]
                    cleared = cleared - sparkIndex
                    val cells = current.cells.toMutableList()
                    cells[sparkIndex] = cells[sparkIndex].copy(spark = true)
                    current = current.copy(cells = cells)
                }
            }
            val settled = BoardEngine.settle(current, cleared, rng)
            current = settled.board
            steps.add(
                ResolveStep(
                    cleared = cleared,
                    board = current,
                    chain = if (creditChain) chain else 0,
                    collected = settled.collected,
                    syrupCleared = settled.syrupCleared
                )
            )
            pending = BoardEngine.findMatches(current)
        }
        return steps
    }
}
