package com.github.izhangzhihao.rainbow.brackets

import com.intellij.lang.Language
import com.intellij.lang.LanguageAnnotators
import com.intellij.openapi.components.ApplicationComponent

class RainbowComponent : ApplicationComponent {
    override fun initComponent() {
        Companion.registerAnnotatorForAllLanguages()
    }

    companion object {

        private val specLangList = arrayOf("JAVA", "kotlin")

        private fun registerAnnotatorForAllLanguages() {
            Language.getRegisteredLanguages()
                    .filter { lang -> !specLangList.contains(lang.id) }
                    .forEach { lang -> LanguageAnnotators.INSTANCE.addExplicitExtension(lang, RainbowBrackets()) }
        }
    }
}
