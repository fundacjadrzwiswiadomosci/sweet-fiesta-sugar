package com.Tahpesa8.Curqer.domain.model

enum class CandyType { STRAWBERRY, GRAPE, LEMON, BLUEBERRY, MELON }

data class Cell(
    val type: CandyType,
    val syrup: Int = 0,
    val spark: Boolean = false
)

data class BoardState(
    val cells: List<Cell>,
    val cols: Int,
    val rows: Int
) {
    fun at(row: Int, col: Int): Cell = cells[row * cols + col]
    fun indexOf(row: Int, col: Int): Int = row * cols + col
    fun rowOf(index: Int): Int = index / cols
    fun colOf(index: Int): Int = index % cols
}

enum class ObjectiveKind { INGREDIENT, SYRUP }

data class Objective(
    val kind: ObjectiveKind,
    val type: CandyType?,
    val target: Int,
    val progress: Int = 0
) {
    val done: Boolean get() = progress >= target
}

data class StageSpec(
    val index: Int,
    val garden: Int,
    val moves: Int,
    val syrupCells: Int,
    val objectives: List<Objective>
)

data class ResolveStep(
    val cleared: Set<Int>,
    val board: BoardState,
    val chain: Int,
    val collected: Map<CandyType, Int>,
    val syrupCleared: Int
)

data class RoundOutcome(
    val stage: Int,
    val daily: Boolean,
    val won: Boolean,
    val outOfTime: Boolean,
    val movesLeft: Int,
    val bestChain: Int,
    val stars: Int,
    val collected: List<Int>,
    val syrupCleared: Int,
    val unlockedRecipeId: Int,
    val unlockedDecorId: Int
)
