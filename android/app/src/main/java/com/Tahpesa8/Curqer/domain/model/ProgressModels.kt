package com.Tahpesa8.Curqer.domain.model

data class Recipe(
    val id: Int,
    val nameRes: Int,
    val ingredients: List<Pair<CandyType, Int>>,
    val unlockType: CandyType,
    val unlockCount: Int
)

data class DecorPiece(
    val id: Int,
    val nameRes: Int,
    val cost: Int,
    val fromMilestone: Boolean,
    val spriteSlot: Int
)

data class RewardMilestone(
    val stars: Int,
    val decorId: Int
)

data class PlayerProgress(
    val lifetimeStars: Int = 0,
    val spentStars: Int = 0,
    val stageStars: Map<Int, Int> = emptyMap(),
    val ingredientCounts: Map<CandyType, Int> = emptyMap(),
    val unlockedDecor: Set<Int> = emptySet(),
    val displayedDecor: Set<Int> = emptySet(),
    val claimedMilestones: Set<Int> = emptySet(),
    val bestChain: Int = 0,
    val dailyLastDay: Long = 0L,
    val dailyStreak: Int = 0,
    val dailyDays: Set<Long> = emptySet()
) {
    val spendableStars: Int get() = lifetimeStars - spentStars

    val clearedStages: Int get() = stageStars.count { it.value > 0 }

    fun nextStage(total: Int): Int {
        var candidate = 1
        while (candidate <= total && (stageStars[candidate] ?: 0) > 0) {
            candidate += 1
        }
        return if (candidate > total) total else candidate
    }

    fun unlockedGardens(perGarden: Int, gardens: Int): Int {
        var open = 1
        while (open < gardens) {
            val first = (open - 1) * perGarden + 1
            val last = open * perGarden
            var all = true
            for (s in first..last) {
                if ((stageStars[s] ?: 0) <= 0) all = false
            }
            if (!all) break
            open += 1
        }
        return open
    }
}

data class GameSettings(
    val highContrast: Boolean = false,
    val reducedMotion: Boolean = false,
    val timedChallenge: Boolean = false,
    val largerText: Boolean = false
)
