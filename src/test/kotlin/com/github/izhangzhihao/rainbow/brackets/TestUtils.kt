package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.angleBrackets
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.angleBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.roundBracketsColors
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.squareBracketsColors
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.squigglyBracketsColors
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.roundBrackets
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.squareBrackets
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.squigglyBrackets
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.getColor

val brackets = roundBrackets + squareBrackets + squigglyBrackets + angleBrackets

fun CharSequence.toChar() = elementAt(0)

fun roundLevel(level: Int) = roundBracketsColors.getColor(level)

fun squigglyLevel(level: Int) = squigglyBracketsColors.getColor(level)

fun angleLevel(level: Int) = angleBracketsColor.getColor(level)

fun squareLevel(level: Int) = squareBracketsColors.getColor(level)