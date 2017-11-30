package com.github.izhangzhihao.rainbow.brackets.settings

import com.github.izhangzhihao.rainbow.brackets.RainbowBrackets
import com.intellij.lang.Language
import com.intellij.lang.LanguageAnnotators
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import org.jetbrains.annotations.Nullable


@State(name = "RainbowSettings", storages = arrayOf(Storage(id = "rainbow_brackets", file = "\$APP_CONFIG$/rainbow_brackets.xml")))
class RainbowSettings : PersistentStateComponent<RainbowSettings> {
    /**
     * default value
     */
    var isRainbowEnabled = true
    var isRainbowHTMLEnabled = true
    var isEnableRainbowBracketsForAnyLanguages = false
    var isEnableRainbowRoundBrackets = true
    var isEnableRainbowSquigglyBrackets = true
    var isEnableRainbowSquareBrackets = true
    var isEnableRainbowAngleBrackets = true

    @Nullable
    override fun getState() = this

    override fun loadState(state: RainbowSettings) {
        if (state.isEnableRainbowBracketsForAnyLanguages) {
            registerAnnotatorForAnyLanguages()
        }
        if (!state.isRainbowHTMLEnabled) {
            disableHTMLSupport()
        }
        copyBean(state, this)
    }

    companion object {
        val instance: RainbowSettings
            get() = getService(RainbowSettings::class.java)

        private fun registerAnnotatorForAnyLanguages() {
            Language.getRegisteredLanguages().forEach { lang -> LanguageAnnotators.INSTANCE.addExplicitExtension(lang, RainbowBrackets()) }
        }

        private fun disableHTMLSupport() {
            LanguageAnnotators.INSTANCE.removeExplicitExtension(Language.findLanguageByID("XML")!!, RainbowBrackets())
            LanguageAnnotators.INSTANCE.removeExplicitExtension(Language.findLanguageByID("HTML")!!, RainbowBrackets())
        }
    }
}