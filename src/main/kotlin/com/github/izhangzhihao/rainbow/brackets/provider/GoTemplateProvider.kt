package com.github.izhangzhihao.rainbow.brackets.provider

import com.goide.template.GoTemplateTypes.*
import com.intellij.lang.BracePair

class GoTemplateProvider : BracePairProvider {
    override fun pairs(): List<BracePair> = listOf(
            BracePair(LDOUBLE_BRACE, RDOUBLE_BRACE, true),
            BracePair(LPAREN, RPAREN, true)
    )
}