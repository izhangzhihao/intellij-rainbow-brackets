package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.lang.dart.DartTokenTypes.GT
import com.jetbrains.lang.dart.DartTokenTypes.LT

class DartBracePairProvider : BracePairProvider {
    override fun pairs(): List<BracePair> = listOf(BracePair(LT, GT, false))
}