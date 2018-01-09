package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.RainbowUtils.getBracketAttributes
import com.github.izhangzhihao.rainbow.brackets.RainbowUtils.getBracketLevel
import com.github.izhangzhihao.rainbow.brackets.RainbowUtils.settings
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement

class RainbowBrackets : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (settings.isRainbowEnabled && element is LeafPsiElement) {
            val level = getBracketLevel(element)
            if (level > 0) {
                val attrs = getBracketAttributes(level, element.text)
                holder.createInfoAnnotation(element as PsiElement, null).enforcedTextAttributes = attrs
            }
        }
    }
}