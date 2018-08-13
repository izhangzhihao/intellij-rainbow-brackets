package com.github.izhangzhihao.rainbow.brackets.settings.form

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.stateStore
import com.intellij.util.io.systemIndependentPath
import java.nio.file.Paths
import javax.swing.*
import javax.swing.border.EmptyBorder

class RainbowSettingsForm {
    private var panel: JPanel? = null
    private var appearancePanel: JPanel? = null
    private var enableRainbow: JCheckBox? = null
    private var enableRainbowRoundBrackets: JCheckBox? = null
    private var enableRainbowSquigglyBrackets: JCheckBox? = null
    private var enableRainbowSquareBrackets: JCheckBox? = null
    private var enableRainbowAngleBrackets: JCheckBox? = null
    private var doNOTRainbowifyBracketsWithoutContent: JCheckBox? = null
    private var version: JLabel? = null
    private var info: JTextArea? = null

    private val settings: RainbowSettings = RainbowSettings.instance

    fun component(): JComponent? = panel

    fun isRainbowEnabled() = enableRainbow?.isSelected

    fun isRainbowRoundBracketsEnabled() = enableRainbowRoundBrackets?.isSelected

    fun isRainbowSquigglyBracketsEnabled() = enableRainbowSquigglyBrackets?.isSelected

    fun isRainbowSquareBracketsEnabled() = enableRainbowSquareBrackets?.isSelected

    fun isRainbowAngleBracketsEnabled() = enableRainbowAngleBrackets?.isSelected

    fun isDoNOTRainbowifyBracketsWithoutContent() = doNOTRainbowifyBracketsWithoutContent?.isSelected

    val isModified: Boolean
        get() = (isRainbowEnabled() != settings.isRainbowEnabled
                || isRainbowAngleBracketsEnabled() != settings.isEnableRainbowAngleBrackets
                || isRainbowRoundBracketsEnabled() != settings.isEnableRainbowRoundBrackets
                || isRainbowSquigglyBracketsEnabled() != settings.isEnableRainbowSquigglyBrackets
                || isRainbowSquareBracketsEnabled() != settings.isEnableRainbowSquareBrackets
                || isDoNOTRainbowifyBracketsWithoutContent() != settings.isDoNOTRainbowifyBracketsWithoutContent)

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
        val emptyBorder = EmptyBorder(0, 10, 0, 0)
        version?.border = emptyBorder
        version?.text = "Version: ${settings.version}"
        info?.border = emptyBorder
        info?.text = """
            Config file path, for more info see:
            https://github.com/izhangzhihao/intellij-rainbow-brackets#config-brackets-colors.
            Path : ${configPath()}
            """.trimIndent()
    }

    private fun configPath(): String {
        val application = ApplicationManager.getApplication()
        val storageManager = application.stateStore.stateStorageManager
        val configPath = storageManager.expandMacros("\$APP_CONFIG\$") + "/rainbow_brackets.xml"
        return Paths.get(configPath).toAbsolutePath().systemIndependentPath
    }
}
