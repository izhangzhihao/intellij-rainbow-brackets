package com.github.izhangzhihao.rainbow.brackets.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import org.jetbrains.annotations.Nullable


@State(name = "RainbowSettings", storages = arrayOf(Storage(id = "rainbow_brackets", file = "\$APP_CONFIG$/rainbow_brackets.xml")))
class RainbowSettings : PersistentStateComponent<RainbowSettings> {
    var isRainbowEnabled = true
    var isRainbowHTMLEnabled = true

    @Nullable
    override fun getState() = this

    override fun loadState(state: RainbowSettings) = copyBean(state, this)

    companion object {
        val instance: RainbowSettings
            get() = getService(RainbowSettings::class.java)
    }
}