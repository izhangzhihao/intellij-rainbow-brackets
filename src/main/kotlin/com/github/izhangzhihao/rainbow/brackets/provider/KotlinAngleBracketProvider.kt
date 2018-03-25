package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import org.jetbrains.kotlin.lexer.KtTokens.GT
import org.jetbrains.kotlin.lexer.KtTokens.LT

/**
 * KotlinAngleBracketProvider
 *
 * Created by Yii.Guxing on 2018/1/30
 */
class KotlinAngleBracketProvider : PairedBraceProvider {

    override val pairs: List<BracePair> = PAIRS

    companion object {
        private val PAIRS: List<BracePair> = listOf(BracePair(LT, GT, false))
    }
}