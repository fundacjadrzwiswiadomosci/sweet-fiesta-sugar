package com.Tahpesa8.Curqer.data.local

import android.content.Context
import android.content.SharedPreferences

class SweetPrefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sweet_fiesta_sugar", Context.MODE_PRIVATE)

    fun getInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getLong(key: String, fallback: Long): Long = prefs.getLong(key, fallback)

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String): String = prefs.getString(key, "") ?: ""

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun wipe(keys: List<String>) {
        val editor = prefs.edit()
        for (key in keys) editor.remove(key)
        editor.apply()
    }

    companion object {
        const val KEY_LIFETIME_STARS = "lifetime_stars"
        const val KEY_SPENT_STARS = "spent_stars"
        const val KEY_STAGE_STARS = "stage_stars"
        const val KEY_INGREDIENTS = "ingredient_counts"
        const val KEY_UNLOCKED_DECOR = "unlocked_decor"
        const val KEY_DISPLAYED_DECOR = "displayed_decor"
        const val KEY_CLAIMED = "claimed_milestones"
        const val KEY_BEST_CHAIN = "best_chain"
        const val KEY_DAILY_LAST = "daily_last_day"
        const val KEY_DAILY_STREAK = "daily_streak"
        const val KEY_DAILY_DAYS = "daily_days"
        const val KEY_HIGH_CONTRAST = "high_contrast"
        const val KEY_REDUCED_MOTION = "reduced_motion"
        const val KEY_TIMED = "timed_challenge"
        const val KEY_LARGER_TEXT = "larger_text"
    }
}
