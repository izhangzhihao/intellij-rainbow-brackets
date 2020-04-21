package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.cidr.lang.parser.OCTokenTypes

class OCAngleBracketProvider : PairedBraceProvider {
    override val pairs: List<BracePair> = PAIRS

    companion object {
        private val PAIRS: List<BracePair> = listOf(BracePair(OCTokenTypes.LT, OCTokenTypes.GT, false))
    }
}