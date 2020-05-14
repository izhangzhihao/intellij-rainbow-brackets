package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.intellij.lang.javascript.JSTokenTypes.GT
import com.intellij.lang.javascript.JSTokenTypes.LT

class TSAngleBracketProvider : PairedBraceProvider {

    override val pairs: List<BracePair> = PAIRS

    companion object {
        private val PAIRS: List<BracePair> = listOf(BracePair(LT, GT, false))
    }
}