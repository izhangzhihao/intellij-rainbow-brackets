package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.colors.impl.AbstractColorsScheme
import java.awt.Color

const val DEFAULT_SCHEME_NAME = "Default"
const val DARCULA_SCHEME_NAME = "Darcula"

fun migrateRainbowColors(settings: RainbowSettings) {
    if (settings.lightRoundBracketsColors.isNullOrEmpty()
            && settings.lightSquareBracketsColors.isNullOrEmpty()
            && settings.lightSquigglyBracketsColors.isNullOrEmpty()
            && settings.lightAngleBracketsColor.isNullOrEmpty()
            && settings.darkRoundBracketsColors.isNullOrEmpty()
            && settings.darkSquareBracketsColors.isNullOrEmpty()
            && settings.darkSquigglyBracketsColors.isNullOrEmpty()
            && settings.darkAngleBracketsColor.isNullOrEmpty()) {
        return
    }

    val round = settings.lightRoundBracketsColors?.map(::color)
    val square = settings.lightSquareBracketsColors?.map(::color)
    val squiggly = settings.lightSquigglyBracketsColors?.map(::color)
    val angle = settings.lightAngleBracketsColor?.map(::color)

    val roundDark = settings.darkRoundBracketsColors?.map(::color)
    val squareDark = settings.darkSquareBracketsColors?.map(::color)
    val squigglyDark = settings.darkSquigglyBracketsColors?.map(::color)
    val angleDark = settings.darkAngleBracketsColor?.map(::color)

    val schemeGroup = EditorColorsManager.getInstance()
            .allSchemes
            .mapNotNull { scheme -> (scheme as? AbstractColorsScheme)?.takeUnless { it.isReadOnly } }
            .groupBy { it.parentScheme?.name }

    schemeGroup[DEFAULT_SCHEME_NAME]?.forEach { scheme ->
        scheme.migrateFrom(round, square, squiggly, angle)
    }
    schemeGroup[DARCULA_SCHEME_NAME]?.forEach { scheme ->
        scheme.migrateFrom(roundDark, squareDark, squigglyDark, angleDark)
    }

    settings.lightRoundBracketsColors = null
    settings.lightSquareBracketsColors = null
    settings.lightSquigglyBracketsColors = null
    settings.lightAngleBracketsColor = null
    settings.darkRoundBracketsColors = null
    settings.darkSquareBracketsColors = null
    settings.darkSquigglyBracketsColors = null
    settings.darkAngleBracketsColor = null
}

private fun AbstractColorsScheme.migrateFrom(round: List<Color>?,
                                             square: List<Color>?,
                                             squiggly: List<Color>?,
                                             angle: List<Color>?) {
    val migrateRound = migrateFrom(RainbowHighlighter.NAME_ROUND_BRACKETS, round)
    val migrateSquare = migrateFrom(RainbowHighlighter.NAME_SQUARE_BRACKETS, square)
    val migrateSquiggly = migrateFrom(RainbowHighlighter.NAME_SQUIGGLY_BRACKETS, squiggly)
    val migrateAngle = migrateFrom(RainbowHighlighter.NAME_ANGLE_BRACKETS, angle)
    val migrated = migrateRound || migrateSquare || migrateSquiggly || migrateAngle

    if (migrated) {
        setSaveNeeded(true)
    }
}

private fun EditorColorsScheme.migrateFrom(rainbowName: String, colors: List<Color>?): Boolean {
    if (colors == null || colors.isEmpty()) {
        return false
    }

    val keys = RainbowHighlighter.getRainbowAttributesKeys(rainbowName)
    for (i in 0 until (minOf(keys.size, colors.size))) {
        setAttributes(keys[i], RainbowHighlighter.createRainbowAttributes(colors[i]))
    }

    return true
}

private fun <T> Array<T>?.isNullOrEmpty() = this == null || isEmpty()

fun color(hex: String) = Color(Integer.decode(hex))
