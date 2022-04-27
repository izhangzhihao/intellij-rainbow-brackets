package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.util.memoize
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesScheme
import com.intellij.openapi.editor.colors.impl.AbstractColorsScheme
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.TestOnly
import java.awt.Color
import java.awt.Font
import java.util.UUID

object RainbowHighlighter {

    val DEFAULT_KOTLIN_LABEL_COLOR = JBColor(0x4a86e8, 0x467cda)

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

    private val settings = RainbowSettings.instance

    private val roundBracketsRainbowColorKeys: Array<TextAttributesKey> =
            createRainbowAttributesKeys(KEY_ROUND_BRACKETS, settings.numberOfColors)
    private val squareBracketsRainbowColorKeys: Array<TextAttributesKey> =
            createRainbowAttributesKeys(KEY_SQUARE_BRACKETS, settings.numberOfColors)
    private val squigglyBracketsRainbowColorKeys: Array<TextAttributesKey> =
            createRainbowAttributesKeys(KEY_SQUIGGLY_BRACKETS, settings.numberOfColors)
    private val angleBracketsRainbowColorKeys: Array<TextAttributesKey> =
            createRainbowAttributesKeys(KEY_ANGLE_BRACKETS, settings.numberOfColors)

    private val rainbowElement: HighlightInfoType = HighlightInfoType
            .HighlightInfoTypeImpl(HighlightSeverity.INFORMATION, DefaultLanguageHighlighterColors.CONSTANT)

    private val PsiElement.isRoundBracket get() = roundBrackets.any { textContains(it) }
    private val PsiElement.isSquareBracket get() = squareBrackets.any { textContains(it) }
    private val PsiElement.isSquigglyBracket get() = squigglyBrackets.any { textContains(it) }
    private val PsiElement.isAngleBracket get() = angleBrackets.any { textContains(it) }

    private fun createRainbowAttributesKeys(keyName: String, size: Int): Array<TextAttributesKey> {
        return generateSequence(0) { it + 1 }
                .map { TextAttributesKey.createTextAttributesKey("$keyName$it") }
                .take(size)
                .toList()
                .toTypedArray()
    }

    fun getRainbowAttributesKeys(rainbowName: String): Array<TextAttributesKey> {
        return when (rainbowName) {
            NAME_ROUND_BRACKETS -> roundBracketsRainbowColorKeys
            NAME_SQUARE_BRACKETS -> squareBracketsRainbowColorKeys
            NAME_SQUIGGLY_BRACKETS -> squigglyBracketsRainbowColorKeys
            NAME_ANGLE_BRACKETS -> angleBracketsRainbowColorKeys
            else -> throw IllegalArgumentException("Unknown rainbow name: $rainbowName")
        }
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

    fun isDarkEditor() = EditorColorsManager.getInstance().isDarkEditor

    fun getRainbowColorByLevel(colorsScheme: TextAttributesScheme, rainbowName: String, level: Int): TextAttributesKey {
        val ind = level % settings.numberOfColors
        if (settings.useColorGenerator) {
            return memGetRainbowColorByLevel(isDarkEditor(), rainbowName, ind)
        }
        val key = getRainbowAttributesKeys(rainbowName).getOrNull(ind)
        return try {
            val result = colorsScheme.getAttributes(key)
            if (key == null || result == null || result.foregroundColor == null) {
                memGetRainbowColorByLevel(isDarkEditor(), rainbowName, ind)
            } else {
                key
            }
        } catch (e: Exception) {
            memGetRainbowColorByLevel(isDarkEditor(), rainbowName, ind)
        }
    }

    @Suppress("UNUSED_PARAMETER") // we use parameter as cache key
    private fun generateColor(isDark: Boolean, rainbowName: String, level: Int): TextAttributesKey {
        if (!settings.customColorGeneratorOption.isNullOrBlank()) {
            return genByOption(settings.customColorGeneratorOption!!, rainbowName, level)
        }
        if (isDark) {
            @Language("JSON") val darkOption = """{"luminosity": "light"}"""
            return genByOption(darkOption, rainbowName, level)
        }
        @Language("JSON") val lightOption = """{"luminosity": "dark"}"""
        return genByOption(lightOption, rainbowName, level)
    }

    private fun genByOption(option: String, rainbowName: String, level: Int) =
        com.github.izhangzhihao.rainbow.brackets.util.create("$rainbowName-$level",
            TextAttributes(randomColor(option), null, null, null, 0))

    val memGetRainbowColorByLevel = { isDark: Boolean, rainbowName: String, level: Int -> generateColor(isDark, rainbowName, level) }.memoize()

    @TestOnly
    fun getBrackets(): CharArray = roundBrackets + squareBrackets + squigglyBrackets + angleBrackets

    @TestOnly
    fun getRainbowColor(rainbowName: String, level: Int): Color? {
        return getRainbowColorByLevel(EditorColorsManager.getInstance().globalScheme, rainbowName, level).defaultAttributes.foregroundColor
    }

    private fun getTextAttributes(colorsScheme: TextAttributesScheme,
                                  element: PsiElement,
                                  level: Int): TextAttributesKey? {
        if (!settings.isRainbowEnabled) {
            return null
        }

        val rainbowName = when {
            settings.applyColorsOfRoundForAllBrackets -> NAME_ROUND_BRACKETS
            element.isRoundBracket -> if (settings.isEnableRainbowRoundBrackets) NAME_ROUND_BRACKETS else null
            element.isSquareBracket -> if (settings.isEnableRainbowSquareBrackets) NAME_SQUARE_BRACKETS else null
            element.isSquigglyBracket -> if (settings.isEnableRainbowSquigglyBrackets) NAME_SQUIGGLY_BRACKETS else null
            element.isAngleBracket -> if (settings.isEnableRainbowAngleBrackets) NAME_ANGLE_BRACKETS else null
            else -> NAME_ROUND_BRACKETS
        } ?: return null

        return getRainbowColorByLevel(colorsScheme, rainbowName, level)
    }

    fun getHighlightInfo(colorsScheme: TextAttributesScheme, element: PsiElement, level: Int)
            : HighlightInfo? = getTextAttributes(colorsScheme, element, level)
            ?.let { attr ->
                HighlightInfo
                        .newHighlightInfo(rainbowElement)
                        .textAttributes(attr)
                        .range(element)
                        .create()
            }


    private val KEY_HTML_CODE: TextAttributesKey by lazy { TextAttributesKey.createTextAttributesKey("HTML_CODE") }
    private val KEY_KOTLIN_LABEL: TextAttributesKey by lazy { TextAttributesKey.createTextAttributesKey("KOTLIN_LABEL") }
    private val KEY_MATCHED_BRACE_ATTRIBUTES: TextAttributesKey by lazy {
        TextAttributesKey.createTextAttributesKey("MATCHED_BRACE_ATTRIBUTES")
    }
    private val KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW: TextAttributesKey by lazy {
        TextAttributesKey.createTextAttributesKey("KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW")
    }


    fun fixHighlighting(scheme: EditorColorsScheme = EditorColorsManager.getInstance().globalScheme) {
        // html code
        scheme.setInherited(KEY_HTML_CODE, !settings.isRainbowifyHTMLInsideJS)

        // kotlin label
        val kotlinLabelColor = DEFAULT_KOTLIN_LABEL_COLOR.takeUnless { settings.isRainbowifyKotlinLabel }
        val kotlinLabel = TextAttributes(kotlinLabelColor, null, null, EffectType.BOXED, Font.PLAIN)
        scheme.setAttributes(KEY_KOTLIN_LABEL, kotlinLabel)

        // matched brace
        if (settings.isOverrideMatchedBraceAttributes) {
            val matchedBraceAttributes = TextAttributes(null, JBColor(0x99ccbb, 0x3b514d), null, EffectType.BOXED, Font.BOLD)
            scheme.setAttributes(KEY_MATCHED_BRACE_ATTRIBUTES, matchedBraceAttributes)
        }

        if (settings.isRainbowifyKotlinFunctionLiteralBracesAndArrow) {
            scheme.setAttributes(KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW,
                    TextAttributes(null, null, null, EffectType.BOXED, Font.BOLD))
        } else {
            scheme.setAttributes(KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW,
                    //TODO: default foregroundColor ???
                    TextAttributes(JBColor(0x89ddff, 0x89ddff), null, null, EffectType.BOXED, Font.BOLD))
        }
    }

    private fun EditorColorsScheme.setInherited(key: TextAttributesKey, inherited: Boolean) {
        setAttributes(key, if (inherited) AbstractColorsScheme.INHERITED_ATTRS_MARKER else TextAttributes())
    }
}
