package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.RainbowColors.roundBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squareBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squigglyBracketsColor
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import java.awt.Color
import java.awt.Font


class RainbowBrackets : Annotator {
    private val bracketsList = arrayOf("(", ")", "{", "}", "[", "]")

    private fun getAttributesColor(level: Int, bracket: String): Color {
        return when (bracket) {
            "(", ")" -> dynamicallySelectColor(level, roundBracketsColor)
            "{", "}" -> dynamicallySelectColor(level, squigglyBracketsColor)
            "[", "]" -> dynamicallySelectColor(level, squareBracketsColor)
            else -> dynamicallySelectColor(level, roundBracketsColor)
        }
    }

    private fun dynamicallySelectColor(level: Int, colors: Array<Color>) =
            colors[level % colors.size]

    private fun getBracketAttributes(level: Int, bracket: String): TextAttributes {
        val rainbowColor = getAttributesColor(level, bracket)
        return TextAttributes(rainbowColor, null, null, null, Font.PLAIN)
    }

    private fun containsBrackets(text: String): Boolean {
        return text.contains("(")
                || text.contains(")")
                || text.contains("{")
                || text.contains("}")
                || text.contains("[")
                || text.contains("]")
    }

    private fun visitParent(element: PsiElement, pred: (PsiElement) -> Boolean): Boolean {
        var result = false
        var eachParent: PsiElement? = element
        while (eachParent != null) {
            if (pred(eachParent)) {
                result = true
            }
            eachParent = eachParent.parent
        }
        return result
    }

    private fun getBracketLevel(psiElement: PsiElement): Int {
        var level = -1
        var eachParent: PsiElement? = psiElement
        while (eachParent != null) {
            if (containsBrackets(eachParent.text)) {
                level++
            }
            eachParent = eachParent.parent
        }
        return level
    }

    private fun isString(t: String): Boolean {
        return t.startsWith("\"") && t.endsWith("\"") || t.startsWith("\'") && t.endsWith("\'")
    }

    private val isString = { element: PsiElement -> visitParent(element, { e -> isString(e.text) }) }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is LeafPsiElement
                && bracketsList.contains(element.text)
                && !isString(element)) {
            val level = getBracketLevel(element)
            val attrs = getBracketAttributes(level, element.text)
            holder.createInfoAnnotation(element as PsiElement, null).enforcedTextAttributes = attrs
        }
    }
}