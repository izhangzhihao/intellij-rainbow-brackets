package com.github.izhangzhihao.rainbow.brackets

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import java.awt.Color
import java.awt.Font
import java.util.regex.Pattern


class RainbowHighliter : Annotator {
    private val bracketsList = arrayOf("(", ")", "{", "}", "[", "]")

    private val haskellMultilineCommentPattern = Pattern.compile(
            "\\{-.*?-\\}"
    )
    private val normalMultilineCommentPattern = Pattern.compile(
            "/\\*.*?\\*/"
    )

    private fun getAttributesColor(selector: Int, bracket: String): Color {
        return when (bracket) {
            "(" -> RainbowColors.roundBracketsColor[selector % RainbowColors.roundBracketsColor.size]
            ")" -> RainbowColors.roundBracketsColor[selector % RainbowColors.roundBracketsColor.size]
            "{" -> RainbowColors.squigglyBracketsColor[selector % RainbowColors.squigglyBracketsColor.size]
            "}" -> RainbowColors.squigglyBracketsColor[selector % RainbowColors.squigglyBracketsColor.size]
            "[" -> RainbowColors.squareBracketsColor[selector % RainbowColors.squareBracketsColor.size]
            "]" -> RainbowColors.squareBracketsColor[selector % RainbowColors.squareBracketsColor.size]
            else -> {
                RainbowColors.roundBracketsColor[selector % RainbowColors.roundBracketsColor.size]
            }
        }
    }

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

    private val isMultilineComment = { element: PsiElement, pattern: Pattern ->
        val matcher = pattern.matcher(element.containingFile.text.replace("\n", " "))
        var isInMultiLineComment = false
        while (matcher.find()) {
            isInMultiLineComment = matcher.start() <= element.textOffset && element.textOffset <= matcher.end()
            if (isInMultiLineComment) break
        }
        isInMultiLineComment
    }

    private val isHaskellMultilineComment = { element: PsiElement -> isMultilineComment(element, haskellMultilineCommentPattern) }

    private val isNormalMultilineComment = { element: PsiElement -> isMultilineComment(element, normalMultilineCommentPattern) }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val languageID = element.language.id
        if (element is LeafPsiElement
                && bracketsList.contains(element.text)
                && languageID != "Clojure"
                && !isString(element)
                && !isHaskellMultilineComment(element)
                && !isNormalMultilineComment(element)) {
            val level = getBracketLevel(element)
            val attrs = getBracketAttributes(level, element.text)
            holder.createInfoAnnotation(element as PsiElement, null).enforcedTextAttributes = attrs
        }
    }
}