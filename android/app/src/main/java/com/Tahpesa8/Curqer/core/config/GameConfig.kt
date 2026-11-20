package com.Tahpesa8.Curqer.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L

    const val COLS = 6
    const val ROWS = 6
    const val BOARD_MAX_DP = 380
    const val BOARD_PAD_DP = 6
    const val BOARD_BORDER_DP = 2
    const val BOARD_FRAME_DP = BOARD_PAD_DP + BOARD_BORDER_DP

    const val MOVES_BASE = 5
    const val MOVES_CAP = 9
    const val SUGAR_PER_CHAIN = 0.12f
    const val TIMED_SECONDS = 90

    const val STAGES_PER_GARDEN = 5
    const val GARDENS = 4
    const val TOTAL_STAGES = STAGES_PER_GARDEN * GARDENS
    const val DAILY_STAR_BONUS = 2

    const val SWAP_ANIM_MS = 180L
    const val CLEAR_ANIM_MS = 220L
    const val CASCADE_STEP_MS = 180L
    const val CHAIN_BADGE_MS = 520L
    const val ROUND_END_DELAY_MS = 600L
    const val POUR_ANIM_MS = 420L

    const val GENERATOR_ATTEMPTS = 20
    const val SPARK_RUN = 5

    fun movesFor(stage: Int): Int {
        val raw = MOVES_BASE + (stage - 1) / 3
        return if (raw > MOVES_CAP) MOVES_CAP else raw
    }

    fun gardenOf(stage: Int): Int = ((stage - 1) / STAGES_PER_GARDEN) + 1

    fun syrupFor(stage: Int): Int {
        val raw = 2 + stage / 4
        return if (raw > 6) 6 else raw
    }
}
