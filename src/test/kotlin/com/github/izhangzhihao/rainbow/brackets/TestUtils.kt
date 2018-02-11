package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.angleBrackets
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.roundBrackets
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.squareBrackets
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.squigglyBrackets

val brackets = roundBrackets + squareBrackets + squigglyBrackets + angleBrackets

fun CharSequence.toChar() = elementAt(0)