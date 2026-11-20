package com.Tahpesa8.Curqer.domain.usecase

import com.Tahpesa8.Curqer.domain.model.CandyType
import com.Tahpesa8.Curqer.domain.model.Objective
import com.Tahpesa8.Curqer.domain.model.ObjectiveKind
import com.Tahpesa8.Curqer.domain.model.Recipe

class AwardStarsUseCase {
    operator fun invoke(won: Boolean, movesLeft: Int): Int = when {
        !won -> 0
        movesLeft >= 3 -> 3
        movesLeft >= 1 -> 2
        else -> 1
    }
}

class EvaluateObjectivesUseCase {
    operator fun invoke(
        objectives: List<Objective>,
        collected: Map<CandyType, Int>,
        syrupCleared: Int
    ): List<Objective> = objectives.map { objective ->
        when (objective.kind) {
            ObjectiveKind.INGREDIENT -> {
                val gained = collected[objective.type] ?: 0
                val next = objective.progress + gained
                objective.copy(progress = if (next > objective.target) objective.target else next)
            }

            ObjectiveKind.SYRUP -> {
                val next = objective.progress + syrupCleared
                objective.copy(progress = if (next > objective.target) objective.target else next)
            }
        }
    }
}

class UnlockRecipesUseCase {
    operator fun invoke(recipes: List<Recipe>, counts: Map<CandyType, Int>): Set<Int> =
        recipes.filter { (counts[it.unlockType] ?: 0) >= it.unlockCount }
            .map { it.id }
            .toSet()
}
