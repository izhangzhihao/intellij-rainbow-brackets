package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import org.jetbrains.plugins.ruby.rdoc.RDocTokenTypes.LPAREN
import org.jetbrains.plugins.ruby.rdoc.RDocTokenTypes.RPAREN

class RubyBracketsProvider : PairedBraceProvider {

    override val pairs: List<BracePair> = PAIRS

    companion object {
        private val PAIRS: List<BracePair> = listOf(BracePair(LPAREN, RPAREN, false))
    }
}