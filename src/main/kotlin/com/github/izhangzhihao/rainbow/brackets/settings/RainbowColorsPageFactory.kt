package com.github.izhangzhihao.rainbow.brackets.settings

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter
import com.github.izhangzhihao.rainbow.brackets.settings.form.RainbowOptionsPanel
import com.intellij.application.options.colors.*
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorAndFontDescriptorsProvider
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.psi.codeStyle.DisplayPriority
import com.intellij.psi.codeStyle.DisplayPrioritySortable


class RainbowColorsPageFactory : ColorAndFontPanelFactory, ColorAndFontDescriptorsProvider, DisplayPrioritySortable {

    override fun getDisplayName(): String = RAINBOW_BRACKETS_GROUP

    override fun getPanelDisplayName(): String = RAINBOW_BRACKETS_GROUP

    override fun getPriority(): DisplayPriority = DisplayPriority.COMMON_SETTINGS

    override fun createPanel(options: ColorAndFontOptions): NewColorAndFontPanel {
        val emptyPreview = PreviewPanel.Empty()
        val schemesPanel = SchemesPanel(options)
        val optionsPanel = RainbowOptionsPanel(options, schemesPanel, RAINBOW_BRACKETS_GROUP)

        schemesPanel.addListener(object : ColorAndFontSettingsListener.Abstract() {
            override fun schemeChanged(source: Any?) {
                optionsPanel.updateOptionsList()
            }
        })

        return NewColorAndFontPanel(schemesPanel, optionsPanel, emptyPreview, RAINBOW_BRACKETS_GROUP, null, null)
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTRIBUTE_DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = emptyArray()

    companion object {
        private const val RAINBOW_BRACKETS_GROUP = "Rainbow Brackets"
        private val ATTRIBUTE_DESCRIPTORS = createDescriptors(RainbowHighlighter.NAME_ROUND_BRACKETS) +
                createDescriptors(RainbowHighlighter.NAME_SQUARE_BRACKETS) +
                createDescriptors(RainbowHighlighter.NAME_SQUIGGLY_BRACKETS) +
                createDescriptors(RainbowHighlighter.NAME_ANGLE_BRACKETS)

        private fun createDescriptors(name: String): Array<AttributesDescriptor> {
            return RainbowHighlighter.getRainbowAttributesKeys(name)
                    .map { key -> AttributesDescriptor("$name:$key", key) }
                    .toTypedArray()
        }
    }

}