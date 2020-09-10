package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair

/***
 * [BracePairProvider.pairs] for the PSI elements should be a pair, so we could rainbow-ify them.
 * [BracePairProvider.blackList] for the PSI elements should NOT be a pair, so we won't rainbow-ify them.
 */
interface BracePairProvider {
    fun pairs(): List<BracePair> = emptyList()
    fun blackList(): List<BracePair> = emptyList()
}