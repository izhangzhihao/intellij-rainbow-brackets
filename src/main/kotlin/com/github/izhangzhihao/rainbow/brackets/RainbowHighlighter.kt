package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Font

object RainbowHighlighter {

    const val NAME_ROUND_BRACKETS = "Round Brackets"
    const val NAME_SQUARE_BRACKETS = "Square Brackets"
    const val NAME_SQUIGGLY_BRACKETS = "Squiggly Brackets"
    const val NAME_ANGLE_BRACKETS = "Angle Brackets"

    private const val KEY_ROUND_BRACKETS = "ROUND_BRACKETS_RAINBOW_COLOR"
    private const val KEY_SQUARE_BRACKETS = "SQUARE_BRACKETS_RAINBOW_COLOR"
    private const val KEY_SQUIGGLY_BRACKETS = "SQUIGGLY_BRACKETS_RAINBOW_COLOR"
    private const val KEY_ANGLE_BRACKETS = "ANGLE_BRACKETS_RAINBOW_COLOR"

    val roundBrackets: CharArray = charArrayOf('(', ')')
    val squareBrackets: CharArray = charArrayOf('[', ']')
    val squigglyBrackets: CharArray = charArrayOf('{', '}')
    val angleBrackets: CharArray = charArrayOf('<', '>')

    private val settings = RainbowSettings.instance

    val roundBracketsColors: Array<Color> = jBColor(settings.lightRoundBracketsColors, settings.darkRoundBracketsColors)
    val squareBracketsColors: Array<Color> = jBColor(settings.lightSquareBracketsColors, settings.darkSquareBracketsColors)
    val squigglyBracketsColors: Array<Color> = jBColor(settings.lightSquigglyBracketsColors, settings.darkSquigglyBracketsColors)
    val angleBracketsColor: Array<Color> = jBColor(settings.lightAngleBracketsColor, settings.darkAngleBracketsColor)

    private val rainbowElement: HighlightInfoType = HighlightInfoType
            .HighlightInfoTypeImpl(HighlightSeverity.INFORMATION, DefaultLanguageHighlighterColors.CONSTANT)

    fun Array<Color>.getColor(level: Int) = this[level % size]

    private val PsiElement.isRoundBracket get() = roundBrackets.any { textContains(it) }
    private val PsiElement.isSquareBracket get() = squareBrackets.any { textContains(it) }
    private val PsiElement.isSquigglyBracket get() = squigglyBrackets.any { textContains(it) }
    private val PsiElement.isAngleBracket get() = angleBrackets.any { textContains(it) }

    fun createRainbowAttributesKeys(rainbowName: String): List<TextAttributesKey> {
        val (keyName, keyCount) = when (rainbowName) {
            NAME_ROUND_BRACKETS -> KEY_ROUND_BRACKETS to 5
            NAME_SQUARE_BRACKETS -> KEY_SQUARE_BRACKETS to 3
            NAME_SQUIGGLY_BRACKETS -> KEY_SQUIGGLY_BRACKETS to 3
            NAME_ANGLE_BRACKETS -> KEY_ANGLE_BRACKETS to 5
            else -> throw IllegalArgumentException("Unknown rainbow name: $rainbowName")
        }

        return generateSequence(0) { it + 1 }
                .map { TextAttributesKey.createTextAttributesKey("$keyName$it") }
                .take(keyCount)
                .toList()
    }

    fun isRainbowEnabled(rainbowName: String): Boolean {
        return when (rainbowName) {
            NAME_ROUND_BRACKETS -> settings.isEnableRainbowRoundBrackets
            NAME_SQUARE_BRACKETS -> settings.isEnableRainbowSquareBrackets
            NAME_SQUIGGLY_BRACKETS -> settings.isEnableRainbowSquigglyBrackets
            NAME_ANGLE_BRACKETS -> settings.isEnableRainbowAngleBrackets
            else -> throw IllegalArgumentException("Unknown rainbow name: $rainbowName")
        }
    }

    fun setRainbowEnabled(rainbowName: String, enabled: Boolean) {
        when (rainbowName) {
            NAME_ROUND_BRACKETS -> settings.isEnableRainbowRoundBrackets = enabled
            NAME_SQUARE_BRACKETS -> settings.isEnableRainbowSquareBrackets = enabled
            NAME_SQUIGGLY_BRACKETS -> settings.isEnableRainbowSquigglyBrackets = enabled
            NAME_ANGLE_BRACKETS -> settings.isEnableRainbowAngleBrackets = enabled
            else -> throw IllegalArgumentException("Unknown rainbow name: $rainbowName")
        }
    }

    private fun createTextAttributes(element: PsiElement, level: Int): TextAttributes? {
        if (!settings.isRainbowEnabled) {
            return null
        }

        val color = when {
            element.isRoundBracket -> if (settings.isEnableRainbowRoundBrackets) roundBracketsColors else return null
            element.isSquareBracket -> if (settings.isEnableRainbowSquareBrackets) squareBracketsColors else return null
            element.isSquigglyBracket -> if (settings.isEnableRainbowSquigglyBrackets) squigglyBracketsColors else return null
            element.isAngleBracket -> if (settings.isEnableRainbowAngleBrackets) angleBracketsColor else return null
            else -> roundBracketsColors
        }.getColor(level)

        return TextAttributes(color, null, null, null, Font.PLAIN)
    }

    fun getHighlightInfo(element: PsiElement, level: Int)
            : HighlightInfo? = createTextAttributes(element, level)
            ?.let {
                HighlightInfo
                        .newHighlightInfo(rainbowElement)
                        .textAttributes(it)
                        .range(element)
                        .create()
            }

    private fun jBColor(light: Array<String>, dark: Array<String>): Array<Color> =
            light.zip(dark).map { JBColor(Integer.decode(it.first), Integer.decode(it.second)) }.toTypedArray()
}
