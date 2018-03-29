package com.github.izhangzhihao.rainbow.brackets.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import org.jetbrains.annotations.Nullable


@State(name = "RainbowSettings", storages = [(Storage(id = "rainbow_brackets", file = "\$APP_CONFIG$/rainbow_brackets.xml"))])
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
    var version: String? = null

    var lightRoundBracketsColors = arrayOf(
            0xE66A01,
            0x1B5E20,
            0x006BE7,
            0x283593,
            0x004D40
    )

    var darkRoundBracketsColors = arrayOf(
            0xE6B422,
            0x43A047,
            0x2196F3,
            0x3F51B5,
            0x00897B
    )

    var lightSquareBracketsColors = arrayOf(
            0x0B9087,
            0x827717,
            0x6444E6
    )

    var darkSquareBracketsColors = arrayOf(
            0x33CCFF,
            0xD4E157,
            0x8080FF
    )

    var lightSquigglyBracketsColors = arrayOf(
            0x0057D2,
            0x558B2F,
            0xFF6D27
    )

    var darkSquigglyBracketsColors = arrayOf(
            0x1976D2,
            0x8BC34A,
            0xFF9863
    )

    var lightAngleBracketsColor = lightRoundBracketsColors
    var darkAngleBracketsColor = darkRoundBracketsColors

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