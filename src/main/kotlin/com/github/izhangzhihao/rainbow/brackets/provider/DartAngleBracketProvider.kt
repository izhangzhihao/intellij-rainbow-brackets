package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.lang.dart.DartTokenTypes.GT
import com.jetbrains.lang.dart.DartTokenTypes.LT

class DartAngleBracketProvider : PairedBraceProvider {

    override val pairs: List<BracePair> = PAIRS

    companion object {
        private val PAIRS: List<BracePair> = listOf(BracePair(LT, GT, false))
    }
}