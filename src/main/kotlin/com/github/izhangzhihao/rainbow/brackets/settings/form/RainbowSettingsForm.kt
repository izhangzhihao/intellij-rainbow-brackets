package com.github.izhangzhihao.rainbow.brackets.settings.form

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
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
    private var doNOTRainbowifyBracketsWithoutContent: JCheckBox? = null
    private var isDoNOTRainbowifyTheFirstLevel: JCheckBox? = null
    private var pressAnyKeyToRemoveTheHighlightingEffects: JCheckBox? = null
    private var applyColorsOfRoundForAllBrackets: JCheckBox? = null
    private var cycleCountOnAllBrackets: JCheckBox? = null

    private var numberOfColors: JTextField? = null

    private var languageBlacklist: JTextField? = null

    private var disableRainbowIndentsInZenMode: JCheckBox? = null

    private var useColorGenerator: JCheckBox? = null

    private var rainbowifyTagNameInXML: JCheckBox? = null

    private var doNOTRainbowifyTemplateString: JCheckBox? = null

    private var doNOTRainbowifyBigFiles: JCheckBox? = null

    private var bigFilesLinesThreshold: JTextField? = null

    private var rainbowifyPythonKeywords: JCheckBox? = null

    private val settings: RainbowSettings = RainbowSettings.instance

    fun component(): JComponent? = panel

    fun isRainbowEnabled() = enableRainbow?.isSelected

    fun isRainbowRoundBracketsEnabled() = enableRainbowRoundBrackets?.isSelected

    fun isRainbowSquigglyBracketsEnabled() = enableRainbowSquigglyBrackets?.isSelected

    fun isRainbowSquareBracketsEnabled() = enableRainbowSquareBrackets?.isSelected

    fun isRainbowAngleBracketsEnabled() = enableRainbowAngleBrackets?.isSelected

    fun isShowRainbowIndentGuides() = showRainbowIndentGuides?.isSelected

    fun isDoNOTRainbowifyBracketsWithoutContent() = doNOTRainbowifyBracketsWithoutContent?.isSelected

    fun isDoNOTRainbowifyTheFirstLevel() = isDoNOTRainbowifyTheFirstLevel?.isSelected

    fun pressAnyKeyToRemoveTheHighlightingEffects() = pressAnyKeyToRemoveTheHighlightingEffects?.isSelected

    fun applyColorsOfRoundForAllBrackets() = applyColorsOfRoundForAllBrackets?.isSelected

    fun cycleCountOnAllBrackets() = cycleCountOnAllBrackets?.isSelected

    fun numberOfColors() = numberOfColors?.text?.toIntOrNull()

    fun languageBlacklist() =
        languageBlacklist?.text?.split(",")?.map { it.trim() }?.filterNot { it.isEmpty() }?.toSet()

    fun disableRainbowIndentsInZenMode() = disableRainbowIndentsInZenMode?.isSelected

    fun useColorGenerator() = useColorGenerator?.isSelected

    fun rainbowifyTagNameInXML() = rainbowifyTagNameInXML?.isSelected

    fun doNOTRainbowifyTemplateString() = doNOTRainbowifyTemplateString?.isSelected

    fun doNOTRainbowifyBigFiles() = doNOTRainbowifyBigFiles?.isSelected

    fun bigFilesLinesThreshold() = bigFilesLinesThreshold?.text?.toIntOrNull()

    fun rainbowifyPythonKeywords() = rainbowifyPythonKeywords?.isSelected

    val isModified: Boolean
        get() = (isRainbowEnabled() != settings.isRainbowEnabled
                || isRainbowAngleBracketsEnabled() != settings.isEnableRainbowAngleBrackets
                || isRainbowRoundBracketsEnabled() != settings.isEnableRainbowRoundBrackets
                || isRainbowSquigglyBracketsEnabled() != settings.isEnableRainbowSquigglyBrackets
                || isRainbowSquareBracketsEnabled() != settings.isEnableRainbowSquareBrackets
                || isShowRainbowIndentGuides() != settings.isShowRainbowIndentGuides
                || isDoNOTRainbowifyBracketsWithoutContent() != settings.isDoNOTRainbowifyBracketsWithoutContent
                || isDoNOTRainbowifyTheFirstLevel() != settings.isDoNOTRainbowifyTheFirstLevel
                || pressAnyKeyToRemoveTheHighlightingEffects() != settings.pressAnyKeyToRemoveTheHighlightingEffects
                || applyColorsOfRoundForAllBrackets() != settings.applyColorsOfRoundForAllBrackets
                || cycleCountOnAllBrackets() != settings.cycleCountOnAllBrackets
                || numberOfColors() != settings.numberOfColors
                || disableRainbowIndentsInZenMode() != settings.disableRainbowIndentsInZenMode
                || useColorGenerator() != settings.useColorGenerator
                || rainbowifyTagNameInXML() != settings.rainbowifyTagNameInXML
                || doNOTRainbowifyTemplateString() != settings.doNOTRainbowifyTemplateString
                || doNOTRainbowifyBigFiles() != settings.doNOTRainbowifyBigFiles
                || bigFilesLinesThreshold() != settings.bigFilesLinesThreshold
                || languageBlacklist() != settings.languageBlacklist
                || rainbowifyPythonKeywords() != settings.rainbowifyPythonKeywords
                )

    init {
        loadSettings()
        doNOTRainbowifyBigFiles?.addActionListener({e -> bigFilesLinesThreshold?.setEnabled(doNOTRainbowifyBigFiles() ?: false)})
    }

    fun loadSettings() {
        enableRainbow?.isSelected = settings.isRainbowEnabled
        enableRainbowRoundBrackets?.isSelected = settings.isEnableRainbowRoundBrackets
        enableRainbowAngleBrackets?.isSelected = settings.isEnableRainbowAngleBrackets
        enableRainbowSquigglyBrackets?.isSelected = settings.isEnableRainbowSquigglyBrackets
        enableRainbowSquareBrackets?.isSelected = settings.isEnableRainbowSquareBrackets
        showRainbowIndentGuides?.isSelected = settings.isShowRainbowIndentGuides
        doNOTRainbowifyBracketsWithoutContent?.isSelected = settings.isDoNOTRainbowifyBracketsWithoutContent
        isDoNOTRainbowifyTheFirstLevel?.isSelected = settings.isDoNOTRainbowifyTheFirstLevel
        pressAnyKeyToRemoveTheHighlightingEffects?.isSelected = settings.pressAnyKeyToRemoveTheHighlightingEffects
        applyColorsOfRoundForAllBrackets?.isSelected = settings.applyColorsOfRoundForAllBrackets
        cycleCountOnAllBrackets?.isSelected = settings.cycleCountOnAllBrackets
        numberOfColors?.text = settings.numberOfColors.toString()
        disableRainbowIndentsInZenMode?.isSelected = settings.disableRainbowIndentsInZenMode
        useColorGenerator?.isSelected = settings.useColorGenerator
        rainbowifyTagNameInXML?.isSelected = settings.rainbowifyTagNameInXML
        doNOTRainbowifyTemplateString?.isSelected = settings.doNOTRainbowifyTemplateString
        doNOTRainbowifyBigFiles?.isSelected = settings.doNOTRainbowifyBigFiles
        bigFilesLinesThreshold?.text = settings.bigFilesLinesThreshold.toString()
        languageBlacklist?.text = settings.languageBlacklist.joinToString(",")
        rainbowifyPythonKeywords?.isSelected = settings.rainbowifyPythonKeywords
    }
}
