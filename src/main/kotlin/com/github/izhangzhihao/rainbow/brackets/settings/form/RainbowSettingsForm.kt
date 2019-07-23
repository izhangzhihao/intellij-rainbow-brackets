package com.github.izhangzhihao.rainbow.brackets.settings.form

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import javax.swing.*

class RainbowSettingsForm {
    private var panel: JPanel? = null
    private var appearancePanel: JPanel? = null
    private var enableRainbow: JCheckBox? = null
    private var enableRainbowRoundBrackets: JCheckBox? = null
    private var enableRainbowSquigglyBrackets: JCheckBox? = null
    private var enableRainbowSquareBrackets: JCheckBox? = null
    private var enableRainbowAngleBrackets: JCheckBox? = null
    private var doNOTRainbowifyBracketsWithoutContent: JCheckBox? = null
    private var isDoNOTRainbowifyTheFirstLevel: JCheckBox? = null
    private var pressAnyKeyToRemoveTheHighlightingEffects: JCheckBox? = null

    private val settings: RainbowSettings = RainbowSettings.instance

    fun component(): JComponent? = panel

    fun isRainbowEnabled() = enableRainbow?.isSelected

    fun isRainbowRoundBracketsEnabled() = enableRainbowRoundBrackets?.isSelected

    fun isRainbowSquigglyBracketsEnabled() = enableRainbowSquigglyBrackets?.isSelected

    fun isRainbowSquareBracketsEnabled() = enableRainbowSquareBrackets?.isSelected

    fun isRainbowAngleBracketsEnabled() = enableRainbowAngleBrackets?.isSelected

    fun isDoNOTRainbowifyBracketsWithoutContent() = doNOTRainbowifyBracketsWithoutContent?.isSelected

    fun isDoNOTRainbowifyTheFirstLevel() = isDoNOTRainbowifyTheFirstLevel?.isSelected

    fun pressAnyKeyToRemoveTheHighlightingEffects() = pressAnyKeyToRemoveTheHighlightingEffects?.isSelected

    val isModified: Boolean
        get() = (isRainbowEnabled() != settings.isRainbowEnabled
                || isRainbowAngleBracketsEnabled() != settings.isEnableRainbowAngleBrackets
                || isRainbowRoundBracketsEnabled() != settings.isEnableRainbowRoundBrackets
                || isRainbowSquigglyBracketsEnabled() != settings.isEnableRainbowSquigglyBrackets
                || isRainbowSquareBracketsEnabled() != settings.isEnableRainbowSquareBrackets
                || isDoNOTRainbowifyBracketsWithoutContent() != settings.isDoNOTRainbowifyBracketsWithoutContent
                || isDoNOTRainbowifyTheFirstLevel() != settings.isDoNOTRainbowifyTheFirstLevel
                || pressAnyKeyToRemoveTheHighlightingEffects() != settings.pressAnyKeyToRemoveTheHighlightingEffects
                )

    init {
        loadSettings()
    }

    fun loadSettings() {
        enableRainbow?.isSelected = settings.isRainbowEnabled
        enableRainbowRoundBrackets?.isSelected = settings.isEnableRainbowRoundBrackets
        enableRainbowAngleBrackets?.isSelected = settings.isEnableRainbowAngleBrackets
        enableRainbowSquigglyBrackets?.isSelected = settings.isEnableRainbowSquigglyBrackets
        enableRainbowSquareBrackets?.isSelected = settings.isEnableRainbowSquareBrackets
        doNOTRainbowifyBracketsWithoutContent?.isSelected = settings.isDoNOTRainbowifyBracketsWithoutContent
        isDoNOTRainbowifyTheFirstLevel?.isSelected = settings.isDoNOTRainbowifyTheFirstLevel
        pressAnyKeyToRemoveTheHighlightingEffects?.isSelected = settings.pressAnyKeyToRemoveTheHighlightingEffects
    }
}
