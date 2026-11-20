package com.Tahpesa8.Curqer.core.ui

import androidx.compose.ui.graphics.Color
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.theme.BerryRed
import com.Tahpesa8.Curqer.core.theme.BlueberryBlue
import com.Tahpesa8.Curqer.core.theme.GrapePurple
import com.Tahpesa8.Curqer.core.theme.MelonGreen
import com.Tahpesa8.Curqer.core.theme.PuffBlueberry
import com.Tahpesa8.Curqer.core.theme.PuffGrape
import com.Tahpesa8.Curqer.core.theme.PuffLemon
import com.Tahpesa8.Curqer.core.theme.PuffMelon
import com.Tahpesa8.Curqer.core.theme.PuffStrawberry
import com.Tahpesa8.Curqer.core.theme.StarGold
import com.Tahpesa8.Curqer.domain.model.CandyType

object CandyVisuals {

    val sprites = listOf(
        R.drawable.sprite_1,
        R.drawable.sprite_2,
        R.drawable.sprite_3,
        R.drawable.sprite_4,
        R.drawable.sprite_5
    )

    fun sprite(type: CandyType): Int = sprites[type.ordinal]

    fun spriteSlot(slot: Int): Int = sprites[slot % sprites.size]

    fun puff(type: CandyType): Color = when (type) {
        CandyType.STRAWBERRY -> PuffStrawberry
        CandyType.GRAPE -> PuffGrape
        CandyType.LEMON -> PuffLemon
        CandyType.BLUEBERRY -> PuffBlueberry
        CandyType.MELON -> PuffMelon
    }

    fun accent(type: CandyType): Color = when (type) {
        CandyType.STRAWBERRY -> BerryRed
        CandyType.GRAPE -> GrapePurple
        CandyType.LEMON -> StarGold
        CandyType.BLUEBERRY -> BlueberryBlue
        CandyType.MELON -> MelonGreen
    }

    fun marker(type: CandyType): Int = type.ordinal
}
