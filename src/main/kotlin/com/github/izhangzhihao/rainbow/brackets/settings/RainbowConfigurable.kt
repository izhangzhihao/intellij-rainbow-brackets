package com.github.izhangzhihao.rainbow.brackets.settings

import com.github.izhangzhihao.rainbow.brackets.settings.form.RainbowSettingsForm
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
        settings.languageBlacklist = settingsForm?.languageBlacklist() ?: emptySet()
        settings.disableRainbowIndentsInZenMode = settingsForm?.disableRainbowIndentsInZenMode() ?: true
        settings.useColorGenerator = settingsForm?.useColorGenerator() ?: false
        settings.rainbowifyTagNameInXML = settingsForm?.rainbowifyTagNameInXML() ?: false
        settings.doNOTRainbowifyTemplateString = settingsForm?.doNOTRainbowifyTemplateString() ?: false
        settings.doNOTRainbowifyBigFiles = settingsForm?.doNOTRainbowifyBigFiles() ?: true
        settings.bigFilesLinesThreshold = settingsForm?.bigFilesLinesThreshold() ?: 1000
        settings.rainbowifyPythonKeywords = settingsForm?.rainbowifyPythonKeywords() ?: false
    }

    override fun reset() {
        settingsForm?.loadSettings()
    }

    override fun disposeUIResources() {
        settingsForm = null
    }

    @Nls
    override fun getDisplayName() = "Rainbow Brackets"

    override fun getId(): String = ID

    companion object {
        val ID = "preferences.rainbow.brackets"
    }
}