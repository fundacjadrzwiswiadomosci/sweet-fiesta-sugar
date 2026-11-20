package com.Tahpesa8.Curqer.core.di

import android.content.Context
import com.Tahpesa8.Curqer.data.local.SweetPrefs
import com.Tahpesa8.Curqer.data.repository.ProgressRepositoryImpl
import com.Tahpesa8.Curqer.data.repository.SettingsRepositoryImpl
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository
import com.Tahpesa8.Curqer.domain.repository.SettingsRepository

object ServiceLocator {

    private var progressRepository: ProgressRepository? = null
    private var settingsRepository: SettingsRepository? = null
    private var cachedFactory: AppViewModelFactory? = null

    fun init(context: Context) {
        if (cachedFactory != null) return
        val prefs = SweetPrefs(context.applicationContext)
        val progress = ProgressRepositoryImpl(prefs)
        val settings = SettingsRepositoryImpl(prefs)
        progressRepository = progress
        settingsRepository = settings
        cachedFactory = AppViewModelFactory(progress, settings)
    }

    val factory: AppViewModelFactory
        get() = cachedFactory ?: throw IllegalStateException("ServiceLocator not initialised")
}
