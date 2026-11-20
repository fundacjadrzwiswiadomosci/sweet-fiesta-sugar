package com.Tahpesa8.Curqer.data.sample

import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.config.GameConfig
import com.Tahpesa8.Curqer.domain.model.CandyType
import com.Tahpesa8.Curqer.domain.model.DecorPiece
import com.Tahpesa8.Curqer.domain.model.Objective
import com.Tahpesa8.Curqer.domain.model.ObjectiveKind
import com.Tahpesa8.Curqer.domain.model.Recipe
import com.Tahpesa8.Curqer.domain.model.RewardMilestone
import com.Tahpesa8.Curqer.domain.model.StageSpec

object SampleData {

    val gardenNames = listOf(
        R.string.garden_1,
        R.string.garden_2,
        R.string.garden_3,
        R.string.garden_4
    )

    val candyNames = mapOf(
        CandyType.STRAWBERRY to R.string.candy_strawberry,
        CandyType.GRAPE to R.string.candy_grape,
        CandyType.LEMON to R.string.candy_lemon,
        CandyType.BLUEBERRY to R.string.candy_blueberry,
        CandyType.MELON to R.string.candy_melon
    )

    val candyPlurals = mapOf(
        CandyType.STRAWBERRY to R.string.candy_strawberries,
        CandyType.GRAPE to R.string.candy_grapes,
        CandyType.LEMON to R.string.candy_lemons,
        CandyType.BLUEBERRY to R.string.candy_blueberries,
        CandyType.MELON to R.string.candy_melons
    )

    val stages: List<StageSpec> = (1..GameConfig.TOTAL_STAGES).map { stage ->
        val primary = CandyType.entries[(stage - 1) % CandyType.entries.size]
        val objectives = mutableListOf(
            Objective(ObjectiveKind.INGREDIENT, primary, 6 + (stage - 1) / 3)
        )
        if (stage >= 8) {
            val secondary = CandyType.entries[(stage + 1) % CandyType.entries.size]
            objectives.add(
                Objective(ObjectiveKind.INGREDIENT, secondary, 4 + (stage - 8) / 4)
            )
        }
        val syrupCells = GameConfig.syrupFor(stage)
        val syrupTarget = minOf(2 + (stage - 1) / 6, syrupCells)
        objectives.add(Objective(ObjectiveKind.SYRUP, null, syrupTarget))
        StageSpec(
            index = stage,
            garden = GameConfig.gardenOf(stage),
            moves = GameConfig.movesFor(stage),
            syrupCells = syrupCells,
            objectives = objectives
        )
    }

    val recipes = listOf(
        Recipe(1, R.string.recipe_1, listOf(CandyType.STRAWBERRY to 6, CandyType.LEMON to 3), CandyType.STRAWBERRY, 15),
        Recipe(2, R.string.recipe_2, listOf(CandyType.GRAPE to 6, CandyType.MELON to 3), CandyType.GRAPE, 15),
        Recipe(3, R.string.recipe_3, listOf(CandyType.LEMON to 7, CandyType.STRAWBERRY to 2), CandyType.LEMON, 15),
        Recipe(4, R.string.recipe_4, listOf(CandyType.BLUEBERRY to 6, CandyType.MELON to 4), CandyType.BLUEBERRY, 15),
        Recipe(5, R.string.recipe_5, listOf(CandyType.MELON to 7, CandyType.LEMON to 3), CandyType.MELON, 15),
        Recipe(6, R.string.recipe_6, listOf(CandyType.STRAWBERRY to 9, CandyType.GRAPE to 4), CandyType.STRAWBERRY, 25),
        Recipe(7, R.string.recipe_7, listOf(CandyType.GRAPE to 9, CandyType.BLUEBERRY to 4), CandyType.GRAPE, 25),
        Recipe(8, R.string.recipe_8, listOf(CandyType.LEMON to 9, CandyType.MELON to 5), CandyType.LEMON, 25),
        Recipe(9, R.string.recipe_9, listOf(CandyType.BLUEBERRY to 10, CandyType.STRAWBERRY to 4), CandyType.BLUEBERRY, 25),
        Recipe(10, R.string.recipe_10, listOf(CandyType.MELON to 11, CandyType.GRAPE to 5), CandyType.MELON, 40),
        Recipe(11, R.string.recipe_11, listOf(CandyType.STRAWBERRY to 12, CandyType.BLUEBERRY to 6), CandyType.STRAWBERRY, 40),
        Recipe(12, R.string.recipe_12, listOf(CandyType.LEMON to 14, CandyType.MELON to 7), CandyType.LEMON, 60)
    )

    val decor = listOf(
        DecorPiece(1, R.string.decor_1, 0, true, 0),
        DecorPiece(2, R.string.decor_2, 0, true, 1),
        DecorPiece(3, R.string.decor_3, 0, true, 2),
        DecorPiece(4, R.string.decor_4, 0, true, 3),
        DecorPiece(5, R.string.decor_5, 0, true, 4),
        DecorPiece(6, R.string.decor_6, 0, true, 0),
        DecorPiece(7, R.string.decor_7, 0, true, 1),
        DecorPiece(8, R.string.decor_8, 0, true, 2),
        DecorPiece(9, R.string.decor_9, 6, false, 3),
        DecorPiece(10, R.string.decor_10, 8, false, 4),
        DecorPiece(11, R.string.decor_11, 10, false, 0),
        DecorPiece(12, R.string.decor_12, 12, false, 1),
        DecorPiece(13, R.string.decor_13, 14, false, 2),
        DecorPiece(14, R.string.decor_14, 16, false, 3),
        DecorPiece(15, R.string.decor_15, 18, false, 4),
        DecorPiece(16, R.string.decor_16, 20, false, 0),
        DecorPiece(17, R.string.decor_17, 22, false, 1),
        DecorPiece(18, R.string.decor_18, 24, false, 2)
    )

    val milestones = listOf(
        RewardMilestone(10, 1),
        RewardMilestone(25, 2),
        RewardMilestone(45, 3),
        RewardMilestone(70, 4),
        RewardMilestone(100, 5),
        RewardMilestone(140, 6),
        RewardMilestone(185, 7),
        RewardMilestone(240, 8)
    )

    val tutorialSteps = listOf(
        R.string.tutorial_1_title to R.string.tutorial_1_body,
        R.string.tutorial_2_title to R.string.tutorial_2_body,
        R.string.tutorial_3_title to R.string.tutorial_3_body,
        R.string.tutorial_4_title to R.string.tutorial_4_body
    )

    fun stage(index: Int): StageSpec {
        val safe = index.coerceIn(1, stages.size)
        return stages[safe - 1]
    }

    fun decorById(id: Int): DecorPiece? = decor.firstOrNull { it.id == id }

    fun recipeById(id: Int): Recipe? = recipes.firstOrNull { it.id == id }

    fun dailySpec(epochDay: Long): StageSpec {
        val base = stage(((epochDay % GameConfig.TOTAL_STAGES).toInt()) + 1)
        return base.copy(moves = GameConfig.MOVES_BASE)
    }
}
