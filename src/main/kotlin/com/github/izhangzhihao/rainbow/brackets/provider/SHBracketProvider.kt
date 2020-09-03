package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.intellij.sh.ShTypes

class SHBracketProvider : PairedBraceProvider {
    override val pairs: List<BracePair> = emptyList()
    override val blackList: List<BracePair> = listOf(
            BracePair(ShTypes.HEREDOC_MARKER_START, ShTypes.HEREDOC_MARKER_END, false),
            BracePair(ShTypes.DO, ShTypes.DONE, true),
            BracePair(ShTypes.IF, ShTypes.FI, true),
            BracePair(ShTypes.CASE, ShTypes.ESAC, true)
    )
}