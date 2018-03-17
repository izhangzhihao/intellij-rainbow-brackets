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

    private val settings: RainbowSettings = RainbowSettings.instance

    val component: JComponent?
        get() = panel

    val isRainbowEnabled: Boolean
        get() = enableRainbow!!.isSelected

    val isRainbowRoundBracketsEnabled: Boolean
        get() = enableRainbowRoundBrackets!!.isSelected

    val isRainbowSquigglyBracketsEnabled: Boolean
        get() = enableRainbowSquigglyBrackets!!.isSelected

    val isRainbowSquareBracketsEnabled: Boolean
        get() = enableRainbowSquareBrackets!!.isSelected

    val isRainbowAngleBracketsEnabled: Boolean
        get() = enableRainbowAngleBrackets!!.isSelected

    val isDoNOTRainbowifyBracketsWithoutContent: Boolean
        get() = doNOTRainbowifyBracketsWithoutContent!!.isSelected

    val isModified: Boolean
        get() = (isRainbowEnabled != settings.isRainbowEnabled
                || isRainbowAngleBracketsEnabled != settings.isEnableRainbowAngleBrackets
                || isRainbowRoundBracketsEnabled != settings.isEnableRainbowRoundBrackets
                || isRainbowSquigglyBracketsEnabled != settings.isEnableRainbowSquigglyBrackets
                || isRainbowSquareBracketsEnabled != settings.isEnableRainbowSquareBrackets
                || isDoNOTRainbowifyBracketsWithoutContent != settings.isDoNOTRainbowifyBracketsWithoutContent)

    init {
        enableRainbow!!.isSelected = settings.isRainbowEnabled
        enableRainbowRoundBrackets!!.isSelected = settings.isEnableRainbowRoundBrackets
        enableRainbowSquigglyBrackets!!.isSelected = settings.isEnableRainbowSquigglyBrackets
        enableRainbowSquareBrackets!!.isSelected = settings.isEnableRainbowSquareBrackets
        enableRainbowAngleBrackets!!.isSelected = settings.isEnableRainbowAngleBrackets
        doNOTRainbowifyBracketsWithoutContent!!.isSelected = settings.isDoNOTRainbowifyBracketsWithoutContent
    }

    fun reset() {
        enableRainbow!!.isSelected = settings.isRainbowEnabled
        enableRainbowRoundBrackets!!.isSelected = settings.isEnableRainbowRoundBrackets
        enableRainbowAngleBrackets!!.isSelected = settings.isEnableRainbowAngleBrackets
        enableRainbowSquigglyBrackets!!.isSelected = settings.isEnableRainbowSquigglyBrackets
        enableRainbowSquareBrackets!!.isSelected = settings.isEnableRainbowSquareBrackets
        doNOTRainbowifyBracketsWithoutContent!!.isSelected = settings.isDoNOTRainbowifyBracketsWithoutContent
    }
}
