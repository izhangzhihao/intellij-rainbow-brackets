package com.github.izhangzhihao.rainbow.brackets.settings

import com.github.izhangzhihao.rainbow.brackets.settings.form.RainbowSettingsForm
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class RainbowConfigurable : Configurable {
    var settingsForm: RainbowSettingsForm? = null

    override fun createComponent(): JComponent? {
        settingsForm = settingsForm ?: RainbowSettingsForm()
        return settingsForm?.component()
    }

    override fun isModified(): Boolean {
        return settingsForm?.isModified ?: return false
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settings = RainbowSettings.instance
        settings.isRainbowEnabled = settingsForm?.isRainbowEnabled() ?: true
        settings.isEnableRainbowRoundBrackets = settingsForm?.isRainbowRoundBracketsEnabled() ?: true
        settings.isEnableRainbowAngleBrackets = settingsForm?.isRainbowAngleBracketsEnabled() ?: true
        settings.isEnableRainbowSquigglyBrackets = settingsForm?.isRainbowSquigglyBracketsEnabled() ?: true
        settings.isEnableRainbowSquareBrackets = settingsForm?.isRainbowSquareBracketsEnabled() ?: true
        settings.isShowRainbowIndentGuides = settingsForm?.isShowRainbowIndentGuides() ?: false
        settings.isDoNOTRainbowifyBracketsWithoutContent = settingsForm?.isDoNOTRainbowifyBracketsWithoutContent()
                ?: false
        settings.isDoNOTRainbowifyTheFirstLevel = settingsForm?.isDoNOTRainbowifyTheFirstLevel() ?: false
        settings.pressAnyKeyToRemoveTheHighlightingEffects = settingsForm?.pressAnyKeyToRemoveTheHighlightingEffects()
                ?: false
        settings.applyColorsOfRoundForAllBrackets = settingsForm?.applyColorsOfRoundForAllBrackets()
                ?: false
        settings.cycleCountOnAllBrackets = settingsForm?.cycleCountOnAllBrackets()
                ?: false
        settings.numberOfColors = settingsForm?.numberOfColors() ?: 5
    }

    override fun reset() {
        settingsForm?.loadSettings()
    }

    override fun disposeUIResources() {
        settingsForm = null
    }

    @Nls
    override fun getDisplayName() = "Rainbow Brackets"
}