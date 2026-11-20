package com.Tahpesa8.Curqer.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tahpesa8.Curqer.core.di.ServiceLocator
import com.Tahpesa8.Curqer.core.navigation.Screen
import com.Tahpesa8.Curqer.core.theme.BackgroundBase
import com.Tahpesa8.Curqer.presentation.collection.CollectionScreen
import com.Tahpesa8.Curqer.presentation.daily.DailyChallengeScreen
import com.Tahpesa8.Curqer.presentation.game.GameScreen
import com.Tahpesa8.Curqer.presentation.gameover.GameOverScreen
import com.Tahpesa8.Curqer.presentation.menu.MenuScreen
import com.Tahpesa8.Curqer.presentation.puzzlemap.PuzzleMapScreen
import com.Tahpesa8.Curqer.presentation.recipe.RecipeBookScreen
import com.Tahpesa8.Curqer.presentation.rewards.RewardsScreen
import com.Tahpesa8.Curqer.presentation.settings.SettingsScreen
import com.Tahpesa8.Curqer.presentation.splash.SplashScreen
import com.Tahpesa8.Curqer.presentation.tutorial.TutorialScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()

    BackHandler(enabled = screen != Screen.Splash && screen != Screen.Menu) {
        appViewModel.back()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBase)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                when (targetState) {
                    is Screen.Game ->
                        (fadeIn(tween(360)) + slideInVertically { full -> full / 8 }) togetherWith
                            fadeOut(tween(260))

                    is Screen.GameOver ->
                        (fadeIn(tween(400)) + scaleIn(initialScale = 0.94f)) togetherWith
                            fadeOut(tween(280))

                    else -> fadeIn(tween(420)) togetherWith fadeOut(tween(320))
                }
            },
            label = "screenTransition"
        ) { target ->
            when (target) {
                is Screen.Splash -> SplashScreen(onFinished = { appViewModel.openMenu() })

                is Screen.Menu -> MenuScreen(
                    onPlay = { stage -> appViewModel.playStage(stage) },
                    onRoute = { route -> appViewModel.navigate(route) }
                )

                is Screen.Game -> GameScreen(
                    stage = target.stage,
                    daily = target.daily,
                    token = target.token,
                    onFinished = { result -> appViewModel.finishRound(result) },
                    onMenu = { appViewModel.backToMenu() }
                )

                is Screen.GameOver -> GameOverScreen(
                    result = target,
                    onPlayAgain = { appViewModel.replayStage(target.stage, target.daily) },
                    onPuzzleMap = { appViewModel.openRoute(Screen.PuzzleMap) },
                    onMenu = { appViewModel.backToMenu() }
                )

                is Screen.PuzzleMap -> PuzzleMapScreen(
                    onPlayStage = { stage -> appViewModel.playStage(stage) },
                    onMenu = { appViewModel.backToMenu() }
                )

                is Screen.Tutorial -> TutorialScreen(onMenu = { appViewModel.backToMenu() })

                is Screen.DailyChallenge -> DailyChallengeScreen(
                    onPlayDaily = { stage -> appViewModel.playStage(stage, daily = true) },
                    onMenu = { appViewModel.backToMenu() }
                )

                is Screen.RecipeBook -> RecipeBookScreen(onMenu = { appViewModel.backToMenu() })

                is Screen.Collection -> CollectionScreen(onMenu = { appViewModel.backToMenu() })

                is Screen.Rewards -> RewardsScreen(onMenu = { appViewModel.backToMenu() })

                is Screen.Settings -> SettingsScreen(onMenu = { appViewModel.backToMenu() })
            }
        }
    }
}
