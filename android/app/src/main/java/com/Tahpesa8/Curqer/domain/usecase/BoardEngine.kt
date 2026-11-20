package com.Tahpesa8.Curqer.domain.usecase

import com.Tahpesa8.Curqer.domain.model.BoardState
import com.Tahpesa8.Curqer.domain.model.CandyType
import com.Tahpesa8.Curqer.domain.model.Cell
import kotlin.random.Random

data class SettleResult(
    val board: BoardState,
    val collected: Map<CandyType, Int>,
    val syrupCleared: Int
)

object BoardEngine {

    val types: List<CandyType> = CandyType.entries.toList()

    fun randomCell(rng: Random): Cell = Cell(types[rng.nextInt(types.size)])

    fun playable(cell: Cell): Boolean = cell.syrup == 0

    fun matchRuns(board: BoardState): List<List<Int>> {
        val runs = mutableListOf<List<Int>>()
        val cols = board.cols
        val rows = board.rows
        for (r in 0 until rows) {
            var start = 0
            while (start < cols) {
                val first = board.cells[r * cols + start]
                if (!playable(first)) {
                    start += 1
                    continue
                }
                var end = start + 1
                while (end < cols) {
                    val next = board.cells[r * cols + end]
                    if (!playable(next) || next.type != first.type) break
                    end += 1
                }
                if (end - start >= 3) runs.add((start until end).map { r * cols + it })
                start = if (end > start) end else start + 1
            }
        }
        for (c in 0 until cols) {
            var start = 0
            while (start < rows) {
                val first = board.cells[start * cols + c]
                if (!playable(first)) {
                    start += 1
                    continue
                }
                var end = start + 1
                while (end < rows) {
                    val next = board.cells[end * cols + c]
                    if (!playable(next) || next.type != first.type) break
                    end += 1
                }
                if (end - start >= 3) runs.add((start until end).map { it * cols + c })
                start = if (end > start) end else start + 1
            }
        }
        return runs
    }

    fun findMatches(board: BoardState): Set<Int> {
        val all = mutableSetOf<Int>()
        for (run in matchRuns(board)) all.addAll(run)
        return all
    }

    fun neighbours(board: BoardState, index: Int): List<Int> {
        val r = index / board.cols
        val c = index % board.cols
        val out = mutableListOf<Int>()
        if (r > 0) out.add(index - board.cols)
        if (r < board.rows - 1) out.add(index + board.cols)
        if (c > 0) out.add(index - 1)
        if (c < board.cols - 1) out.add(index + 1)
        return out
    }

    fun swapped(board: BoardState, a: Int, b: Int): BoardState {
        val cells = board.cells.toMutableList()
        val tmp = cells[a]
        cells[a] = cells[b]
        cells[b] = tmp
        return board.copy(cells = cells)
    }

    fun isLegalSwap(board: BoardState, a: Int, b: Int): Boolean {
        if (a == b) return false
        if (!neighbours(board, a).contains(b)) return false
        return playable(board.cells[a]) && playable(board.cells[b])
    }

    fun swapMatches(board: BoardState, a: Int, b: Int): Set<Int> {
        if (!isLegalSwap(board, a, b)) return emptySet()
        if (board.cells[a].spark || board.cells[b].spark) return setOf(a, b)
        return findMatches(swapped(board, a, b))
    }

    fun hasAnyMove(board: BoardState): Boolean {
        for (i in board.cells.indices) {
            if (board.cells[i].spark && playable(board.cells[i])) return true
            val r = i / board.cols
            val c = i % board.cols
            if (c < board.cols - 1 && swapMatches(board, i, i + 1).isNotEmpty()) return true
            if (r < board.rows - 1 && swapMatches(board, i, i + board.cols).isNotEmpty()) return true
        }
        return false
    }

    fun settle(board: BoardState, cleared: Set<Int>, rng: Random): SettleResult {
        val cols = board.cols
        val rows = board.rows
        val work = board.cells.toMutableList()
        val collected = mutableMapOf<CandyType, Int>()
        for (i in cleared) {
            val cell = work[i]
            collected[cell.type] = (collected[cell.type] ?: 0) + 1
        }
        val melt = mutableSetOf<Int>()
        for (i in cleared) {
            for (n in neighbours(board, i)) {
                if (work[n].syrup > 0) melt.add(n)
            }
        }
        var syrupCleared = 0
        for (n in melt) {
            work[n] = work[n].copy(syrup = work[n].syrup - 1)
            syrupCleared += 1
        }
        val kept = arrayOfNulls<Cell>(rows * cols)
        for (i in work.indices) {
            if (!cleared.contains(i)) kept[i] = work[i]
        }
        val out = arrayOfNulls<Cell>(rows * cols)
        for (c in 0 until cols) {
            var write = rows - 1
            var read = rows - 1
            while (read >= 0) {
                val idx = read * cols + c
                val cell = kept[idx]
                if (cell != null && cell.syrup > 0) {
                    var fill = write
                    while (fill > read) {
                        out[fill * cols + c] = randomCell(rng)
                        fill -= 1
                    }
                    out[idx] = cell
                    write = read - 1
                } else if (cell != null) {
                    out[write * cols + c] = cell
                    write -= 1
                }
                read -= 1
            }
            var fill = write
            while (fill >= 0) {
                out[fill * cols + c] = randomCell(rng)
                fill -= 1
            }
        }
        val settled = BoardState(out.map { it ?: randomCell(rng) }, cols, rows)
        return SettleResult(settled, collected, syrupCleared)
    }

    fun bottomRowPlayable(board: BoardState): List<Int> {
        val row = board.rows - 1
        return (0 until board.cols)
            .map { row * board.cols + it }
            .filter { playable(board.cells[it]) }
    }

    fun syrupCells(board: BoardState): List<Int> =
        board.cells.indices.filter { board.cells[it].syrup > 0 }
}
