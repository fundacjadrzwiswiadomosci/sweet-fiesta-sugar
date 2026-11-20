package com.Tahpesa8.Curqer.domain.repository

import com.Tahpesa8.Curqer.domain.model.GameSettings
import com.Tahpesa8.Curqer.domain.model.PlayerProgress

interface ProgressRepository {
    fun load(): PlayerProgress
    fun save(progress: PlayerProgress)
    fun clear()
}

interface SettingsRepository {
    fun load(): GameSettings
    fun save(settings: GameSettings)
}
