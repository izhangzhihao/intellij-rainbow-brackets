package com.github.izhangzhihao.rainbow.brackets.specific

import com.github.izhangzhihao.rainbow.brackets.RainbowColors.roundBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squareBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squigglyBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowUtils.settings
import com.github.izhangzhihao.rainbow.brackets.specific.RainbowUtils.annotateUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement

class RainbowJS : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (settings.isRainbowEnabled && element is LeafPsiElement) {
            if (settings.isEnableRainbowRoundBrackets) annotateUtil(element, holder, "JS:LPAR", "JS:RPAR", roundBracketsColor)
            if (settings.isEnableRainbowSquareBrackets) annotateUtil(element, holder, "JS:LBRACKET", "JS:RBRACKET", squareBracketsColor)
            if (settings.isEnableRainbowSquigglyBrackets) annotateUtil(element, holder, "JS:LBRACE", "JS:RBRACE", squigglyBracketsColor)
        }
    }
}