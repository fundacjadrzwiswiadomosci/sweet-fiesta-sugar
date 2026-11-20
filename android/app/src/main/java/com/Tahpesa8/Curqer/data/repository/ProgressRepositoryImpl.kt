package com.Tahpesa8.Curqer.data.repository

import com.Tahpesa8.Curqer.data.local.SweetPrefs
import com.Tahpesa8.Curqer.domain.model.CandyType
import com.Tahpesa8.Curqer.domain.model.PlayerProgress
import com.Tahpesa8.Curqer.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val prefs: SweetPrefs) : ProgressRepository {

    override fun load(): PlayerProgress = PlayerProgress(
        lifetimeStars = prefs.getInt(SweetPrefs.KEY_LIFETIME_STARS, 0),
        spentStars = prefs.getInt(SweetPrefs.KEY_SPENT_STARS, 0),
        stageStars = decodeIntMap(prefs.getString(SweetPrefs.KEY_STAGE_STARS)),
        ingredientCounts = decodeCandyMap(prefs.getString(SweetPrefs.KEY_INGREDIENTS)),
        unlockedDecor = decodeIntSet(prefs.getString(SweetPrefs.KEY_UNLOCKED_DECOR)),
        displayedDecor = decodeIntSet(prefs.getString(SweetPrefs.KEY_DISPLAYED_DECOR)),
        claimedMilestones = decodeIntSet(prefs.getString(SweetPrefs.KEY_CLAIMED)),
        bestChain = prefs.getInt(SweetPrefs.KEY_BEST_CHAIN, 0),
        dailyLastDay = prefs.getLong(SweetPrefs.KEY_DAILY_LAST, 0L),
        dailyStreak = prefs.getInt(SweetPrefs.KEY_DAILY_STREAK, 0),
        dailyDays = decodeLongSet(prefs.getString(SweetPrefs.KEY_DAILY_DAYS))
    )

    override fun save(progress: PlayerProgress) {
        prefs.putInt(SweetPrefs.KEY_LIFETIME_STARS, progress.lifetimeStars)
        prefs.putInt(SweetPrefs.KEY_SPENT_STARS, progress.spentStars)
        prefs.putString(SweetPrefs.KEY_STAGE_STARS, encodeIntMap(progress.stageStars))
        prefs.putString(SweetPrefs.KEY_INGREDIENTS, encodeCandyMap(progress.ingredientCounts))
        prefs.putString(SweetPrefs.KEY_UNLOCKED_DECOR, encodeInts(progress.unlockedDecor))
        prefs.putString(SweetPrefs.KEY_DISPLAYED_DECOR, encodeInts(progress.displayedDecor))
        prefs.putString(SweetPrefs.KEY_CLAIMED, encodeInts(progress.claimedMilestones))
        prefs.putInt(SweetPrefs.KEY_BEST_CHAIN, progress.bestChain)
        prefs.putLong(SweetPrefs.KEY_DAILY_LAST, progress.dailyLastDay)
        prefs.putInt(SweetPrefs.KEY_DAILY_STREAK, progress.dailyStreak)
        prefs.putString(SweetPrefs.KEY_DAILY_DAYS, encodeLongs(progress.dailyDays))
    }

    override fun clear() {
        prefs.wipe(
            listOf(
                SweetPrefs.KEY_LIFETIME_STARS,
                SweetPrefs.KEY_SPENT_STARS,
                SweetPrefs.KEY_STAGE_STARS,
                SweetPrefs.KEY_INGREDIENTS,
                SweetPrefs.KEY_UNLOCKED_DECOR,
                SweetPrefs.KEY_DISPLAYED_DECOR,
                SweetPrefs.KEY_CLAIMED,
                SweetPrefs.KEY_BEST_CHAIN,
                SweetPrefs.KEY_DAILY_LAST,
                SweetPrefs.KEY_DAILY_STREAK,
                SweetPrefs.KEY_DAILY_DAYS
            )
        )
    }

    private fun encodeInts(values: Set<Int>): String = values.sorted().joinToString(",")

    private fun encodeLongs(values: Set<Long>): String = values.sorted().joinToString(",")

    private fun decodeIntSet(raw: String): Set<Int> =
        raw.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()

    private fun decodeLongSet(raw: String): Set<Long> =
        raw.split(",").mapNotNull { it.trim().toLongOrNull() }.toSet()

    private fun encodeIntMap(values: Map<Int, Int>): String =
        values.entries.sortedBy { it.key }.joinToString(",") { "${it.key}:${it.value}" }

    private fun decodeIntMap(raw: String): Map<Int, Int> {
        val out = mutableMapOf<Int, Int>()
        for (part in raw.split(",")) {
            val pair = part.split(":")
            if (pair.size != 2) continue
            val key = pair[0].trim().toIntOrNull() ?: continue
            val value = pair[1].trim().toIntOrNull() ?: continue
            out[key] = value
        }
        return out
    }

    private fun encodeCandyMap(values: Map<CandyType, Int>): String =
        values.entries.joinToString(",") { "${it.key.ordinal}:${it.value}" }

    private fun decodeCandyMap(raw: String): Map<CandyType, Int> {
        val out = mutableMapOf<CandyType, Int>()
        val all = CandyType.entries
        for (part in raw.split(",")) {
            val pair = part.split(":")
            if (pair.size != 2) continue
            val key = pair[0].trim().toIntOrNull() ?: continue
            val value = pair[1].trim().toIntOrNull() ?: continue
            if (key in all.indices) out[all[key]] = value
        }
        return out
    }
}
