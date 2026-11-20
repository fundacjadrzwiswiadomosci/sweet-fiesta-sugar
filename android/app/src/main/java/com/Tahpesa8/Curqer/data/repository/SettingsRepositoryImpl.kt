package com.Tahpesa8.Curqer.data.repository

import com.Tahpesa8.Curqer.data.local.SweetPrefs
import com.Tahpesa8.Curqer.domain.model.GameSettings
import com.Tahpesa8.Curqer.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val prefs: SweetPrefs) : SettingsRepository {

    override fun load(): GameSettings = GameSettings(
        highContrast = prefs.getBoolean(SweetPrefs.KEY_HIGH_CONTRAST, false),
        reducedMotion = prefs.getBoolean(SweetPrefs.KEY_REDUCED_MOTION, false),
        timedChallenge = prefs.getBoolean(SweetPrefs.KEY_TIMED, false),
        largerText = prefs.getBoolean(SweetPrefs.KEY_LARGER_TEXT, false)
    )

    override fun save(settings: GameSettings) {
        prefs.putBoolean(SweetPrefs.KEY_HIGH_CONTRAST, settings.highContrast)
        prefs.putBoolean(SweetPrefs.KEY_REDUCED_MOTION, settings.reducedMotion)
        prefs.putBoolean(SweetPrefs.KEY_TIMED, settings.timedChallenge)
        prefs.putBoolean(SweetPrefs.KEY_LARGER_TEXT, settings.largerText)
    }
}
