package com.github.izhangzhihao.rainbow.brackets.component

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.impl.AbstractColorsScheme
import com.intellij.openapi.editor.colors.impl.EditorColorsManagerImpl
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.extensions.PluginId
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Font

class RainbowComponent : ApplicationComponent {

    private var _updated: Boolean = false

    val updated: Boolean get() = _updated

    override fun initComponent() {
        val settings = RainbowSettings.instance
        _updated = getPlugin()?.version != settings.version
        if (updated) {
            onUpgrade()
            settings.version = getPlugin()!!.version
        }

        val globalScheme = EditorColorsManager.getInstance().globalScheme
        if (settings.isRainbowifyHTMLInsideJS) {
            globalScheme.setAttributes(KEY_HTML_CODE, TextAttributes())
        }
        if (settings.isRainbowifyKotlinFunctionLiteralBracesAndArrow) {
            globalScheme.setAttributes(KEY_KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW,
                    // Default Attributes
                    TextAttributes(null, null, null, EffectType.BOXED, Font.BOLD))
        }
        if (settings.isRainbowifyKotlinLabel) {
            globalScheme.setAttributes(KEY_KOTLIN_LABEL,
                    TextAttributes(null, null, null, EffectType.BOXED, Font.PLAIN))
        }
        if (settings.isOverrideMatchedBraceAttributes) {
            globalScheme.setAttributes(KEY_MATCHED_BRACE_ATTRIBUTES,
                    TextAttributes(null, JBColor(0x99ccbb, 0x3b514d), null, EffectType.BOXED, Font.BOLD))
        }
    }

    private fun onUpgrade() {
        val settings = RainbowSettings.instance

        val round = settings.lightRoundBracketsColors?.map(::color)
        val square = settings.lightSquareBracketsColors?.map(::color)
        val squiggly = settings.lightSquigglyBracketsColors?.map(::color)
        val angle = settings.lightAngleBracketsColor?.map(::color)

        val roundDark = settings.darkRoundBracketsColors?.map(::color)
        val squareDark = settings.darkSquareBracketsColors?.map(::color)
        val squigglyDark = settings.darkSquigglyBracketsColors?.map(::color)
        val angleDark = settings.darkAngleBracketsColor?.map(::color)

        val colorsManager = EditorColorsManager.getInstance() as EditorColorsManagerImpl
        val schemeGroup = colorsManager.allSchemes
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
        val migrated = migrateFrom(RainbowHighlighter.NAME_ROUND_BRACKETS, round)
                || migrateFrom(RainbowHighlighter.NAME_SQUARE_BRACKETS, square)
                || migrateFrom(RainbowHighlighter.NAME_SQUIGGLY_BRACKETS, squiggly)
                || migrateFrom(RainbowHighlighter.NAME_ANGLE_BRACKETS, angle)

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

    companion object {
        private const val DEFAULT_SCHEME_NAME = "Default"
        private const val DARCULA_SCHEME_NAME = "Darcula"

        private val KEY_HTML_CODE = TextAttributesKey.createTextAttributesKey("HTML_CODE")
        private val KEY_KOTLIN_LABEL = TextAttributesKey.createTextAttributesKey("KOTLIN_LABEL")
        private val KEY_MATCHED_BRACE_ATTRIBUTES =
                TextAttributesKey.createTextAttributesKey("MATCHED_BRACE_ATTRIBUTES")
        private val KEY_KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW =
                TextAttributesKey.createTextAttributesKey("KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW")

        val instance: RainbowComponent
            get() = ApplicationManager.getApplication().getComponent(RainbowComponent::class.java)

        private fun getPlugin(): IdeaPluginDescriptor? = PluginManager.getPlugin(
                PluginId.getId("izhangzhihao.rainbow.brackets"))

        private fun color(hex: String) = Color(Integer.decode(hex))
    }
}