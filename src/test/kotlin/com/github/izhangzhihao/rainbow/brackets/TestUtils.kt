package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.angleBrackets
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.roundBrackets
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.squareBrackets
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.squigglyBrackets

val brackets = roundBrackets + squareBrackets + squigglyBrackets + angleBrackets

fun CharSequence.toChar() = elementAt(0)

fun roundLevel(level: Int) = RainbowHighlighter.getRainbowColor(RainbowHighlighter.NAME_ROUND_BRACKETS, level)

fun squigglyLevel(level: Int) = RainbowHighlighter.getRainbowColor(RainbowHighlighter.NAME_SQUIGGLY_BRACKETS, level)

fun angleLevel(level: Int) = RainbowHighlighter.getRainbowColor(RainbowHighlighter.NAME_ANGLE_BRACKETS, level)

fun squareLevel(level: Int) = RainbowHighlighter.getRainbowColor(RainbowHighlighter.NAME_SQUARE_BRACKETS, level)