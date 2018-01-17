package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.RainbowColors.angleBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.roundBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squareBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squigglyBracketsColor
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.lang.Language
import com.intellij.lang.LanguageAnnotators
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import java.awt.Color
import java.awt.Font

object RainbowUtils {

    val settings = RainbowSettings.instance

    private val roundBrackets = arrayOf("(", ")")
    private val squigglyBrackets = arrayOf("{", "}")
    private val squareBrackets = arrayOf("[", "]")
    private val angleBrackets = arrayOf("<", ">", "</", "/>")

    fun dynamicallySelectColor(level: Int, colors: Array<Color>) =
            colors[level % colors.size]

    private fun containsBrackets(text: String, brackets: Array<String>) =
            brackets.any { text.contains(it) }

    private fun getAttributesColor(level: Int, bracket: String) =
            when (bracket) {
                in roundBrackets -> dynamicallySelectColor(level, roundBracketsColor)
                in squigglyBrackets -> dynamicallySelectColor(level, squigglyBracketsColor)
                in squareBrackets -> dynamicallySelectColor(level, squareBracketsColor)
                in angleBrackets -> dynamicallySelectColor(level, angleBracketsColor)
                else -> dynamicallySelectColor(level, roundBracketsColor)
            }

    private fun getBracketAttributes(level: Int, bracket: String) =
            TextAttributes(getAttributesColor(level, bracket), null, null, null, Font.PLAIN)

    private fun getBracketLevel(element: LeafPsiElement) =
            when {
                settings.isEnableRainbowRoundBrackets && element.text in roundBrackets -> getBracketLevel(element, roundBrackets)
                settings.isEnableRainbowSquigglyBrackets && element.text in squigglyBrackets -> getBracketLevel(element, squigglyBrackets)
                settings.isEnableRainbowSquareBrackets && element.text in squareBrackets -> getBracketLevel(element, squareBrackets)
                settings.isEnableRainbowAngleBrackets && element.text in angleBrackets -> getBracketLevel(element, angleBrackets)
                else -> 0
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

    private val specLangList = arrayOf("JAVA", "kotlin", "Scala", "JavaScript", "PHP")

    fun registerAnnotatorForAllLanguages() {
        Language.getRegisteredLanguages()
                .filter { lang -> !specLangList.contains(lang.id) }
                .forEach { lang -> LanguageAnnotators.INSTANCE.addExplicitExtension(lang, RainbowBrackets()) }
    }

    fun annotateUtil(element: LeafPsiElement, holder: AnnotationHolder) {
        val level = getBracketLevel(element)
        if (level > 0) {
            val attrs = getBracketAttributes(level, element.text)
            holder.createInfoAnnotation(element.psi, null).enforcedTextAttributes = attrs
        }
    }
}