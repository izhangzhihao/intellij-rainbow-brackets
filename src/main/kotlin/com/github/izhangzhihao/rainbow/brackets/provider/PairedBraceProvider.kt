package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair

/***
 * [PairedBraceProvider.pairs] for the PSI elements should be a pair, so we could rainbow-ify them.
 * [PairedBraceProvider.blackList] for the PSI elements should NOT be a pair, so we won't rainbow-ify them.
 */
interface PairedBraceProvider {
    fun pairs(): List<BracePair>
    fun blackList(): List<BracePair> = emptyList()
}