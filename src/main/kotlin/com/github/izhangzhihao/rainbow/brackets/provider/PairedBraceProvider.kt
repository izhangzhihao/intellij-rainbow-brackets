package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair

interface PairedBraceProvider {
    val pairs: List<BracePair>
}