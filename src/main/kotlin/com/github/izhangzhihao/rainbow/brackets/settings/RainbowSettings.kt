package com.github.izhangzhihao.rainbow.brackets.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import org.jetbrains.annotations.Nullable


@State(name = "RainbowSettings", storages = [(Storage("rainbow_brackets.xml"))])
class RainbowSettings : PersistentStateComponent<RainbowSettings> {
    /**
     * default value
     */
    var isRainbowEnabled = true
    var isEnableRainbowRoundBrackets = true
    var isEnableRainbowSquigglyBrackets = true
    var isEnableRainbowSquareBrackets = true
    var isEnableRainbowAngleBrackets = true
    var isDoNOTRainbowifyBracketsWithoutContent = false
    var version = "Unknown"
    var isRainbowifyHTMLInsideJS = true
    var isRainbowifyKotlinLabel = true
    var isOverrideMatchedBraceAttributes = true

    var lightRoundBracketsColors: Array<String>? = null
    var darkRoundBracketsColors: Array<String>? = null
    var lightSquareBracketsColors: Array<String>? = null
    var darkSquareBracketsColors: Array<String>? = null
    var lightSquigglyBracketsColors: Array<String>? = null
    var darkSquigglyBracketsColors: Array<String>? = null
    var lightAngleBracketsColor: Array<String>? = null
    var darkAngleBracketsColor: Array<String>? = null

    @Nullable
    override fun getState() = this

    override fun loadState(state: RainbowSettings) {
        copyBean(state, this)
    }

    companion object {
        val instance: RainbowSettings
            get() = getService(RainbowSettings::class.java)
    }
}