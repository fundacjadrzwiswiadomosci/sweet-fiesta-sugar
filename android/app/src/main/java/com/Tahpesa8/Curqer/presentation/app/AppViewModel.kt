package com.Tahpesa8.Curqer.presentation.app

import androidx.lifecycle.ViewModel
import com.Tahpesa8.Curqer.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val backStack = mutableListOf<Screen>()
    private var roundToken = 0L

    fun openMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
    }

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) backStack.add(current)
        _screen.value = target
    }

    fun openRoute(target: Screen) {
        backStack.clear()
        backStack.add(Screen.Menu)
        _screen.value = target
    }

    fun playStage(stage: Int, daily: Boolean = false) {
        roundToken += 1
        navigate(Screen.Game(stage, daily, roundToken))
    }

    fun replayStage(stage: Int, daily: Boolean) {
        roundToken += 1
        _screen.value = Screen.Game(stage, daily, roundToken)
    }

    fun finishRound(result: Screen.GameOver) {
        _screen.value = result
    }

    fun back() {
        val previous = backStack.removeLastOrNull()
        _screen.value = previous ?: Screen.Menu
    }

    fun backToMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
    }
}
