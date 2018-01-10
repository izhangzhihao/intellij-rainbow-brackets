package com.github.izhangzhihao.rainbow.brackets.specific

import com.github.izhangzhihao.rainbow.brackets.RainbowColors.roundBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squareBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squigglyBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowUtils.settings
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens.*


/**
 * From https://github.com/JetBrains/kotlin/blob/master/compiler/frontend/src/org/jetbrains/kotlin/lexer/KtTokens.java
 * LBRACKET [
 * RBRACKET ]
 * LBRACE {
 * RBRACE }
 * LPAR (
 * RPAR )
 */
class RainbowKotlin : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (settings.isRainbowEnabled) {
            if (settings.isEnableRainbowRoundBrackets) {
                annotateUtil(element, holder, LPAR, RPAR, roundBracketsColor)
            }
            if (settings.isEnableRainbowSquareBrackets) {
                annotateUtil(element, holder, LBRACKET, RBRACKET, squareBracketsColor)
            }
            if (settings.isEnableRainbowSquigglyBrackets) {
                annotateUtil(element, holder, LBRACE, RBRACE, squigglyBracketsColor)
            }
        }
    }
}