package com.Tahpesa8.Curqer.core.navigation

sealed interface Screen {

    data object Splash : Screen

    data object Menu : Screen

    data class Game(
        val stage: Int,
        val daily: Boolean,
        val token: Long
    ) : Screen

    data class GameOver(
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
    ) : Screen

    data object PuzzleMap : Screen

    data object Tutorial : Screen

    data object DailyChallenge : Screen

    data object RecipeBook : Screen

    data object Collection : Screen

    data object Rewards : Screen

    data object Settings : Screen
}
