package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesScheme
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.TestOnly
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

    private val roundBrackets: CharArray = charArrayOf('(', ')')
    private val squareBrackets: CharArray = charArrayOf('[', ']')
    private val squigglyBrackets: CharArray = charArrayOf('{', '}')
    private val angleBrackets: CharArray = charArrayOf('<', '>')

    private val roundBracketsRainbowColorKeys: List<TextAttributesKey> =
            createRainbowAttributesKeys(KEY_ROUND_BRACKETS, 5)
    private val squareBracketsRainbowColorKeys: List<TextAttributesKey> =
            createRainbowAttributesKeys(KEY_SQUARE_BRACKETS, 3)
    private val squigglyBracketsRainbowColorKeys: List<TextAttributesKey> =
            createRainbowAttributesKeys(KEY_SQUIGGLY_BRACKETS, 3)
    private val angleBracketsRainbowColorKeys: List<TextAttributesKey> =
            createRainbowAttributesKeys(KEY_ANGLE_BRACKETS, 5)

    private val settings = RainbowSettings.instance

    private val rainbowElement: HighlightInfoType = HighlightInfoType
            .HighlightInfoTypeImpl(HighlightSeverity.INFORMATION, DefaultLanguageHighlighterColors.CONSTANT)

    private val PsiElement.isRoundBracket get() = roundBrackets.any { textContains(it) }
    private val PsiElement.isSquareBracket get() = squareBrackets.any { textContains(it) }
    private val PsiElement.isSquigglyBracket get() = squigglyBrackets.any { textContains(it) }
    private val PsiElement.isAngleBracket get() = angleBrackets.any { textContains(it) }

    private fun createRainbowAttributesKeys(keyName: String, size: Int): List<TextAttributesKey> {
        return generateSequence(0) { it + 1 }
                .map { TextAttributesKey.createTextAttributesKey("$keyName$it") }
                .take(size)
                .toList()
    }

    fun getRainbowAttributesKeys(rainbowName: String): List<TextAttributesKey> {
        return when (rainbowName) {
            NAME_ROUND_BRACKETS -> roundBracketsRainbowColorKeys
            NAME_SQUARE_BRACKETS -> squareBracketsRainbowColorKeys
            NAME_SQUIGGLY_BRACKETS -> squigglyBracketsRainbowColorKeys
            NAME_ANGLE_BRACKETS -> angleBracketsRainbowColorKeys
            else -> throw IllegalArgumentException("Unknown rainbow name: $rainbowName")
        }
    }

    fun createRainbowAttributes(color: Color): TextAttributes {
        return TextAttributes(color, null, null, EffectType.BOXED, Font.PLAIN)
    }

    // FIXME: Meta properties(SchemeMetaInfo) should be used.
    fun isRainbowEnabled(rainbowName: String): Boolean {
        return when (rainbowName) {
            NAME_ROUND_BRACKETS -> settings.isEnableRainbowRoundBrackets
            NAME_SQUARE_BRACKETS -> settings.isEnableRainbowSquareBrackets
            NAME_SQUIGGLY_BRACKETS -> settings.isEnableRainbowSquigglyBrackets
            NAME_ANGLE_BRACKETS -> settings.isEnableRainbowAngleBrackets
            else -> throw IllegalArgumentException("Unknown rainbow name: $rainbowName")
        }
    }

    // FIXME: Meta properties(SchemeMetaInfo) should be used.
    fun setRainbowEnabled(rainbowName: String, enabled: Boolean) {
        when (rainbowName) {
            NAME_ROUND_BRACKETS -> settings.isEnableRainbowRoundBrackets = enabled
            NAME_SQUARE_BRACKETS -> settings.isEnableRainbowSquareBrackets = enabled
            NAME_SQUIGGLY_BRACKETS -> settings.isEnableRainbowSquigglyBrackets = enabled
            NAME_ANGLE_BRACKETS -> settings.isEnableRainbowAngleBrackets = enabled
            else -> throw IllegalArgumentException("Unknown rainbow name: $rainbowName")
        }
    }

    private fun List<TextAttributesKey>.getAttributesKey(level: Int) = this[level % size]

    private fun getTextAttributes(colorsScheme: TextAttributesScheme, rainbowName: String, level: Int): TextAttributes {
        val key = getRainbowAttributesKeys(rainbowName).getAttributesKey(level)
        return colorsScheme.getAttributes(key)
    }

    @TestOnly
    fun getBrackets(): CharArray = roundBrackets + squareBrackets + squigglyBrackets + angleBrackets

    @TestOnly
    fun getRainbowColor(rainbowName: String, level: Int): Color? {
        return getTextAttributes(EditorColorsManager.getInstance().globalScheme, rainbowName, level).foregroundColor
    }

    private fun getTextAttributes(colorsScheme: TextAttributesScheme?, element: PsiElement, level: Int): TextAttributes? {
        if (!settings.isRainbowEnabled) {
            return null
        }

        val scheme = colorsScheme ?: EditorColorsManager.getInstance().globalScheme
        val rainbowName = when {
            element.isRoundBracket -> if (settings.isEnableRainbowRoundBrackets) NAME_ROUND_BRACKETS else null
            element.isSquareBracket -> if (settings.isEnableRainbowSquareBrackets) NAME_SQUARE_BRACKETS else null
            element.isSquigglyBracket -> if (settings.isEnableRainbowSquigglyBrackets) NAME_SQUIGGLY_BRACKETS else null
            element.isAngleBracket -> if (settings.isEnableRainbowAngleBrackets) NAME_ANGLE_BRACKETS else null
            else -> NAME_ROUND_BRACKETS
        } ?: return null

        return getTextAttributes(scheme, rainbowName, level)
    }

    fun getHighlightInfo(colorsScheme: TextAttributesScheme?, element: PsiElement, level: Int)
            : HighlightInfo? = getTextAttributes(colorsScheme, element, level)
            ?.let { attr ->
                HighlightInfo
                        .newHighlightInfo(rainbowElement)
                        .textAttributes(attr)
                        .range(element)
                        .create()
            }
}
