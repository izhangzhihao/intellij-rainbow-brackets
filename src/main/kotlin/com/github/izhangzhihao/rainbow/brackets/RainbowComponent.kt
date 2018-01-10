package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.specific.RainbowJava
import com.github.izhangzhihao.rainbow.brackets.specific.RainbowKotiln
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

            val javaLang: Language? = Language.findLanguageByID("JAVA")
            if (javaLang != null) {
                LanguageAnnotators.INSTANCE.addExplicitExtension(javaLang, RainbowJava())
            }

            val kotlinLang: Language? = Language.findLanguageByID("kotlin")
            if (kotlinLang != null) {
                LanguageAnnotators.INSTANCE.addExplicitExtension(kotlinLang, RainbowKotiln())
            }
        }
    }
}
