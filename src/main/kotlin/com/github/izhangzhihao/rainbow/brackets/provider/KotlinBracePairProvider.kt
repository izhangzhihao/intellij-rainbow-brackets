package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import org.jetbrains.kotlin.lexer.KtTokens.GT
import org.jetbrains.kotlin.lexer.KtTokens.LT


class KotlinBracePairProvider : BracePairProvider {
    override fun pairs(): List<BracePair> = listOf(BracePair(LT, GT, false))
}