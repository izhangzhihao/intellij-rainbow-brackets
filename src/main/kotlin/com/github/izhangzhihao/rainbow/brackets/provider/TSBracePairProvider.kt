package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.intellij.lang.javascript.JSTokenTypes.GT
import com.intellij.lang.javascript.JSTokenTypes.LT

class TSBracePairProvider : BracePairProvider {
    override fun pairs(): List<BracePair> = listOf(BracePair(LT, GT, false))
}