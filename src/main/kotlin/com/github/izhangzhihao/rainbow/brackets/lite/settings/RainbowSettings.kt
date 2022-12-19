package com.github.izhangzhihao.rainbow.brackets.lite.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import org.jetbrains.annotations.Nullable


@State(name = "RainbowLiteSettings", storages = [(Storage("rainbow_brackets_lite.xml"))])
class RainbowSettings : PersistentStateComponent<RainbowSettings> {
    /**
     * default value
     */
    var isRainbowEnabled = true
    var isEnableRainbowRoundBrackets = true
    var isEnableRainbowSquigglyBrackets = true
    var isEnableRainbowSquareBrackets = true
    var isEnableRainbowAngleBrackets = true
    var isShowRainbowIndentGuides = true

    var numberOfColors = 5

    var doNOTRainbowifyBigFiles = true
    var bigFilesLinesThreshold = 1000


    var suppressDisabledCheck = false
    var suppressBigFileCheck = false
    var suppressBlackListCheck = false

    @Nullable
    override fun getState() = this

    override fun loadState(state: RainbowSettings) {
        copyBean(state, this)
    }

    companion object {
        val instance: RainbowSettings
            get() = ApplicationManager.getApplication().getService(RainbowSettings::class.java)
    }
}