package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.php.lang.lexer.PhpTokenTypes

// https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/423
class PHPBracePairProvider : BracePairProvider {
    override fun blackList(): List<BracePair> = listOf(
            BracePair(PhpTokenTypes.PHP_OPENING_TAG, PhpTokenTypes.PHP_CLOSING_TAG, false),
            BracePair(PhpTokenTypes.PHP_ECHO_OPENING_TAG, PhpTokenTypes.PHP_CLOSING_TAG, false)
    )
}