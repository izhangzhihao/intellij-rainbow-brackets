package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.RainbowColors.angleBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.roundBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squareBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squigglyBracketsColor
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.containers.stream
import java.awt.Color
import java.awt.Font


class RainbowBrackets : Annotator {
    private val roundBrackets = arrayOf("(", ")")
    private val squigglyBrackets = arrayOf("{", "}")
    private val squareBrackets = arrayOf("[", "]")
    private val angleBrackets = arrayOf("<", ">", "</")

    private fun getAttributesColor(level: Int, bracket: String): Color {
        return when (bracket) {
            in roundBrackets -> dynamicallySelectColor(level, roundBracketsColor)
            in squigglyBrackets -> dynamicallySelectColor(level, squigglyBracketsColor)
            in squareBrackets -> dynamicallySelectColor(level, squareBracketsColor)
            in angleBrackets -> dynamicallySelectColor(level, angleBracketsColor)
            else -> dynamicallySelectColor(level, roundBracketsColor)
        }
    }

    private fun dynamicallySelectColor(level: Int, colors: Array<Color>): Color =
            colors[level % colors.size]

    private fun getBracketAttributes(level: Int, bracket: String): TextAttributes {
        val rainbowColor = getAttributesColor(level, bracket)
        return TextAttributes(rainbowColor, null, null, null, Font.PLAIN)
    }

    private fun containsBrackets(text: String, brackets: Array<String>): Boolean {
        return brackets
                .stream()
                .anyMatch { text.contains(it) }
    }

    private fun getBracketLevel(element: LeafPsiElement): Int {
        return when {
            element.text in roundBrackets -> getBracketLevel(element, roundBrackets)
            element.text in squigglyBrackets -> getBracketLevel(element, squigglyBrackets)
            element.text in squareBrackets -> getBracketLevel(element, squareBrackets)
            element.text in angleBrackets -> getBracketLevel(element, angleBrackets)
            else -> 0
        }
    }

    private fun getBracketLevel(psiElement: PsiElement, brackets: Array<String>): Int {
        var level = 0
        var eachParent: PsiElement? = psiElement
        while (eachParent != null) {
            if (containsBrackets(eachParent.text, brackets)) {
                level++
            }
            eachParent = eachParent.parent
        }
        return level
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is LeafPsiElement) {
            val level = getBracketLevel(element)
            if (level > 0) {
                val attrs = getBracketAttributes(level, element.text)
                holder.createInfoAnnotation(element as PsiElement, null).enforcedTextAttributes = attrs
            }
        }
    }
}