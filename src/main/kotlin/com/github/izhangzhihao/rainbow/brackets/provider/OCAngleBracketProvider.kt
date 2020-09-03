package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.cidr.lang.parser.OCTokenTypes

class OCAngleBracketProvider : PairedBraceProvider {
    override val pairs: List<BracePair> = listOf(BracePair(OCTokenTypes.LT, OCTokenTypes.GT, false))
    override val blackList: List<BracePair> = emptyList()
}