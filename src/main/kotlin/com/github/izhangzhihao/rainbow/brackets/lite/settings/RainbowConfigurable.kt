package com.github.izhangzhihao.rainbow.brackets.lite.settings

import com.github.izhangzhihao.rainbow.brackets.lite.settings.form.RainbowSettingsForm
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class RainbowConfigurable : SearchableConfigurable {
    private var settingsForm: RainbowSettingsForm? = null

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
        settings.numberOfColors = settingsForm?.numberOfColors() ?: 5
        settings.doNOTRainbowifyBigFiles = settingsForm?.doNOTRainbowifyBigFiles() ?: true
        settings.bigFilesLinesThreshold = settingsForm?.bigFilesLinesThreshold() ?: 1000
    }

    override fun reset() {
        settingsForm?.loadSettings()
    }

    override fun disposeUIResources() {
        settingsForm = null
    }

    @Nls
    override fun getDisplayName() = "Rainbow Brackets Lite"

    override fun getId(): String = ID

    companion object {
        val ID = "preferences.rainbow.brackets.lite"
    }
}