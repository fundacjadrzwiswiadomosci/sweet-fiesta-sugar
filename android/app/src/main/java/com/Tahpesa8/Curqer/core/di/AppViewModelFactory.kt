package com.Tahpesa8.Curqer.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import com.Tahpesa8.Curqer.domain.repository.SettingsRepository
import com.Tahpesa8.Curqer.presentation.app.AppViewModel
import com.Tahpesa8.Curqer.presentation.collection.CollectionViewModel
import com.Tahpesa8.Curqer.presentation.daily.DailyChallengeViewModel
import com.Tahpesa8.Curqer.presentation.game.GameViewModel
import com.Tahpesa8.Curqer.presentation.gameover.GameOverViewModel
import com.Tahpesa8.Curqer.presentation.menu.MenuViewModel
import com.Tahpesa8.Curqer.presentation.puzzlemap.PuzzleMapViewModel
import com.Tahpesa8.Curqer.presentation.recipe.RecipeBookViewModel
import com.Tahpesa8.Curqer.presentation.rewards.RewardsViewModel
import com.Tahpesa8.Curqer.presentation.settings.SettingsViewModel
import com.Tahpesa8.Curqer.presentation.splash.SplashViewModel
import com.Tahpesa8.Curqer.presentation.tutorial.TutorialViewModel

class AppViewModelFactory(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel() as T
        modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel() as T
        modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(progress) as T
        modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(progress, settings) as T
        modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(progress) as T
        modelClass.isAssignableFrom(PuzzleMapViewModel::class.java) -> PuzzleMapViewModel(progress) as T
        modelClass.isAssignableFrom(TutorialViewModel::class.java) -> TutorialViewModel() as T
        modelClass.isAssignableFrom(DailyChallengeViewModel::class.java) -> DailyChallengeViewModel(progress) as T
        modelClass.isAssignableFrom(RecipeBookViewModel::class.java) -> RecipeBookViewModel(progress) as T
        modelClass.isAssignableFrom(CollectionViewModel::class.java) -> CollectionViewModel(progress) as T
        modelClass.isAssignableFrom(RewardsViewModel::class.java) -> RewardsViewModel(progress) as T
        modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(progress, settings) as T
        else -> throw IllegalArgumentException(modelClass.name)
    }
}
