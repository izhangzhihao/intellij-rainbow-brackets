package com.github.izhangzhihao.rainbow.brackets

import com.intellij.lang.Language
import com.intellij.lang.LanguageAnnotators
import com.intellij.openapi.components.ApplicationComponent

class RainbowComponent : ApplicationComponent {
    override fun initComponent() {
        Companion.registerAnnotatorForAllLanguages()
    }

    companion object {

        private fun registerAnnotatorForAllLanguages() {
            Language.getRegisteredLanguages().forEach { lang -> LanguageAnnotators.INSTANCE.addExplicitExtension(lang, RainbowBrackets()) }
        }
    }
}
