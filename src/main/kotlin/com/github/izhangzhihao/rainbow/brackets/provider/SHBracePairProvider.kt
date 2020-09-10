package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.intellij.sh.ShTypes

class SHBracePairProvider : BracePairProvider {
    override fun blackList(): List<BracePair> = listOf(
            BracePair(ShTypes.HEREDOC_MARKER_START, ShTypes.HEREDOC_MARKER_END, false),
            BracePair(ShTypes.DO, ShTypes.DONE, true),
            BracePair(ShTypes.IF, ShTypes.FI, true),
            BracePair(ShTypes.CASE, ShTypes.ESAC, true)
    )
}