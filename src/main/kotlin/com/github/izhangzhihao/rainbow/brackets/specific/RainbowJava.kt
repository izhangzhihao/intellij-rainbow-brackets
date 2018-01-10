package com.github.izhangzhihao.rainbow.brackets.specific

import com.github.izhangzhihao.rainbow.brackets.RainbowColors.roundBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squareBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squigglyBracketsColor
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.JavaTokenType.*
import com.intellij.psi.PsiElement

class RainbowJava : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        annotateUtil(element, holder, LPARENTH, RPARENTH, roundBracketsColor)
        annotateUtil(element, holder, LBRACKET, RBRACKET, squareBracketsColor)
        annotateUtil(element, holder, LBRACE, RBRACE, squigglyBracketsColor)
    }
}