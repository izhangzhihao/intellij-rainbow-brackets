package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import org.jetbrains.plugins.groovy.lang.lexer.GroovyTokenTypes.mGT
import org.jetbrains.plugins.groovy.lang.lexer.GroovyTokenTypes.mLT

class GroovyBracePairProvider : BracePairProvider {
    override fun pairs(): List<BracePair> = listOf(BracePair(mLT, mGT, false))
}