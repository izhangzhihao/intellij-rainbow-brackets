package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import org.jetbrains.plugins.groovy.lang.lexer.GroovyTokenTypes.mGT
import org.jetbrains.plugins.groovy.lang.lexer.GroovyTokenTypes.mLT

class GroovyAngleBracketProvider : PairedBraceProvider {
    override val pairs: List<BracePair> = listOf(BracePair(mLT, mGT, false))
}