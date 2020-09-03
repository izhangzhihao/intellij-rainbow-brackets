package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.lang.dart.DartTokenTypes.GT
import com.jetbrains.lang.dart.DartTokenTypes.LT

class DartAngleBracketProvider : PairedBraceProvider {
    override val pairs: List<BracePair> = listOf(BracePair(LT, GT, false))
    override val blackList: List<BracePair> = emptyList()
}