package com.github.izhangzhihao.rainbow.brackets.annotator

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.NAME_ANGLE_BRACKETS
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.NAME_ROUND_BRACKETS
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.NAME_SQUARE_BRACKETS
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.NAME_SQUIGGLY_BRACKETS
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.getRainbowColorByLevel
import com.github.izhangzhihao.rainbow.brackets.annotator.RainbowUtils.annotateUtil
import com.github.izhangzhihao.rainbow.brackets.annotator.RainbowUtils.settings
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement

class RainbowAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (settings.isRainbowEnabled && element is LeafPsiElement) {
            if (!settings.applyColorsOfRoundForAllBrackets) {
                if (settings.isEnableRainbowRoundBrackets) annotateUtil(element, holder, "(", ")", NAME_ROUND_BRACKETS)
                if (settings.isEnableRainbowSquareBrackets) annotateUtil(element, holder, "[", "]", NAME_SQUARE_BRACKETS)
                if (settings.isEnableRainbowSquigglyBrackets) annotateUtil(element, holder, "{", "}", NAME_SQUIGGLY_BRACKETS)
                if (settings.isEnableRainbowAngleBrackets) annotateUtil(element, holder, "<", ">", NAME_ANGLE_BRACKETS)
            } else {
                if (settings.isEnableRainbowRoundBrackets) annotateUtil(element, holder, "(", ")", NAME_ROUND_BRACKETS)
                if (settings.isEnableRainbowSquareBrackets) annotateUtil(element, holder, "[", "]", NAME_ROUND_BRACKETS)
                if (settings.isEnableRainbowSquigglyBrackets) annotateUtil(element, holder, "{", "}", NAME_ROUND_BRACKETS)
                if (settings.isEnableRainbowAngleBrackets) annotateUtil(element, holder, "<", ">", NAME_ROUND_BRACKETS)
            }
        }
    }
}


object RainbowUtils {

    private val leftBracketsSet = setOf("(", "[", "{", "<")
    private val rightBracketsSet = setOf(")", "]", "}", ">")

    val settings = RainbowSettings.instance

    private fun getBracketLevel(element: LeafPsiElement, LEFT: String, RIGHT: String): Int {
        //Using `element.elementType.toString()` if we didn't want add more dependencies.
        var level = if (element.text == RIGHT) 0 else -1
        tailrec fun iterateParents(currentNode: PsiElement) {
            tailrec fun iterateChildren(currentChild: PsiElement) {
                if (currentChild is LeafPsiElement) {
                    //Using `currentChild.elementType.toString()` if we didn't want add more dependencies.
                    if (!settings.cycleCountOnAllBrackets) {
                        when (currentChild.text) {
                            LEFT -> level++
                            RIGHT -> level--
                        }
                    } else {
                        if (leftBracketsSet.contains(currentChild.text)) {
                            level++
                        } else if (rightBracketsSet.contains(currentChild.text)) {
                            level--
                        }
                    }
                }
                if ((currentChild != currentNode) && (currentChild != currentNode.parent.lastChild)) {
                    iterateChildren(currentChild.nextSibling)
                }
            }
            if (currentNode.parent !is PsiFile) {
                iterateChildren(currentNode.parent.firstChild)
                iterateParents(currentNode.parent)
            }
        }
        iterateParents(element)
        return level
    }

    fun annotateUtil(element: LeafPsiElement, holder: AnnotationHolder,
                     LEFT: String, RIGHT: String, rainbowName: String) {
        //Using `element.elementType.toString()` if we didn't want add more dependencies.
        val level = when (element.text) {
            LEFT, RIGHT -> getBracketLevel(element, LEFT, RIGHT)
            else -> -1
        }
        val scheme = EditorColorsManager.getInstance().globalScheme
        if (RainbowSettings.instance.isDoNOTRainbowifyTheFirstLevel) {
            if (level >= 1) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(element.psi)
                        .textAttributes(getRainbowColorByLevel(scheme, rainbowName, level))
                        .create()
            }
        } else {
            if (level >= 0) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(element.psi)
                        .textAttributes(getRainbowColorByLevel(scheme, rainbowName, level))
                        .create()
            }
        }
    }
}