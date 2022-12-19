package com.github.izhangzhihao.rainbow.brackets.lite.settings.form

import com.github.izhangzhihao.rainbow.brackets.lite.settings.RainbowSettings
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class RainbowSettingsForm {
    private var panel: JPanel? = null
    private var appearancePanel: JPanel? = null
    private var enableRainbow: JCheckBox? = null
    private var enableRainbowRoundBrackets: JCheckBox? = null
    private var enableRainbowSquigglyBrackets: JCheckBox? = null
    private var enableRainbowSquareBrackets: JCheckBox? = null
    private var enableRainbowAngleBrackets: JCheckBox? = null
    private var showRainbowIndentGuides: JCheckBox? = null

    private var numberOfColors: JTextField? = null

    private var doNOTRainbowifyBigFiles: JCheckBox? = null

    private var bigFilesLinesThreshold: JTextField? = null

    private val settings: RainbowSettings = RainbowSettings.instance

    fun component(): JComponent? = panel

    fun isRainbowEnabled() = enableRainbow?.isSelected

    fun isRainbowRoundBracketsEnabled() = enableRainbowRoundBrackets?.isSelected

    fun isRainbowSquigglyBracketsEnabled() = enableRainbowSquigglyBrackets?.isSelected

    fun isRainbowSquareBracketsEnabled() = enableRainbowSquareBrackets?.isSelected

    fun isRainbowAngleBracketsEnabled() = enableRainbowAngleBrackets?.isSelected

    fun isShowRainbowIndentGuides() = showRainbowIndentGuides?.isSelected

    fun numberOfColors() = numberOfColors?.text?.toIntOrNull()

    fun doNOTRainbowifyBigFiles() = doNOTRainbowifyBigFiles?.isSelected

    fun bigFilesLinesThreshold() = bigFilesLinesThreshold?.text?.toIntOrNull()

    val isModified: Boolean
        get() = (isRainbowEnabled() != settings.isRainbowEnabled
                || isRainbowAngleBracketsEnabled() != settings.isEnableRainbowAngleBrackets
                || isRainbowRoundBracketsEnabled() != settings.isEnableRainbowRoundBrackets
                || isRainbowSquigglyBracketsEnabled() != settings.isEnableRainbowSquigglyBrackets
                || isRainbowSquareBracketsEnabled() != settings.isEnableRainbowSquareBrackets
                || isShowRainbowIndentGuides() != settings.isShowRainbowIndentGuides
                || numberOfColors() != settings.numberOfColors
                || doNOTRainbowifyBigFiles() != settings.doNOTRainbowifyBigFiles
                || bigFilesLinesThreshold() != settings.bigFilesLinesThreshold
                )

    init {
        loadSettings()
        doNOTRainbowifyBigFiles?.addActionListener({ e ->
            bigFilesLinesThreshold?.setEnabled(
                doNOTRainbowifyBigFiles() ?: false
            )
        })
    }

    fun loadSettings() {
        enableRainbow?.isSelected = settings.isRainbowEnabled
        enableRainbowRoundBrackets?.isSelected = settings.isEnableRainbowRoundBrackets
        enableRainbowAngleBrackets?.isSelected = settings.isEnableRainbowAngleBrackets
        enableRainbowSquigglyBrackets?.isSelected = settings.isEnableRainbowSquigglyBrackets
        enableRainbowSquareBrackets?.isSelected = settings.isEnableRainbowSquareBrackets
        showRainbowIndentGuides?.isSelected = settings.isShowRainbowIndentGuides
        numberOfColors?.text = settings.numberOfColors.toString()
        doNOTRainbowifyBigFiles?.isSelected = settings.doNOTRainbowifyBigFiles
        bigFilesLinesThreshold?.text = settings.bigFilesLinesThreshold.toString()
    }
}
