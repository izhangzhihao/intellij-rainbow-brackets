package com.github.izhangzhihao.rainbow.brackets.specific

import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squareBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.roundBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squigglyBracketsColor
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
class RainbowKotiln : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        annotateUtil(element, holder, LPAR, RPAR, roundBracketsColor)
        annotateUtil(element, holder, LBRACKET, RBRACKET, squareBracketsColor)
        annotateUtil(element, holder, LBRACE, RBRACE, squigglyBracketsColor)
    }
}