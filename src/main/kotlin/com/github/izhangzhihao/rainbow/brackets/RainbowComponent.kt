package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.RainbowUtils.registerAnnotatorForAllLanguages
import com.intellij.openapi.components.ApplicationComponent

class RainbowComponent : ApplicationComponent {
    override fun initComponent() {
        registerAnnotatorForAllLanguages()
    }
}
