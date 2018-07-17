package com.github.izhangzhihao.rainbow.brackets.component

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.extensions.PluginId
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Font

class RainbowComponent : ApplicationComponent {
    var updated: Boolean = false

    override fun initComponent() {
        val settings = RainbowSettings.instance
        updated = getPlugin()?.version != settings.version
        if (updated) {
            settings.version = getPlugin()!!.version
        }
        val globalScheme = EditorColorsManager.getInstance().globalScheme
        if (settings.isRainbowifyHTMLInsideJS) {
            globalScheme.setAttributes(createTextAttributesKey("HTML_CODE"), TextAttributes())
        }

        if (settings.isRainbowifyKotlinFunctionLiteralBracesAndArrow) {
            globalScheme.setAttributes(createTextAttributesKey("KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW"),
                    // Default Attributes
                    TextAttributes(null, null, null, EffectType.BOXED, Font.BOLD))
        }

        if (settings.isRainbowifyKotlinLabel) {
            globalScheme.setAttributes(createTextAttributesKey("KOTLIN_LABEL"),
                    TextAttributes(null, null, null, EffectType.BOXED, Font.PLAIN))
        }

        if (settings.isOverrideMatchedBraceAttributes) {
            globalScheme.setAttributes(createTextAttributesKey("MATCHED_BRACE_ATTRIBUTES"),
                    TextAttributes(null, JBColor(Integer.decode("0x99ccbb"), Integer.decode("0x3b514d")), null, EffectType.BOXED, Font.BOLD))
        }
    }

    companion object {
        val instance: RainbowComponent
            get() = ApplicationManager.getApplication().getComponent(RainbowComponent::class.java)

        private fun getPlugin(): IdeaPluginDescriptor? = PluginManager.getPlugin(
                PluginId.getId("izhangzhihao.rainbow.brackets"))
    }
}